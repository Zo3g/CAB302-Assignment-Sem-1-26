package com.example.cab302assignment.controller;

import com.example.cab302assignment.model.Membership;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ManagementController
{
    @FXML private TextField emailSearchField;
    @FXML private Text userIdText;
    @FXML private Text nameText;
    @FXML private Text emailText;
    @FXML private Text activeText;
    @FXML private CheckBox confirmCheckBox;
    @FXML private Button removeButton;
    @FXML private Button addButton;
}
