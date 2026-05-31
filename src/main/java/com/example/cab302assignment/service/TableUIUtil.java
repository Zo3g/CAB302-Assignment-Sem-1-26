package com.example.cab302assignment.service;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * A utility class containing reusable UI helper methods for JavaFX components, needed for tables in two controller files.
 * Centralizing these methods ensures consistent styling across the application
 * and prevents code duplication in controllers.
 */
public class TableUIUtil {

    /**
     * Replaces the standard text of a TableColumn header with a custom Label containing a Tooltip.
     *
     * @param column     The JavaFX TableColumn to modify. Allows any generic type.
     * @param headerText The text to display inside the new header label.
     */
    public static void addHeaderTooltip(TableColumn<?, ?> column, String headerText) {
        Label label = new Label(headerText);

        Tooltip headerTooltip = new Tooltip("Click to sort by " + headerText);
        headerTooltip.setShowDelay(Duration.millis(0));

        headerTooltip.getStyleClass().add("custom-tooltip");

        label.setTooltip(headerTooltip);
        column.setGraphic(label);
        column.setText("");
    }

    /**
     * Apllies a custom comparator to ensure that when a user clicks on the Risk Level header of the table, the rows
     * are sorted by the risk levels' weights, not alphabetically.
     *
     * Formats text so that the first letter is capitalised.
     *
     * Applies a custom cell factory to a TableColumn to render risk levels as styled badges.
     * The badge color dynamically updates based on the text content.
     *
     * @param column The TableColumn to style. The <T> allows it to work with any table data model.
     * @param <T>    The type of the TableView data model.
     */
    public static <T> void setupRiskLevelColumn(TableColumn<T, String> column) {


        //Compares numeric weights of risk so that they are sorted correctly, not alphabetically.
        column.setComparator((risk1, risk2) -> {
            int weight1 = getRiskSortWeight(risk1);
            int weight2 = getRiskSortWeight(risk2);
            return Integer.compare(weight1, weight2);
        });

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }

                String lower = item.toLowerCase();
                String formattedText = lower.substring(0, 1).toUpperCase() + lower.substring(1);
                Label badge = new Label(formattedText);
                badge.getStyleClass().add("risk-label");

                if (lower.contains("critical")) {
                    badge.getStyleClass().add("risk-label-critical");
                } else if (lower.contains("high")) {
                    badge.getStyleClass().add("risk-label-high");
                } else if (lower.contains("medium")) {
                    badge.getStyleClass().add("risk-label-medium");
                } else if (lower.contains("low")) {
                    badge.getStyleClass().add("risk-label-low");
                } else {
                    badge.getStyleClass().add("risk-label-none");
                }

                setGraphic(badge);
                setText(null);
            }
        });
    }
    /**
     * Helper method to assign a numeric weight to risk level strings for sorting.
     */
    private static int getRiskSortWeight(String risk) {
        if (risk == null) return 0;

        String lower = risk.toLowerCase();
        if (lower.contains("critical")) return 5;
        if (lower.contains("high")) return 4;
        if (lower.contains("medium")) return 3;
        if (lower.contains("low")) return 2;
        if (lower.contains("none")) return 1;

        return 0; // Covers "None", "No Past Prompts", or unknowns
    }
}
