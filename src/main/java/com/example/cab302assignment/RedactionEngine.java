package com.example.cab302assignment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedactionEngine {

    // 1. compile the patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("(?<!\\d)(?:4\\d{3}|5[1-5]\\d{2}|3[47]\\d{1})[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{3,4}(?!\\d)");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("(?i)[\\w\\s]{3,},\\s?(?:NSW|VIC|QLD|TAS|WA|SA|NT|ACT)\\s?\\d{4}");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("(?<![A-Z])[A-Z]{1,2}\\d{7}(?!\\d)");
    private static final Pattern IP_PATTERN = Pattern.compile("(?<!\\d)(?:\\d{1,3}\\.){3}\\d{1,3}(?!\\d)");
    private static final Pattern BSB_AND_ACC_PATTERN = Pattern.compile("(?i)(?<!\\d)(\\d{3}[\\s\\-]?\\d{3})[\\s\\-\\w]*(?:acc|account)?[\\s\\-]*(\\d{6,10})(?!\\d)");


    // 2. the main redaction logic
    public String redactPrompt(String input) {
        String safeText = input;
        int totalCaught = 0;

        // Process Emails
        Matcher emailMatcher = EMAIL_PATTERN.matcher(safeText);
        while (emailMatcher.find()) {
            System.out.println("Detected Email: " + emailMatcher.group());
            totalCaught++;
        }
        // Reset the matcher after counting, then replace the text
        safeText = emailMatcher.reset().replaceAll("[REDACTED EMAIL]");

        // Process Credit Cards
        Matcher creditcardMatcher = CREDIT_CARD_PATTERN.matcher(safeText);
        while (emailMatcher.find()) {
            System.out.println("Detected Credit Card: " + creditcardMatcher.group());
            totalCaught++;
        }
        safeText = creditcardMatcher.reset().replaceAll("[REDACTED CREDIT CARD]");

        // Process Addresses
        Matcher addressMatcher = ADDRESS_PATTERN.matcher(safeText);
        while (addressMatcher.find()) {
            System.out.println("Detected Address: " + addressMatcher.group());
            totalCaught++;
        }
        safeText = addressMatcher.reset().replaceAll("[REDACTED ADDRESS]");

        // Process Passport numbers
        Matcher passportMatcher = PASSPORT_PATTERN.matcher(safeText);
        while (passportMatcher.find()) {
            System.out.println("Detected Passport: " + passportMatcher.group());
            totalCaught++;
        }
        safeText = passportMatcher.reset().replaceAll("[REDACTED Passport]");

        // Process IP Addresses
        Matcher ipMatcher = IP_PATTERN.matcher(safeText);
        while (ipMatcher.find()) {
            System.out.println("Detected IP Address: " + ipMatcher.group());
            totalCaught++;
        }
        safeText = ipMatcher.reset().replaceAll("[REDACTED IP ADDRESS]");

        // Process Bank Details
        Matcher bsbANDaccMatcher = BSB_AND_ACC_PATTERN.matcher(safeText);
        while (bsbANDaccMatcher.find()) {
            System.out.println("Detected Bank Details: " + bsbANDaccMatcher.group());
            totalCaught++;
        }
        safeText = bsbANDaccMatcher.reset().replaceAll("[REDACTED BANK DETAILS]");

        System.out.println("Total sensitive items caught: " + totalCaught);
        System.out.println("-----------------------------------");

        return safeText;
    }

    // 3. test with both email and tfn
    public static void main(String[] args) {
        RedactionEngine engine = new RedactionEngine();

        // The sample prompt an employee might accidentally type
        String samplePrompt = "Please summarize this medical leave request. Forward the summary to dan@email.com and dan@gmail.com.au. The employee's TFN is 123456789.";

        System.out.println("--- AI Compliance Guard Test ---");
        System.out.println("Original Prompt:\n" + samplePrompt + "\n");

        String result = engine.redactPrompt(samplePrompt);

        System.out.println("Scrubbed Prompt:\n" + result);
    }
}
