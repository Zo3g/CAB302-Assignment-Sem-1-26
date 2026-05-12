package com.example.cab302assignment.model.enums;

public enum RiskLevel {
    NO(0.0, "None"),
    LOW(25.0, "Low"),
    MEDIUM(50.0, "Medium"),
    HIGH(75.0, "High"),
    CRITICAL(100.0, "Critical");

    private final double scoreDouble;
    private final String scoreString;

    RiskLevel(double scoreDouble, String scoreString) {

        this.scoreDouble = scoreDouble;
        this.scoreString = scoreString;
    }

    public double scoreDouble() {
        return scoreDouble;
    }

    public String scoreString() {
        return scoreString;
    }
}
