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

    public static RiskLevel fromCalculatedScore(double calculatedScore) {
        if (calculatedScore < 12.5) {
            return NO;
        }
        if (calculatedScore < 37.5) {
            return LOW;
        }
        if (calculatedScore < 62.5) {
            return MEDIUM;
        }
        if (calculatedScore < 87.5) {
            return HIGH;
        }
        return CRITICAL;
    }
}
