package com.ghostchu.peerbanhelper.gui;

import com.ghostchu.peerbanhelper.PeerBanHelper;
import com.ghostchu.peerbanhelper.gui.impl.GuiImpl;
import org.slf4j.event.Level;


public class PBHGuiManager implements GuiManager {
    private final GuiImpl gui;

    public PBHGuiManager(GuiImpl gui) {
        this.gui = gui;
    }

    @Override
    public void setup() {
        gui.setup();
    }

    @Override
    public boolean isDarkMode() {
        return gui.isDarkMode();
    }

    @Override
    public String getName() {
        return gui.getName();
    }

    @Override
    public boolean isGuiAvailable() {
        return gui.isGuiAvailable();
    }

    @Override
    public void sync() {
        gui.sync();
    }

    @Override
    public void close() {
        gui.close();
    }

    @Override
    public void onPBHFullyStarted(PeerBanHelper server) {
        gui.onPBHFullyStarted(server);
    }

    @Override
    public void createNotification(Level level, String title, String description) {
        gui.createNotification(level, title, description);
    }

    @Override
    public boolean supportInteractive() {
        return gui.supportInteractive();
    }

    @Override
    public void createDialog(Level level, String title, String description, Runnable clickEvent) {
        gui.createDialog(level, title, description, clickEvent);
    }

    @Override
    public void createYesNoDialog(Level level, String title, String description, Runnable yesEvent, Runnable noEvent) {
        gui.createYesNoDialog(level, title, description, yesEvent, noEvent);
    }

    @Override
    public ProgressDialog createProgressDialog(String title, String description, String buttonText, Runnable buttonEvent, boolean allowCancel) {
        return gui.createProgressDialog(title, description, buttonText, buttonEvent, allowCancel);
    }

    @Override
    public TaskbarControl taskbarControl() {
        return gui.taskbarControl();
    }

    @Override
    public void openUrlInBrowser(String url) {
        gui.openUrlInBrowser(url);
    }
}
