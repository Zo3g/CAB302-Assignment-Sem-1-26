package com.example.cab302assignment.model;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

public class GuardiaApplication extends Application {
    @Override
    public void start(Stage stage) {
        try (InputStream fontStream = getClass().getResourceAsStream("/assets/fonts/IstokWeb-Regular.ttf")) {
            if (fontStream != null) {
                Font.loadFont(fontStream, 14);
            }
        } catch (Exception ignored) {
        }

        DatabaseInitialiser.createTables();
        ViewManager.setPrimaryStage(stage);
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }
}
