package com.example.cab302assignment.app;

import com.example.cab302assignment.service.InactivityMonitor;
import com.example.cab302assignment.controller.MemberDrilldownController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchView(String fxmlFile, String title) {
        try {
            InactivityMonitor.stop();
            FXMLLoader loader = new FXMLLoader(
                ViewManager.class.getResource("/com/example/cab302assignment/" + fxmlFile)
            );
            Scene scene = new Scene(loader.load(), 1280, 800);
            String stylesheet = ViewManager.class.getResource("/com/example/cab302assignment/login-styles.css").toExternalForm();
            String globalCss = ViewManager.class.getResource("/com/example/cab302assignment/global.css").toExternalForm();
            scene.getStylesheets().addAll(globalCss, stylesheet);

            primaryStage.setFullScreen(false);
            primaryStage.setMaximized(false);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(960);
            primaryStage.setMinHeight(600);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
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

            InactivityMonitor.start(scene, () -> switchView("sign-in-view.fxml", "Sign In"));
        } catch (IOException e) {
            System.err.println("Failed to load workspace");
            e.printStackTrace();
        }
    }
    public static void switchToUserDrilldown(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewManager.class.getResource("/com/example/cab302assignment/views/member-drilldown.fxml")
            );

            javafx.scene.Node drilldownView = loader.load();

            MemberDrilldownController controller = loader.getController();
            controller.initData(userId);

            javafx.scene.Parent currentRoot = primaryStage.getScene().getRoot();

            BorderPane borderPane = (BorderPane) currentRoot;
            borderPane.setCenter(drilldownView);

        } catch (IOException e) {
            System.err.println("Failed to load member drilldown view");
            e.printStackTrace();
        }
    }
}
