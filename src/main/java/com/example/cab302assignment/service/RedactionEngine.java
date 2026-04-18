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
        rs.addRule(new RedactionRule("Email Address",
                "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                SensitiveDataType.EMAIL_ADDRESS, true, "[REDACTED EMAIL]"));

        rs.addRule(new RedactionRule("Australian Phone Number",
                "(?<!\\d)(?:\\+?61|0)\\s?(\\s?\\(?[2-478]\\)?\\s?)(?:[\\s-]?\\d){8}(?!\\d)",
                SensitiveDataType.PHONE_NUMBER, true, "[REDACTED PHONE]"));

        rs.addRule(new RedactionRule("Street Address",
                "(?i)\\b\\d+(?:/\\d+)?[a-zA-Z]?\\s+[a-z]+(?:\\s+[a-z]+)*\\s+(?:Alley|Ally|Arcade|Arc|Avenue|Ave|Boulevard|Bvd|Bypass|Bypa|Circuit|Cct|Close|Cl|Corner|Crn|Court|Ct|Crescent|Cres|Cul-de-sac|Cds|Drive|Dr|Esplanade|Esp|Green|Grn|Grove|Gr|Highway|Hwy|Junction|Jnc|Lane|Link|Mews|Parade|Pde|Place|Pl|Ridge|Rdge|Road|Rd|Square|Sq|Street|St|Terrace|Tce)\\b",
                SensitiveDataType.ADDRESS, true, "[REDACTED ADDRESS]"));

        rs.addRule(new RedactionRule("Credit Card",
                "(?<!\\d)(?:4\\d{3}(?:[\\s\\-]?\\d{4}){3}|(?:5[1-5]\\d{2}|2[2-7]\\d{2})(?:[\\s\\-]?\\d{4}){3}|3[47]\\d{2}[\\s\\-]?\\d{6}[\\s\\-]?\\d{5})(?!\\d)",
                SensitiveDataType.CREDIT_CARD, true, "[REDACTED CREDIT CARD]"));

        rs.addRule(new RedactionRule("TFN",
                "(?i)(?:\\b(?:tfn|tax\\s*file\\s*number|tax\\s*number)\\b.{0,30}?(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d)|(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d).{0,30}?\\b(?:tfn|tax\\s*file\\s*number|tax\\s*number)\\b)",
                SensitiveDataType.TFN, true, "[REDACTED TFN]"));

        rs.addRule(new RedactionRule("QLD Driver's License Number",
                "(?i)(?:\\b(?:driver[s']?\\s*licen[cs]e|licen[cs]e\\s*num(?:ber)?|licen[cs]e\\s*no\\b|crn|qld\\s*licen[cs]e)\\b.{0,30}?(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d)|(?<!\\d)\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3}(?!\\d).{0,30}?\\b(?:driver[s']?\\s*licen[cs]e|licen[cs]e\\s*num(?:ber)?|licen[cs]e\\s*no\\b|crn|qld\\s*licen[cs]e)\\b)",
                SensitiveDataType.DRIVERS_LICENCE, true, "[REDACTED DRIVER'S LICENCE]"));

        rs.addRule(new RedactionRule("API Key",
                "(?i)\\b(?:sk|pk|api[_-]?key)[_-][A-Za-z0-9]{16,}\\b",
                SensitiveDataType.API_KEY, true, "[REDACTED API KEY]"));

        rs.addRule(new RedactionRule("Password Assignment",
                "(?i)\\b(?:password|passwd|pwd|pass)\\b\\s*[\"']?\\s*(?:[:=]|\\s+is\\s+)\\s*[\"']?\\S+",
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
