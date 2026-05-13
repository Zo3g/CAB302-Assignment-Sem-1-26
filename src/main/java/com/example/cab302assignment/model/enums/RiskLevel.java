package com.example.cab302assignment.model.enums;

public enum RiskLevel {
    NO(0.0),
    LOW(25.0),
    MEDIUM(50.0),
    HIGH(75.0),
    CRITICAL(100.0);

    private final double score;

    RiskLevel(double score) {
        this.score = score;
    }

    public double score() {
        return score;
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
