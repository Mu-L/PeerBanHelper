package com.ghostchu.peerbanhelper.btn.ability.impl;

import com.ghostchu.peerbanhelper.Main;
import com.ghostchu.peerbanhelper.btn.BtnNetwork;
import com.ghostchu.peerbanhelper.btn.BtnRuleset;
import com.ghostchu.peerbanhelper.btn.BtnRulesetParsed;
import com.ghostchu.peerbanhelper.btn.ability.AbstractBtnAbility;
import com.ghostchu.peerbanhelper.event.BtnRuleUpdateEvent;
import com.ghostchu.peerbanhelper.text.Lang;
import com.ghostchu.peerbanhelper.text.TranslationComponent;
import com.ghostchu.peerbanhelper.util.URLUtil;
import com.ghostchu.peerbanhelper.util.json.JsonUtil;
import com.ghostchu.peerbanhelper.util.rule.matcher.IPMatcher;
import com.ghostchu.peerbanhelper.util.scriptengine.ScriptEngine;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.ghostchu.peerbanhelper.text.TextManager.tlUI;

@Slf4j
public final class BtnAbilityRules extends AbstractBtnAbility {
    private final BtnNetwork btnNetwork;
    private final long interval;
    private final String endpoint;
    private final long randomInitialDelay;
    private final File btnCacheFile = new File(Main.getDataDirectory(), "btn.cache");
    private final ScriptEngine scriptEngine;
    private final boolean scriptExecute;
    @Getter
    private BtnRulesetParsed btnRule;


    public BtnAbilityRules(BtnNetwork btnNetwork, ScriptEngine scriptEngine, JsonObject ability, boolean scriptExecute) {
        this.btnNetwork = btnNetwork;
        this.scriptEngine = scriptEngine;
        this.interval = ability.get("interval").getAsLong();
        this.endpoint = ability.get("endpoint").getAsString();
        this.randomInitialDelay = ability.get("random_initial_delay").getAsLong();
        this.scriptExecute = scriptExecute;
        setLastStatus(true, new TranslationComponent(Lang.BTN_STAND_BY));
    }

    private void loadCacheFile() throws IOException {
        if (!btnCacheFile.exists()) {
            if (!btnCacheFile.getParentFile().exists()) {
                btnCacheFile.getParentFile().mkdirs();
            }
            btnCacheFile.createNewFile();
        } else {
            try {
                BtnRuleset btnRuleset = JsonUtil.getGson().fromJson(Files.readString(btnCacheFile.toPath()), BtnRuleset.class);
                this.btnRule = new BtnRulesetParsed(scriptEngine, btnRuleset, scriptExecute);
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public String getName() {
        return "BtnAbilityRuleset";
    }

    @Override
    public TranslationComponent getDisplayName() {
        return new TranslationComponent(Lang.BTN_ABILITY_RULES);
    }

    @Override
    public TranslationComponent getDescription() {
        if(btnRule == null){
            return new TranslationComponent(Lang.BTN_ABILITY_RULES_DESCRIPTION, "N/A", 0, 0, 0, 0, 0);
        }
        return new TranslationComponent(Lang.BTN_ABILITY_RULES_DESCRIPTION,
                btnRule.getVersion(),
                btnRule.size(),
                btnRule.getIpRules().values().stream().mapToLong(IPMatcher::size).sum(),
                btnRule.getPeerIdRules().values().stream().mapToLong(List::size).sum(),
                btnRule.getClientNameRules().values().stream().mapToLong(List::size).sum(),
                btnRule.getPortRules().values().stream().mapToLong(List::size).sum(),
                btnRule.getScriptRules().size());
    }

    @Override
    public void load() {
        try {
            loadCacheFile();
            setLastStatus(true, new TranslationComponent(Lang.BTN_RULES_LOADED_FROM_CACHE));
        } catch (Exception e) {
            log.error(tlUI(Lang.BTN_RULES_LOAD_FROM_CACHE_FAILED));
            setLastStatus(false, new TranslationComponent(e.getClass().getName() + ": " + e.getMessage()));
        }
        btnNetwork.getScheduler().scheduleWithFixedDelay(this::updateRule, ThreadLocalRandom.current().nextLong(randomInitialDelay), interval, TimeUnit.MILLISECONDS);
    }

    private void updateRule() {
        String version;
        if (btnRule == null || btnRule.getVersion() == null) {
            version = "initial";
        } else {
            version = btnRule.getVersion();
        }
        
        String url = URLUtil.appendUrl(endpoint, Map.of("rev", version));
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
                
        try (Response response = btnNetwork.getHttpClient().newCall(request).execute()) {
            if (response.code() == 204) {
                setLastStatus(true, new TranslationComponent(Lang.BTN_RULES_LOADED_FROM_REMOTE, this.btnRule.getVersion()));
                return;
            }
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                log.error(tlUI(Lang.BTN_REQUEST_FAILS, response.code() + " - " + responseBody));
                setLastStatus(false, new TranslationComponent(Lang.BTN_HTTP_ERROR, response.code(), responseBody));
            } else {
                try {
                    String responseBody = response.body().string();
                    BtnRuleset btr = JsonUtil.getGson().fromJson(responseBody, BtnRuleset.class);
                    this.btnRule = new BtnRulesetParsed(scriptEngine, btr, scriptExecute);
                    Main.getEventBus().post(new BtnRuleUpdateEvent());
                    try {
                        Files.writeString(btnCacheFile.toPath(), responseBody, StandardCharsets.UTF_8);
                    } catch (IOException ignored) {
                    }
                    log.info(tlUI(Lang.BTN_UPDATE_RULES_SUCCESSES, this.btnRule.getVersion()));
                    setLastStatus(true, new TranslationComponent(Lang.BTN_RULES_LOADED_FROM_REMOTE, this.btnRule.getVersion()));
                    btnNetwork.getModuleMatchCache().invalidateAll();
                } catch (JsonSyntaxException e) {
                    String responseBody = response.body().string();
                    setLastStatus(false, new TranslationComponent("JsonSyntaxException: " + response.code() + " - " + responseBody));
                    log.error("Unable to parse BtnRule as a valid Json object: {}-{}", response.code(), responseBody, e);
                }
            }
        } catch (Exception e) {
            log.error(tlUI(Lang.BTN_REQUEST_FAILS), e);
            setLastStatus(false, new TranslationComponent(Lang.BTN_UNKNOWN_ERROR, e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    @Override
    public void unload() {

    }
}
