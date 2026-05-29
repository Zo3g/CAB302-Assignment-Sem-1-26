package com.example.cab302assignment.model;

import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single rule used by the redaction engine to identify and mask sensitive data.
 * A rule defines a regular expression pattern to search for, the type of data it represents,
 * and how the redacted text should be formatted when a match is found.
 * * Note: Many accessor/mutator methods were initially included to support
 * planned future functionality for manager-controlled ruleset customisation. This feature will not be completed at this current stage.
 * Currently, these rules are evaluated in-memory using a hardcoded default ruleset.
 */
public class RedactionRule {
    private int ruleId;
    private int rulesetId;
    private String name;
    private String regexPattern;
    private SensitiveDataType dataType;
    private boolean enabled;
    private String placeholderFormat;

    private Pattern compiled;

    /**
     * Default constructor required for database operations and serialization.
     * (Planned functionality for custom database rulesets).
     */
    public RedactionRule() {
    }

    /**
     * Constructs a new RedactionRule.
     *
     * @param name              The descriptive name of the rule (e.g., "Australian Medicare Number").
     * @param regexPattern      The regular expression used to find matches in the text.
     * @param dataType          The categorical type of sensitive data this rule detects.
     * @param enabled           Whether this rule is currently active in the engine.
     * @param placeholderFormat The string format used to replace detected text (e.g., "[MEDICARE-{index}]").
     */
    public RedactionRule(String name, String regexPattern, SensitiveDataType dataType, boolean enabled, String placeholderFormat) {
        this.name = name;
        this.regexPattern = regexPattern;
        this.dataType = dataType;
        this.enabled = enabled;
        this.placeholderFormat = placeholderFormat;
    }

    /**
     * Constructs a fully populated RedactionRule, typically used when loading an existing rule from the database.
     * (Planned functionality for custom database rulesets).
     *
     * @param ruleId            The unique database identifier for this rule.
     * @param rulesetId         The ID of the ruleset this rule belongs to.
     * @param name              The descriptive name of the rule.
     * @param regexPattern      The regular expression used to find matches in the text.
     * @param dataType          The categorical type of sensitive data this rule detects.
     * @param enabled           Whether this rule is currently active in the engine.
     * @param placeholderFormat The string format used to replace detected text.
     */
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

    /**
     * Gets the unique database identifier for this rule.
     * (Planned functionality for custom rulesets).
     * @return The rule ID.
     */
    public int getRuleId() { return ruleId; }

    /**
     * Sets the unique database identifier for this rule.
     * (Planned functionality for custom rulesets).
     * @param ruleId The rule ID.
     */
    public void setRuleId(int ruleId) { this.ruleId = ruleId; }

    /**
     * Gets the ID of the ruleset this rule belongs to.
     * (Planned functionality for custom rulesets).
     * @return The ruleset ID.
     */
    public int getRulesetId() { return rulesetId; }

    /**
     * Sets the ID of the ruleset this rule belongs to.
     * (Planned functionality for custom rulesets).
     * @param rulesetId The ruleset ID.
     */
    public void setRulesetId(int rulesetId) { this.rulesetId = rulesetId; }

    /**
     * Gets the descriptive name of the rule.
     * (Planned functionality for custom rulesets).
     * @return The rule name.
     */
    public String getName() { return name; }

    /**
     * Sets the descriptive name of the rule.
     * (Planned functionality for custom rulesets).
     * @param name The rule name.
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the raw regular expression pattern used by this rule.
     * (Planned functionality for custom rulesets).
     * @return The regex pattern string.
     */
    public String getRegexPattern() { return regexPattern; }

    /**
     * Sets the regular expression pattern for this rule.
     * Updating this value automatically invalidates the cached compiled pattern.
     * (Planned functionality for custom rulesets).
     *
     * @param regexPattern The new regex pattern string.
     */
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
        this.compiled = null;
    }

    /**
     * Gets the category of sensitive data this rule detects.
     * @return The sensitive data type.
     */
    public SensitiveDataType getDataType() { return dataType; }

    /**
     * Sets the category of sensitive data this rule detects.
     * (Planned functionality for custom rulesets).
     * @param dataType The sensitive data type.
     */
    public void setDataType(SensitiveDataType dataType) { this.dataType = dataType; }

    /**
     * Checks if this rule is currently active.
     * @return True if the rule is enabled, false otherwise.
     */
    public boolean isEnabled() { return enabled; }

    /**
     * Sets the active status of this rule.
     * (Planned functionality for custom rulesets).
     * @param enabled True to enable the rule, false to disable.
     */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    /**
     * Gets the format string used to replace detected data.
     * (Planned functionality for custom rulesets).
     * @return The placeholder format string.
     */
    public String getPlaceholderFormat() { return placeholderFormat; }

    /**
     * Sets the format string used to replace detected data.
     * (Planned functionality for custom rulesets).
     * @param placeholderFormat The placeholder format string.
     */
    public void setPlaceholderFormat(String placeholderFormat) { this.placeholderFormat = placeholderFormat; }

    /**
     * Compiles and returns the regular expression pattern.
     *
     * @return The compiled {@link Pattern} object.
     */
    public Pattern getCompiledPattern() {
        if (compiled == null) {
            compiled = Pattern.compile(regexPattern);
        }
        return compiled;
    }


    /**
     * Generates the replacement string for a detected match, substituting the dynamic index marker.
     *
     * @param index The occurrence number of this specific data type in the text (e.g., 1 for the first match).
     * @return The formatted placeholder string (e.g., "[REDACTED-1]"). Defaults to "[REDACTED]" if no format is set.
     */
    public String getPlaceholder(int index) {
        if (placeholderFormat == null) return "[REDACTED]";
        return placeholderFormat.replace("{index}", Integer.toString(index));
    }
}