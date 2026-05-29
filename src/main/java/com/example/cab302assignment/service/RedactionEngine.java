package com.example.cab302assignment.service;

import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RedactionRule;
import com.example.cab302assignment.model.Ruleset;
import com.example.cab302assignment.model.enums.SensitiveDataType;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * The core service responsible for scanning and sanitizing text.
 * The engine applies a configured {@link Ruleset} to an input string, utilizing regular expressions
 * to find, count, and redact sensitive information before it can be sent to an external LLM.
 */
public class RedactionEngine {

    private Ruleset activeRuleset;

    /**
     * Constructs a new RedactionEngine initialized with the default, hardcoded ruleset.
     */
    public RedactionEngine() {
        this.activeRuleset = defaultRuleset();
    }

    /**
     * Constructs a new RedactionEngine with a specific custom ruleset.
     *(Planned functionality for custom rulesets)
     * @param ruleset The ruleset to apply during redaction.
     */
    public RedactionEngine(Ruleset ruleset) {
        this.activeRuleset = ruleset;
    }

    /**
     * Gets the currently active ruleset being used by the engine.
     * (Planned functionality for custom rulesets)
     * @return The active {@link Ruleset}.
     */
    public Ruleset getActiveRuleset() {
        return activeRuleset;
    }

    /**
     * Sets or updates the active ruleset for the engine.
     * (Planned functionality for custom rulesets)
     * @param ruleset The new {@link Ruleset} to use for subsequent redactions.
     */
    public void setActiveRuleset(Ruleset ruleset) {
        this.activeRuleset = ruleset;
    }

    /**
     * Processes an input string, identifying and masking any sensitive data defined by the active ruleset.
     * This method also tallies the total number of detections and categorizes them by type.
     *
     * @param promptText The raw, unsanitized input string provided by the user.
     * @return A detailed {@link RedactionResult} containing the safe text, total hit count,
     * and a map of specific sensitive data types found.
     */
    public RedactionResult redact(String promptText) {
        Map<SensitiveDataType, Integer> typeCounts = new EnumMap<>(SensitiveDataType.class);

        // Return immediately if the input is blank or no ruleset is loaded
        if (promptText == null || promptText.trim().isEmpty() || activeRuleset == null) {
            return new RedactionResult(promptText, 0, typeCounts);
        }

        String safeText = promptText;
        int totalCaught = 0;

        // Iterate through every rule in the active ruleset
        for (RedactionRule rule : activeRuleset.getRules()) {
            if (!rule.isEnabled()) continue;

            Matcher matcher = rule.getCompiledPattern().matcher(safeText);
            int typeCount = 0;

            // Count how many times this specific rule matches the text
            while (matcher.find()) {
                typeCount++;
                totalCaught++;
            }

            // If we found matches, replace them with the rule's secure placeholder
            if (typeCount > 0) {
                safeText = matcher.reset().replaceAll(Matcher.quoteReplacement(rule.getPlaceholder(0)));
                typeCounts.merge(rule.getDataType(), typeCount, Integer::sum);
            }
        }

        return new RedactionResult(safeText, totalCaught, typeCounts);
    }

    /**
     * A convenience wrapper for the redact method that only returns the sanitised string.
     * Useful when the detailed counts and metrics are not needed.
     *
     * @param input The raw, unsanitized input string.
     * @return The sanitized text with sensitive data replaced by placeholders.
     */
    public String redactPrompt(String input) {
        return redact(input).getRedactedText();
    }

    /**
     * Generates the default, comprehensive ruleset used by the application.
     * This ruleset contains hardcoded regular expressions designed to catch standard personal,
     * financial, and developer-related sensitive data formats (e.g., Medicare, TFN, API Keys).
     *
     * @return A fully populated {@link Ruleset} containing all default active rules.
     */
    public static Ruleset defaultRuleset() {
        Ruleset rs = new Ruleset(0);

        rs.addRule(new RedactionRule("Email Address",
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                SensitiveDataType.EMAIL_ADDRESS, true, "[REDACTED EMAIL]"));

        rs.addRule(new RedactionRule("Australian Phone Number",
                "(?<!\\d)\\(?(?:\\+?61|0)\\s*\\(?[2-478]\\)?\\s*(?:[\\s-]?\\d){8}(?!\\d)",
                SensitiveDataType.PHONE_NUMBER, true, "[REDACTED PHONE]"));

        rs.addRule(new RedactionRule("Street Address",
                "(?i)\\b\\d+(?:/\\d+)?[a-zA-Z]?\\s+[a-z]+(?:\\s+[a-z]+)*\\s+(?:Alley|Ally|Arcade|Arc|Avenue|Ave|Boulevard|Bvd|Bypass|Bypa|Circuit|Cct|Close|Cl|Corner|Crn|Court|Ct|Crescent|Cres|Cul-de-sac|Cds|Drive|Dr|Esplanade|Esp|Green|Grn|Grove|Gr|Highway|Hwy|Junction|Jnc|Lane|Link|Mews|Parade|Pde|Place|Pl|Ridge|Rdge|Road|Rd|Square|Sq|Street|St|Terrace|Tce)\\b",
                SensitiveDataType.ADDRESS, true, "[REDACTED ADDRESS]"));

        rs.addRule(new RedactionRule("Visa & Mastercard card number",
                "(?<!\\d)(?:4\\d{3}(?:[\\s\\-]?\\d{4}){3}|(?:5[1-5]\\d{2}|222[1-9]|22[3-9]\\d|2[3-6]\\d{2}|27[0-1]\\d|2720)(?:[\\s\\-]?\\d{4}){3})(?!\\d)",
                SensitiveDataType.CREDIT_CARD, true, "[REDACTED CREDIT CARD]"));

        rs.addRule(new RedactionRule("American Express card number",
                "(?<!\\d)3[47]\\d{2}[\\s\\-]?\\d{6}[\\s\\-]?\\d{5}(?!\\d)",
                SensitiveDataType.CREDIT_CARD, true, "[REDACTED CREDIT CARD]"));

        rs.addRule(new RedactionRule("TFN",
                "(?i)(?:\\b(?:tfn|tax\\s*file\\s*number|tax\\s*number)\\b.{0,30}?(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d)|(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d).{0,30}?\\b(?:tfn|tax\\s*file\\s*number|tax\\s*number)\\b)",
                SensitiveDataType.TFN, true, "[REDACTED TFN]"));

        rs.addRule(new RedactionRule("QLD Driver's Licence Number",
                "(?i)(?:\\b(?:driver[s']?\\s*licen[cs]e|licen[cs]e\\s*num(?:ber)?|licen[cs]e\\s*no\\b|qld\\s*licen[cs]e)\\b.{0,30}?(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d)|(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d).{0,30}?\\b(?:driver[s']?\\s*licen[cs]e|licen[cs]e\\s*num(?:ber)?|licen[cs]e\\s*no\\b|crn|qld\\s*licen[cs]e)\\b)",
                SensitiveDataType.DRIVERS_LICENCE, true, "[REDACTED DRIVER'S LICENCE]"));

        rs.addRule(new RedactionRule("AWS Access Key",
                "\\bAKIA[A-Z0-9]{16}\\b",
                SensitiveDataType.AWS_KEY, true, "[REDACTED AWS KEY]"));

        rs.addRule(new RedactionRule("Google Cloud (GCP) Key",
                "\\bAIza[A-Za-z0-9_-]{20,40}\\b",
                SensitiveDataType.GCP_KEY, true, "[REDACTED GCP KEY]"));

        rs.addRule(new RedactionRule("Google OAuth Token",
                "\\bya29\\.[A-Za-z0-9_-]+\\b",
                SensitiveDataType.GOOGLE_OAUTH_KEY, true, "[REDACTED GOOGLE OAUTH TOKEN]"));

        rs.addRule(new RedactionRule("GitHub Token",
                "(?i)\\bgh[pousr]_[A-Za-z0-9]{32,40}\\b",
                SensitiveDataType.GITHUB_TOKEN, true, "[REDACTED GITHUB TOKEN]"));

        rs.addRule(new RedactionRule("Slack Token",
                "(?i)\\bxox[baprs]-[A-Za-z0-9-]{24,}\\b",
                SensitiveDataType.SLACK_TOKEN, true, "[REDACTED SLACK TOKEN]"));

        rs.addRule(new RedactionRule("SendGrid Key",
                "\\bSG\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\b",
                SensitiveDataType.SENDGRID_KEY, true, "[REDACTED SENDGRID KEY]"));

        rs.addRule(new RedactionRule("Generic / Stripe / OpenAI Key",
                "(?i)\\b(?:sk|pk|rk|api[_-]?key)[_-][A-Za-z0-9_-]{16,}\\b",
                SensitiveDataType.GENERIC_API_KEY, true, "[REDACTED API KEY]"));

        rs.addRule(new RedactionRule("JSON Web Token (JWT)",
                "\\beyJ[A-Za-z0-9_-]+\\.eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\b",
                SensitiveDataType.JWT, true, "[REDACTED JWT]"));

        rs.addRule(new RedactionRule("Password Assignment",
                "(?i)(?<=\\b|_|-)(?:password|passwd|pwd|pass)\\b\\s*(?:[-:=>]+|\\s+is\\s+|\\s+)\\s*(?:[\"'][^\"']+[\"']|[^\\s.,!?]+)",
                SensitiveDataType.PASSWORD, true, "[REDACTED PASSWORD]"));

        rs.addRule(new RedactionRule("IPv4 Address",
                "(?<!\\d)(?:\\d{1,3}\\.){3}\\d{1,3}(?!\\d)",
                SensitiveDataType.IP_ADDRESS, true, "[REDACTED IP]"));

        rs.addRule(new RedactionRule("IPv6 Address",
                "(?i)\\b(?:(?:[a-f0-9]{1,4}:){1,6}(?::[a-f0-9]{1,4}){1,6}|(?:[a-f0-9]{1,4}:){7}[a-f0-9]{1,4}|(?:[a-f0-9]{1,4}:){1,7}:|:(?::[a-f0-9]{1,4}){1,7}|::)\\b",
                SensitiveDataType.IP_ADDRESS, true, "[REDACTED IP]"));

        rs.addRule(new RedactionRule("Date of Birth (Numeric)",
                "(?i)(?:\\b(?:dob|date\\s*of\\s*birth|birth\\s*date|birthdate|born\\s*on|born|bday)\\b.{0,30}?(?<!\\d)(?:0?[1-9]|[12]\\d|3[01])[/-](?:0?[1-9]|1[0-2])(?:[/-](?:19|20)\\d{2})?(?!\\d)|(?<!\\d)(?:0?[1-9]|[12]\\d|3[01])[/-](?:0?[1-9]|1[0-2])(?:[/-](?:19|20)\\d{2})?(?!\\d).{0,30}?\\b(?:dob|date\\s*of\\s*birth|birth\\s*date|birthdate|born\\s*on|born|bday)\\b)",
                SensitiveDataType.DATE_OF_BIRTH, true, "[REDACTED DOB]"));

        rs.addRule(new RedactionRule("Date of Birth (Written)",
                "(?i)(?:\\b(?:dob|date\\s*of\\s*birth|birth\\s*date|birthdate|born\\s*on|born|bday)\\b.{0,30}?(?:0?[1-9]|[12]\\d|3[01])(?:st|nd|rd|th)?(?:\\s+of\\s+|\\s+)(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*(?:(?:,?|\\.?)\\s+(?:19|20)\\d{2})?|(?:0?[1-9]|[12]\\d|3[01])(?:st|nd|rd|th)?(?:\\s+of\\s+|\\s+)(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*(?:(?:,?|\\.?)\\s+(?:19|20)\\d{2})?.{0,30}?\\b(?:dob|date\\s*of\\s*birth|birth\\s*date|birthdate|born\\s*on|born|bday)\\b)",
                SensitiveDataType.DATE_OF_BIRTH, true, "[REDACTED DOB]"));

        rs.addRule(new RedactionRule("BSB & Account Number",
                "(?<!\\d)\\d{3}-\\d{3}(?:[\\s\\-]|\\s+acc(?:ount)?[:\\s-]*)+(?:\\d[\\s\\-]?){6,10}(?!\\d)",
                SensitiveDataType.BANK_ACCOUNT, true, "[REDACTED BANK ACCOUNT]"));

        rs.addRule(new RedactionRule("Australian Passport Number",
                "(?<![A-Z])[A-Z]{1,2}\\d{7}(?!\\d)",
                SensitiveDataType.PASSPORT, true, "[REDACTED PASSPORT NUMBER]"));

        rs.addRule(new RedactionRule("Medicare Card Number",
                "(?i)(?:\\b(?:medicare|med\\s*card)\\b.{0,30}?(?<!\\d)[2-6](?:[\\s-]?\\d){9}(?!\\d)|(?<!\\d)[2-6](?:[\\s-]?\\d){9}(?!\\d).{0,30}?\\b(?:medicare|med\\s*card)\\b)",
                SensitiveDataType.MEDICARE, true, "[REDACTED MEDICARE]"));

        rs.addRule(new RedactionRule("Centrelink CRN",
                "(?i)(?:\\b(?:centrelink|crn|customer\\s*reference\\s*number)\\b.{0,30}?(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}[\\s-]?[A-Z]\\b|(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}[\\s-]?[A-Z]\\b.{0,30}?\\b(?:centrelink|crn|customer\\s*reference\\s*number)\\b)",
                SensitiveDataType.CENTRELINK_NUMBER, true, "[REDACTED CENTRELINK NUMBER]"));

        rs.addRule(new RedactionRule("ABN",
                "(?i)(?:\\b(?:abn|australian\\s*business\\s*number|business\\s*no(?:\\.|mber)?)\\b.{0,30}?(?<!\\d)\\d{2}[\\s-]?\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d)|(?<!\\d)\\d{2}[\\s-]?\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d).{0,30}?\\b(?:abn|australian\\s*business\\s*number|business\\s*no(?:\\.|mber)?)\\b)",
                SensitiveDataType.ABN, true, "[REDACTED ABN]"));
        return rs;
    }
}