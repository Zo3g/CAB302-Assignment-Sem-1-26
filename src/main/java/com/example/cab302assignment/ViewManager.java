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
            Scene scene = new Scene(loader.load(), 440, 640);
            String stylesheet = ViewManager.class.getResource("/com/example/cab302assignment/login-styles.css").toExternalForm();
            String globalCss = ViewManager.class.getResource("/com/example/cab302assignment/global.css").toExternalForm();
            scene.getStylesheets().addAll(globalCss, stylesheet);

            primaryStage.setFullScreen(false);
            primaryStage.setMaximized(false);
            primaryStage.setResizable(false);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static void switchToWorkspace() {
        try {
            FXMLLoader loader = new FXMLLoader(
                ViewManager.class.getResource("/com/example/cab302assignment/views/main-layout.fxml")
            );
            Scene scene = new Scene(loader.load());
            String globalCss = ViewManager.class.getResource("/com/example/cab302assignment/global.css").toExternalForm();
            scene.getStylesheets().add(globalCss);

            primaryStage.hide();
            primaryStage.setFullScreen(false);
            primaryStage.setResizable(true);
            primaryStage.setTitle("Guardia: AI Compliance Guard");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load workspace");
            e.printStackTrace();
        }
    }
}
