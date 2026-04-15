package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedactionRule {
    private int ruleId;
    private int rulesetId;
    private String name;
    private String regexPattern;
    private SensitiveDataType dataType;
    private boolean enabled;
    private String placeholderFormat;

    private Pattern compiled;

    public RedactionRule() {
    }

    public RedactionRule(String name, String regexPattern, SensitiveDataType dataType, boolean enabled, String placeholderFormat) {
        this.name = name;
        this.regexPattern = regexPattern;
        this.dataType = dataType;
        this.enabled = enabled;
        this.placeholderFormat = placeholderFormat;
    }

    public RedactionRule(int ruleId, int rulesetId, String name, String regexPattern,
                         SensitiveDataType dataType, boolean enabled, String placeholderFormat) {
        this.ruleId = ruleId;
        this.rulesetId = rulesetId;
        this.name = name;
        this.regexPattern = regexPattern;
        this.dataType = dataType;
        this.enabled = enabled;
        this.placeholderFormat = placeholderFormat;
    }

    public int getRuleId() { return ruleId; }
    public void setRuleId(int ruleId) { this.ruleId = ruleId; }

    public int getRulesetId() { return rulesetId; }
    public void setRulesetId(int rulesetId) { this.rulesetId = rulesetId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegexPattern() { return regexPattern; }
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
        this.compiled = null;
    }

    public SensitiveDataType getDataType() { return dataType; }
    public void setDataType(SensitiveDataType dataType) { this.dataType = dataType; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getPlaceholderFormat() { return placeholderFormat; }
    public void setPlaceholderFormat(String placeholderFormat) { this.placeholderFormat = placeholderFormat; }

    public Pattern getCompiledPattern() {
        if (compiled == null) {
            compiled = Pattern.compile(regexPattern);
        }
        return compiled;
    }

    public List<int[]> matches(String text) {
        List<int[]> hits = new ArrayList<>();
        if (text == null) return hits;
        Matcher m = getCompiledPattern().matcher(text);
        while (m.find()) {
            hits.add(new int[]{m.start(), m.end()});
        }
        return hits;
    }

    public String getPlaceholder(int index) {
        if (placeholderFormat == null) return "[REDACTED]";
        return placeholderFormat.replace("{index}", Integer.toString(index));
    }
}
