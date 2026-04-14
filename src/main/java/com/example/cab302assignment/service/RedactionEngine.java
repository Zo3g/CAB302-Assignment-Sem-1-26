package com.example.cab302assignment.service;

import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RedactionRule;
import com.example.cab302assignment.model.Ruleset;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;

public class RedactionEngine {
    private Ruleset activeRuleset;

    public RedactionEngine() {
        this.activeRuleset = defaultRuleset();
    }

    public RedactionEngine(Ruleset ruleset) {
        this.activeRuleset = ruleset;
    }

    public Ruleset getActiveRuleset() { return activeRuleset; }
    public void setActiveRuleset(Ruleset ruleset) { this.activeRuleset = ruleset; }

    public RedactionResult redact(String promptText) {
        Map<SensitiveDataType, Integer> typeCounts = new EnumMap<>(SensitiveDataType.class);
        if (promptText == null || promptText.trim().isEmpty() || activeRuleset == null) {
            return new RedactionResult(promptText, 0, typeCounts);
        }

        String safeText = promptText;
        int totalCaught = 0;

        for (RedactionRule rule : activeRuleset.getRules()) {
            if (!rule.isEnabled()) continue;

            Matcher matcher = rule.getCompiledPattern().matcher(safeText);
            int typeCount = 0;
            while (matcher.find()) {
                typeCount++;
                totalCaught++;
            }

            if (typeCount > 0) {
                safeText = matcher.reset().replaceAll(Matcher.quoteReplacement(rule.getPlaceholder(0)));
                typeCounts.merge(rule.getDataType(), typeCount, Integer::sum);
            }
        }

        return new RedactionResult(safeText, totalCaught, typeCounts);
    }

    public String redactPrompt(String input) {
        return redact(input).getRedactedText();
    }

    public static Ruleset defaultRuleset() {
        Ruleset rs = new Ruleset(0);
        rs.addRule(new RedactionRule("Email", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                SensitiveDataType.EMAIL_ADDRESS, true, "[REDACTED EMAIL]"));
        rs.addRule(new RedactionRule("Australian Phone",
                "(?<!\\d)(?:\\+?61|0)\\s?(\\s?\\(?[2-478]\\)?\\s?)(?:[\\s-]?\\d){8}(?!\\d)",
                SensitiveDataType.PHONE_NUMBER, true, "[REDACTED PHONE]"));
        rs.addRule(new RedactionRule("Credit Card",
                "(?<!\\d)(?:4\\d{3}|5[1-5]\\d{2}|3[47]\\d{1})[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{3,4}(?!\\d)",
                SensitiveDataType.CREDIT_CARD, true, "[REDACTED CREDIT CARD]"));
        rs.addRule(new RedactionRule("TFN", "(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d)",
                SensitiveDataType.TFN, true, "[REDACTED TFN]"));
        rs.addRule(new RedactionRule("API Key", "(?i)\\b(?:sk|pk|api[_-]?key)[_-][A-Za-z0-9]{16,}\\b",
                SensitiveDataType.API_KEY, true, "[REDACTED API KEY]"));
        rs.addRule(new RedactionRule("Password Assignment",
                "(?i)\\bpassword\\s*[:=]\\s*\\S+",
                SensitiveDataType.PASSWORD, true, "[REDACTED PASSWORD]"));
        rs.addRule(new RedactionRule("IPv4 Address",
                "(?<!\\d)(?:\\d{1,3}\\.){3}\\d{1,3}(?!\\d)",
                SensitiveDataType.IP_ADDRESS, true, "[REDACTED IP]"));
        rs.addRule(new RedactionRule("Date of Birth",
                "(?<!\\d)(?:0?[1-9]|[12]\\d|3[01])[/-](?:0?[1-9]|1[0-2])[/-](?:19|20)\\d{2}(?!\\d)",
                SensitiveDataType.DATE_OF_BIRTH, true, "[REDACTED DOB]"));
        rs.addRule(new RedactionRule("BSB/Bank Account",
                "(?<!\\d)\\d{3}[\\s\\-]?\\d{3}(?:[\\s\\-]|\\s+acc(?:ount)?[:\\s-]*)+(?:\\d[\\s\\-]?){6,10}(?!\\d)",
                SensitiveDataType.BANK_ACCOUNT, true, "[REDACTED BANK ACCOUNT]"));
        return rs;
    }
}
