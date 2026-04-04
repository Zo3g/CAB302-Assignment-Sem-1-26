package com.example.cab302assignment;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        DatabaseInitialiser.createTables();
        ViewManager.setPrimaryStage(stage);
        ViewManager.switchView("sign-in-view.fxml", "Sign In");
    }
}
