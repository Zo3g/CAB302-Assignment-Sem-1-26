package com.example.cab302assignment.model.enums;

public enum RiskLevel {
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
}
