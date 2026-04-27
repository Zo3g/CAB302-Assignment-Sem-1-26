package com.example.cab302assignment.app;

import javafx.application.Application;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launcher {
    public static void main(String[] args) {
        configureLogging();
        Application.launch(GuardiaApplication.class, args);
    }

    private static void configureLogging() {
        if (System.getProperty("java.util.logging.config.file") != null) {
            return;
        }
        try (InputStream config = Launcher.class.getResourceAsStream("/logging.properties")) {
            if (config != null) {
                LogManager.getLogManager().readConfiguration(config);
            }
        } catch (Exception e) {
            Logger.getLogger(Launcher.class.getName())
                    .warning("Failed to load logging.properties: " + e.getMessage());
        }
    }
}
