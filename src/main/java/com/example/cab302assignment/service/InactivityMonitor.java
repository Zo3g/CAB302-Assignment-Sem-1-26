package com.example.cab302assignment.service;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class InactivityMonitor {
    private static final Logger LOGGER = Logger.getLogger(InactivityMonitor.class.getName());
    private static final long ACTIVITY_LOG_THROTTLE_MS = 1000L;

    private static Scene attachedScene;
    private static Timeline checker;
    private static Runnable onExpiry;
    private static long lastLoggedActivityAt = 0L;

    private static final EventHandler<MouseEvent> MOUSE_HANDLER = e -> registerActivity(e.getEventType().getName());
    private static final EventHandler<KeyEvent> KEY_HANDLER = e -> registerActivity(e.getEventType().getName());
    private static final EventHandler<ScrollEvent> SCROLL_HANDLER = e -> registerActivity(e.getEventType().getName());

    private InactivityMonitor() {}

    public static void start(Scene scene, Runnable expiryCallback) {
        stop();
        if (scene == null) {
            return;
        }
        attachedScene = scene;
        onExpiry = expiryCallback;
        SessionManager.recordActivity();
        LOGGER.log(Level.INFO, "Inactivity monitor started (timeout={0}ms)",
                SessionManager.getInactivityTimeoutMs());

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
            LOGGER.log(Level.INFO, "Inactivity monitor stopped");
        }
        onExpiry = null;
        lastLoggedActivityAt = 0L;
    }

    private static void registerActivity(String eventType) {
        SessionManager.recordActivity();
        long now = System.currentTimeMillis();
        if (now - lastLoggedActivityAt >= ACTIVITY_LOG_THROTTLE_MS) {
            lastLoggedActivityAt = now;
            LOGGER.log(Level.FINE, "Activity reset inactivity timer (event={0})", eventType);
        }
    }

    private static void checkExpiry() {
        if (SessionManager.isExpired()) {
            long idleMs = System.currentTimeMillis() - SessionManager.getLastActivityAt();
            LOGGER.log(Level.INFO, "Session timed out after {0}ms of inactivity", idleMs);
            Runnable callback = onExpiry;
            stop();
            SessionManager.logout();
            SessionManager.setTimedOut(true);
            if (callback != null) {
                callback.run();
            }
        }
    }
}
