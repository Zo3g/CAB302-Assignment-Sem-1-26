package com.example.cab302assignment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedactionEngine {

    // Patterns enum for every type of sensitive data.
    private enum SensitiveDataType {
        ADDRESS("(?i)\\b\\d+(?:/\\d+)?[a-zA-Z]?\\s+[a-z]+(?:\\s+[a-z]+)*\\s+(?:Alley|Ally|Arcade|Arc|Avenue|Ave|Boulevard|Bvd|Bypass|Bypa|Circuit|Cct|Close|Cl|Corner|Crn|Court|Ct|Crescent|Cres|Cul-de-sac|Cds|Drive|Dr|Esplanade|Esp|Green|Grn|Grove|Gr|Highway|Hwy|Junction|Jnc|Lane|Link|Mews|Parade|Pde|Place|Pl|Ridge|Rdge|Road|Rd|Square|Sq|Street|St|Terrace|Tce)\\b", "[REDACTED ADDRESS]"),
        BSB_ACCOUNT("(?i)(?<!\\d)(\\d{3}[\\s\\-]?\\d{3})(?:[\\s\\-]|\\s+acc(?:ount)?[:\\s-]*)+(\\d{6,10})(?!\\d)", "[REDACTED BANKING]"),
        CREDIT_CARD("(?<!\\d)(?:4\\d{3}|5[1-5]\\d{2}|3[47]\\d{1})[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{3,4}(?!\\d)", "[REDACTED CREDIT CARD]"),
        EMAIL("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "[REDACTED EMAIL]"),
        IPV4_ADDRESS("(?<!\\d)(?:\\d{1,3}\\.){3}\\d{1,3}(?!\\d)", "[REDACTED IPv4 ADDRESS]"),
        IPV6_ADDRESS("(?i)\\b(?:(?:[a-f0-9]{1,4}:){1,6}(?::[a-f0-9]{1,4}){1,6}|(?:[a-f0-9]{1,4}:){7}[a-f0-9]{1,4}|(?:[a-f0-9]{1,4}:){1,7}:|:(?::[a-f0-9]{1,4}){1,7}|::)\\b","[REDACTED IPv6 ADDRESS]"),
        PASSPORT("(?<![A-Z])[A-Z]{1,2}\\d{7}(?!\\d)", "[REDACTED PASSPORT NUMBER]"),
        PHONE_NUMBER("(?<!\\d)(?:\\+?61|0)\\s?(\\s?\\(?[2-478]\\)?\\s?)(?:[\\s-]?\\d){8}(?!\\d)", "[REDACTED PHONE]");

        private final Pattern pattern;
        private final String replacementTag;

        // Constructor that compiles the regex as soon as the app starts
        SensitiveDataType(String regex, String replacementTag) {
            this.pattern = Pattern.compile(regex);
            this.replacementTag = replacementTag;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public String getReplacementTag() {
            return replacementTag;
        }
    }

    // 2. logic to redact the prompt
    public String redactPrompt(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String safeText = input;
        int totalCaught = 0;

        // Loop through every value defined in the Enum above
        for (SensitiveDataType dataType : SensitiveDataType.values()) {
            Matcher matcher = dataType.getPattern().matcher(safeText);

            int typeCount = 0;
            while (matcher.find()) {
                typeCount++;
                totalCaught++;
            }

            // Replace and print if we actually found something for this specific type
            if (typeCount > 0) {
                System.out.println("Detected " + typeCount + " instance(s) of " + dataType.name());
                safeText = matcher.reset().replaceAll(dataType.getReplacementTag());
            }
        }

        if (totalCaught > 0) {
            System.out.println("Total sensitive items scrubbed: " + totalCaught);
            System.out.println("-----------------------------------");
        }

        return safeText;
    }
}