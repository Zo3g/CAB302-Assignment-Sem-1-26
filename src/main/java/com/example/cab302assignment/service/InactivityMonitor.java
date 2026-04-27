package com.example.cab302assignment.service;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

public final class InactivityMonitor {
    private static Scene attachedScene;
    private static Timeline checker;
    private static Runnable onExpiry;

    private static final EventHandler<MouseEvent> MOUSE_HANDLER = e -> SessionManager.recordActivity();
    private static final EventHandler<KeyEvent> KEY_HANDLER = e -> SessionManager.recordActivity();
    private static final EventHandler<ScrollEvent> SCROLL_HANDLER = e -> SessionManager.recordActivity();

    private InactivityMonitor() {}

    public static void start(Scene scene, Runnable expiryCallback) {
        stop();
        if (scene == null) {
            return;
        }
        attachedScene = scene;
        onExpiry = expiryCallback;
        SessionManager.recordActivity();

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, MOUSE_HANDLER);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, MOUSE_HANDLER);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, KEY_HANDLER);
        scene.addEventFilter(ScrollEvent.SCROLL, SCROLL_HANDLER);

        checker = new Timeline(new KeyFrame(Duration.seconds(5), e -> checkExpiry()));
        checker.setCycleCount(Timeline.INDEFINITE);
        checker.play();
    }

    public static void stop() {
        if (checker != null) {
            checker.stop();
            checker = null;
        }
        if (attachedScene != null) {
            attachedScene.removeEventFilter(MouseEvent.MOUSE_MOVED, MOUSE_HANDLER);
            attachedScene.removeEventFilter(MouseEvent.MOUSE_PRESSED, MOUSE_HANDLER);
            attachedScene.removeEventFilter(KeyEvent.KEY_PRESSED, KEY_HANDLER);
            attachedScene.removeEventFilter(ScrollEvent.SCROLL, SCROLL_HANDLER);
            attachedScene = null;
        }
        onExpiry = null;
    }

    private static void checkExpiry() {
        if (SessionManager.isExpired()) {
            Runnable callback = onExpiry;
            stop();
            SessionManager.logout();
            if (callback != null) {
                callback.run();
            }
        }
    }
}
