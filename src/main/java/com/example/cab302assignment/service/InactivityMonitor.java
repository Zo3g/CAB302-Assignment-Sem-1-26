package com.example.cab302assignment.service;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * Global inactivity monitoring utility for tracking user session activity.
 *
 * Listens for user input events (mouse, keyboard, scroll) and periodically
 * checks whether the session has exceeded the configured inactivity timeout.
 *
 * If a timeout occurs, the monitor:
 * - Stops event tracking
 * - Logs the user out via SessionManager
 * - Triggers an optional expiry callback (e.g. UI redirect)
 *
 * This class is static-only and cannot be instantiated.
 */
public final class InactivityMonitor {
    private static final long ACTIVITY_LOG_THROTTLE_MS = 1000L;

    private static Scene attachedScene;
    private static Timeline checker;
    private static Runnable onExpiry;
    private static long lastLoggedActivityAt = 0L;

    private static final EventHandler<MouseEvent> MOUSE_HANDLER = e -> registerActivity(e.getEventType().getName());
    private static final EventHandler<KeyEvent> KEY_HANDLER = e -> registerActivity(e.getEventType().getName());
    private static final EventHandler<ScrollEvent> SCROLL_HANDLER = e -> registerActivity(e.getEventType().getName());

    private InactivityMonitor() {}

    /**
     * Starts inactivity monitoring on the provided JavaFX scene.
     *
     * Registers input listeners for:
     * - Mouse movement and clicks
     * - Keyboard input
     * - Scroll events
     *
     * Also starts a periodic timer that checks for session expiry.
     *
     * @param scene the JavaFX scene to attach activity listeners to
     * @param expiryCallback callback executed when the session expires
     */
    public static void start(Scene scene, Runnable expiryCallback) {
        stop();
        if (scene == null) {
            return;
        }
        attachedScene = scene;
        onExpiry = expiryCallback;
        SessionManager.recordActivity();
        System.out.println("[InactivityMonitor] started (timeout=" + SessionManager.getInactivityTimeoutMs() + "ms)");

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, MOUSE_HANDLER);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, MOUSE_HANDLER);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, KEY_HANDLER);
        scene.addEventFilter(ScrollEvent.SCROLL, SCROLL_HANDLER);

        checker = new Timeline(new KeyFrame(Duration.seconds(5), e -> checkExpiry()));
        checker.setCycleCount(Timeline.INDEFINITE);
        checker.play();
    }

    /**
     * Stops inactivity monitoring and removes all registered event listeners.
     *
     * Also stops the internal expiry timer and clears session state references.
     */
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
            System.out.println("[InactivityMonitor] stopped");
        }
        onExpiry = null;
        lastLoggedActivityAt = 0L;
    }

    /**
     * Records user activity and resets the session inactivity timer.
     *
     * Throttles logging to avoid excessive console output.
     *
     * @param eventType type of input event that triggered activity reset
     */
    private static void registerActivity(String eventType) {
        SessionManager.recordActivity();
        long now = System.currentTimeMillis();
        if (now - lastLoggedActivityAt >= ACTIVITY_LOG_THROTTLE_MS) {
            lastLoggedActivityAt = now;
            System.out.println("[InactivityMonitor] activity reset timer (event=" + eventType + ")");
        }
    }

    /**
     * Periodically checks whether the session has expired due to inactivity.
     *
     * If expired:
     * - Logs timeout duration
     * - Stops monitoring
     * - Logs the user out
     * - Flags session as timed out
     * - Executes expiry callback if provided
     */
    private static void checkExpiry() {
        if (SessionManager.isExpired()) {
            long idleMs = System.currentTimeMillis() - SessionManager.getLastActivityAt();
            System.out.println("[InactivityMonitor] session timed out after " + idleMs + "ms of inactivity");
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
