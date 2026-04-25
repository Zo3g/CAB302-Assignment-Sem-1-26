package com.example.cab302assignment.controller;

import com.example.cab302assignment.service.GeminiService;
import com.example.cab302assignment.service.RedactionEngine;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.scene.paint.Color;

public class WorkspaceController {
    @FXML private TextArea inputArea;
    @FXML private TextArea outputArea;
    @FXML private TextFlow insightCol1;
    @FXML private TextFlow insightCol2;
    @FXML private Button scanButton;
    @FXML private Button copyButton;
    @FXML private Button clearButton;
    @FXML private AnchorPane gaugePane;
    @FXML private VBox insightPane1;
    @FXML private VBox insightPane2;
    @FXML private VBox resultContainer;
    @FXML private HBox promptContainer;
    @FXML private ScrollPane root;
    @FXML private Label errorMessage;

    private Gauge riskGauge;

    enum RiskLevel {
        NO_RISK,
        LOW_RISK,
        MEDIUM_RISK,
        HIGH_RISK,
        CRITICAL_RISK
    }

    private RedactionEngine redactionEngine = new RedactionEngine();

    private GeminiService geminiService;

    @FXML
    public void initialize() {
        promptContainer.minHeightProperty().bind(root.heightProperty().multiply(0.5));
        resultContainer.maxHeightProperty().bind(root.heightProperty().multiply(0.3));
        gaugePane.maxWidthProperty().bind(resultContainer.widthProperty().multiply(0.3));
        insightCol1.prefWidthProperty().bind(resultContainer.widthProperty().multiply(0.3));
        insightCol2.prefWidthProperty().bind(resultContainer.widthProperty().multiply(0.3));

        setTextFlowContent(insightCol1, "Guardia is here to help you remain compliant and protect your sensitive data. Enter your generative AI prompt to scan for risks and generate useful insights.");
        setTextFlowContent(insightCol2, "Enter your generative AI prompt to scan for risks and generate useful insights.");

        buildRiskGauge();

        // Initialize Gemini service
        try{
            geminiService = new GeminiService();
        } catch(IllegalStateException e){
            System.out.println("Gemini unavailable");
        }

    }

    @FXML
    private void onScan() {
        String prompt = inputArea.getText();

        if (!validatePrompt(prompt)) {
            return; // Stop processing if validation fails
        }

        String sanitized = sanitizePrompt(prompt);
        outputArea.setText(sanitized);

        // Show loading state
        scanButton.setDisable(true);
        scanButton.setText("Analysing...");
        setTextFlowContent(insightCol1, "Waiting for AI analysis...");
        setTextFlowContent(insightCol2, "");

        if (geminiService == null) {
            errorMessage.setText("Artificial intelligence service unavailable — check your GEMINI_API_KEY. Please try again later for insights.");
            return;
        }

        Task<String> analysisTask = new Task<>() {
            @Override
            protected String call() {
                System.out.println("Calling AI service...");
                return geminiService.analysePromptRisk(sanitized);
            }
        };

        analysisTask.setOnSucceeded(workerStateEvent -> {
            String analysis = analysisTask.getValue();
            parseAndDisplayAnalysis(analysis);
            scanButton.setDisable(false);
            scanButton.setText("Scan");
        });

        analysisTask.setOnFailed(workerStateEvent -> {
            Throwable ex = analysisTask.getException();
            System.err.println("AI analysis failed: " + ex.getMessage());
            ex.printStackTrace();
            setTextFlowContent(insightCol1, "Analysis failed: " + ex.getMessage());
            scanButton.setDisable(false);
            scanButton.setText("Scan");
        });

        // Start the task on a background thread
        Thread thread = new Thread(analysisTask);
        thread.setDaemon(true);
        thread.start();

        //fetchInsights(prompt);
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

    private boolean validatePrompt(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            errorMessage.setText("Prompt cannot be empty. Please enter a valid prompt.");
            return false;
        }

        if (prompt.length() > 10000) {
            errorMessage.setText("Prompt is too long. Maximum allowed characters: 10,000.");
            return false;
        }

        errorMessage.setText("");
        return true;
    }

    private String sanitizePrompt(String prompt) {
        return redactionEngine.redactPrompt(prompt);
    }

    /**
     * Splits the AI response into Risk and Effect sections,
     * then renders each into the corresponding TextFlow column.
     */
    private void parseAndDisplayAnalysis(String analysis) {
        if (analysis == null || analysis.isBlank()) {
            setTextFlowContent(insightCol1, "No analysis available.");
            setTextFlowContent(insightCol2, "");
            return;
        }

        // Split on "Effect:" (case-insensitive) to separate the two sections
        String[] parts = analysis.split("(?i)Effect:", 2);

        String riskSection = parts[0].trim();
        String effectSection = parts.length > 1 ? parts[1].trim() : "";

        // Remove the leading "Risk:" header if present
        riskSection = riskSection.replaceFirst("(?i)^Risk:\\s*", "").trim();

        renderMarkdownBold(insightCol1, "Risk", riskSection);
        renderMarkdownBold(insightCol2, "Effect", effectSection);
    }

    /**
     * Renders text with **bold** markdown markers into a TextFlow.
     * Adds a bold title header, then parses **...**  segments as bold Text nodes.
     */
    private void renderMarkdownBold(TextFlow textFlow, String title, String content) {
        textFlow.getChildren().clear();

        // Add a bold title line
        Text titleText = new Text(title + ":\n");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 14));
        textFlow.getChildren().add(titleText);

        if (content == null || content.isBlank()) {
            return;
        }

        // Replace markdown bullet markers (* ) with actual bullet points (•)
        content = content.replaceAll("(?m)^\\*\\s", "• ");

        // Regex to match **bold** segments
        Pattern boldPattern = Pattern.compile("\\*\\*(.+?)\\*\\*");
        Matcher matcher = boldPattern.matcher(content);

        int lastEnd = 0;
        while (matcher.find()) {
            // Add the plain text before this bold match
            if (matcher.start() > lastEnd) {
                String plainChunk = content.substring(lastEnd, matcher.start());
                Text plainText = new Text(plainChunk);
                plainText.setFont(Font.font("System", FontWeight.NORMAL, 12));
                textFlow.getChildren().add(plainText);
            }

            // Add the bold text (without the ** markers)
            Text boldText = new Text(matcher.group(1));
            boldText.setFont(Font.font("System", FontWeight.BOLD, 12));
            textFlow.getChildren().add(boldText);

            lastEnd = matcher.end();
        }

        // Add any remaining plain text after the last bold match
        if (lastEnd < content.length()) {
            String remaining = content.substring(lastEnd);
            Text remainingText = new Text(remaining);
            remainingText.setFont(Font.font("System", FontWeight.NORMAL, 12));
            textFlow.getChildren().add(remainingText);
        }
    }

    /**
     * Simple helper to set plain text content on a TextFlow.
     */
    private void setTextFlowContent(TextFlow textFlow, String content) {
        textFlow.getChildren().clear();
        Text text = new Text(content);
        text.setFont(Font.font("System", FontWeight.NORMAL, 12));
        textFlow.getChildren().add(text);
    }

    private double calculateRisk(String prompt) {
        //Update LLM logic here
        RiskLevel risk = RiskLevel.NO_RISK;

        return switch (risk) {
            case NO_RISK -> 0;
            case LOW_RISK -> 20;
            case MEDIUM_RISK -> 45;
            case HIGH_RISK -> 70;
            case CRITICAL_RISK -> 95;
        };
    }

    private void buildRiskGauge() {
        riskGauge = GaugeBuilder.create()
                .minValue(0)
                .maxValue(100)
                .title("Risk Score")
                .unit("")
                .animated(true)
                .animationDuration(1600)
                .skinType(Gauge.SkinType.DASHBOARD)
                .valueVisible(true)
                .sections(
                        new Section(0, 0, Color.LIMEGREEN),        // No Risk (special case)
                        new Section(1, 25, Color.GREEN),          // Low Risk
                        new Section(25, 50, Color.YELLOW),        // Medium Risk
                        new Section(50, 75, Color.ORANGE),        // High Risk
                        new Section(75, 100, Color.RED)
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


