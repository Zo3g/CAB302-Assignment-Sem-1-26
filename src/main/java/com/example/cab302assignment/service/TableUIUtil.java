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
     * Applies a custom cell factory to a TableColumn to render risk levels as styled badges.
     * The badge color dynamically updates based on the text content.
     *
     * @param column The TableColumn to style. The <T> allows it to work with any table data model.
     * @param <T>    The type of the TableView data model.
     */
    public static <T> void setupRiskLevelColumn(TableColumn<T, String> column) {
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

                Label badge = new Label(item);
                badge.getStyleClass().add("risk-label");

                String lower = item.toLowerCase();

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
}
