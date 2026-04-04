package com.example.cab302assignment;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class GuardiaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try (InputStream fontStream = getClass().getResourceAsStream("/assets/fonts/IstokWeb-Regular.ttf")) {
            if (fontStream != null) {
                Font.loadFont(fontStream, 14);
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/cab302assignment/views/main-layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().getClass().getResource("/com/example/cab302assignment/global.css");
        stage.setTitle("Guardia: AI Compliance Guard");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
