module com.example.cab302assignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cab302assignment to javafx.fxml;
    exports com.example.cab302assignment;
}