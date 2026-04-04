package com.example.cab302assignment;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchView(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ViewManager.class.getResource("/com/example/cab302assignment/" + fxmlFile)
            );
            Scene scene = new Scene(loader.load(), 400, 550);
            String stylesheet = ViewManager.class.getResource("/com/example/cab302assignment/stylesheet.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
