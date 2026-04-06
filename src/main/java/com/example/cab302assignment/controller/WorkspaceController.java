package com.example.cab302assignment.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.scene.paint.Color;




public class WorkspaceController {
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private Text insightCol1;
    @FXML private Text insightCol2;
    @FXML private Button scanButton;
    @FXML private Button copyButton;
    @FXML private Button clearButton;
    @FXML private AnchorPane gaugePane;
    @FXML private VBox insightPane1;
    @FXML private VBox insightPane2;
    @FXML private VBox resultContainer;
    @FXML private HBox promptContainer;
    @FXML private ScrollPane root;

    private Gauge riskGauge;

    @FXML
    public void initialize() {
        promptContainer.minHeightProperty().bind(root.heightProperty().multiply(0.5));
        resultContainer.maxHeightProperty().bind(root.heightProperty().multiply(0.3));
        gaugePane.maxWidthProperty().bind(resultContainer.widthProperty().multiply(0.3));
        insightCol1.wrappingWidthProperty().bind(resultContainer.widthProperty().multiply(0.3));
        insightCol2.wrappingWidthProperty().bind(resultContainer.widthProperty().multiply(0.3));

        insightCol1.setText("Guardia is here to help you remain compliant and protect your sensitive data. Enter your generative AI prompt to scan for risks and generate useful insights.");
        insightCol2.setText("Enter your generative AI prompt to scan for risks and generate useful insights.");

        buildRiskGauge();

    }

    @FXML
    private void onScan() {
        String prompt = inputArea.getText();
        String sanitized = sanitizePrompt(prompt);
        outputArea.setText(sanitized);
        fetchInsights(prompt);
        double risk = calculateRisk(prompt);
        riskGauge.setValue(risk);

    }

    @FXML
    private void onCopy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(outputArea.getText());
        clipboard.setContent(content);
    }

    @FXML
    private void onClear() {
        inputArea.setText("");
    }

    private String sanitizePrompt(String prompt) {
        return prompt.trim();
    }

    private void fetchInsights(String prompt) {
        String text = prompt;

        String col1 = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam id risus lobortis, finibus nulla in, sodales eros. Curabitur urna enim, sagittis ornare mauris ac, viverra commodo justo. Vivamus laoreet sapien non risus aliquet porttitor. Etiam tempus ultricies consectetur. Nam sit amet pharetra justo. Vestibulum ut ornare nulla. Integer et lorem eleifend, auctor sem nec, fringilla lorem. Vestibulum nisl arcu, consequat quis tellus vitae, pulvinar dignissim purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse vel leo non turpis fermentum laoreet eget ut tortor. Sed sollicitudin arcu justo, eu tempor ante blandit non. In hac habitasse platea dictumst. Duis vulputate quam fermentum quam accumsan eleifend. Proin nunc orci, gravida sit amet laoreet id, tempor in nibh. In vel neque et ipsum pretium posuere quis at leo.""";

        String col2 = """
                Suspendisse pellentesque tellus metus, sed ornare dui facilisis at. Nam consectetur felis sed augue pretium ultrices. Aenean rhoncus purus mauris, porta elementum sem placerat vitae. Pellentesque luctus mollis eros ut iaculis. Etiam suscipit lorem nec metus auctor aliquet. Sed commodo dapibus velit. Proin sit amet iaculis nisi. Nam ac lacinia est. Nulla porttitor, nisi eget pharetra ultrices, turpis tortor placerat ligula, vel efficitur nunc eros id diam. Mauris a convallis dolor. Donec sollicitudin eget diam in dignissim. In ex quam, rhoncus vitae commodo a, pharetra eget lorem.""";

        insightCol1.setText(col1);
        insightCol2.setText(col2);
    }

    private double calculateRisk(String prompt) {
        double risk = Math.random() * 100;
        return risk;
    }

    private void buildRiskGauge() {
            riskGauge = GaugeBuilder.create()
                    .minValue(0)
                    .maxValue(100)
                    .title("Risk Score")
                    .animated(true)
                    .animationDuration(1600)
                    // Use a supported "arch"/dashboard skin
                    .skinType(Gauge.SkinType.DASHBOARD)
                    // Display the numeric value above
                    .valueVisible(true)
                    // Set section ranges with rainbow colors
                    .sections(
                            new Section(0, 20, Color.LIMEGREEN),
                            new Section(20, 40, Color.GREENYELLOW),
                            new Section(40, 60, Color.GOLD),
                            new Section(60, 80, Color.ORANGE),
                            new Section(80, 100, Color.RED)
                    )
                    .sectionsVisible(true)
                    .build();

            gaugePane.getChildren().add(riskGauge);
            AnchorPane.setTopAnchor(riskGauge, 0.0);
            AnchorPane.setBottomAnchor(riskGauge, 0.0);
            AnchorPane.setLeftAnchor(riskGauge, 0.0);
            AnchorPane.setRightAnchor(riskGauge, 0.0);
    }
}


