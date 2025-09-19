package com.ghostchu.peerbanhelper.gui.impl;

import com.ghostchu.peerbanhelper.PeerBanHelper;
import com.ghostchu.peerbanhelper.gui.ProgressDialog;
import com.ghostchu.peerbanhelper.gui.TaskbarControl;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

public interface GuiImpl {
    boolean isDarkMode();

    void setup();

    void createMainWindow();

    void sync();

    void close();

    default void onPBHFullyStarted(PeerBanHelper server) {
    }

    void createNotification(Level level, String title, String description);

    void createDialog(Level level, String title, String description, Runnable clickEvent);

    ProgressDialog createProgressDialog(String title, String description, String buttonText, Runnable buttonEvent, boolean allowCancel);

    TaskbarControl taskbarControl();

    boolean isGuiAvailable();

    String getName();

    boolean supportInteractive();

    void createYesNoDialog(Level level, String title, String description, @Nullable Runnable yesEvent, @Nullable Runnable noEvent);

    void openUrlInBrowser(String url);
}
