package com.example.cab302assignment;

import com.example.cab302assignment.service.RedactionEngine;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RedactionRule;
import com.example.cab302assignment.model.enums.SensitiveDataType;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedactionTest {

    RedactionEngine redactionEngine = new RedactionEngine();


    //*Tests for The redaction process itself, rule disabling, and results tallying

    //Test for tallying
    @Test
    void testRedactionResultTallying() {
        // This tests that the engine correctly counts multiple instances and maps them to the right enums
        String prompt = "Send the invoice to finance@company.com and copy director@company.com. Call me on 0412 345 678.";

        RedactionResult result = redactionEngine.redact(prompt);

        // Check the text was successfully redacted
        String expectedText = "Send the invoice to [REDACTED EMAIL] and copy [REDACTED EMAIL]. Call me on [REDACTED PHONE].";
        assertEquals(expectedText, result.getRedactedText());

        // Check the total detections is exactly 3
        assertEquals(3, result.getTotalDetections());

        // Check the specific type counts map is accurate
        Map<SensitiveDataType, Integer> counts = result.getTypeCounts();
        assertEquals(2, counts.get(SensitiveDataType.EMAIL_ADDRESS));
        assertEquals(1, counts.get(SensitiveDataType.PHONE_NUMBER));

        // Assert that a rule that wasn't triggered doesn't appear in the counts
        assertNull(counts.get(SensitiveDataType.CREDIT_CARD));
    }

    //Test for ignoring disabled rules
    @Test
    void testDisabledRuleIgnoresMatch() {
        String prompt = "My email is test@company.com";

        // Find the email rule in the active ruleset and disable it
        for (RedactionRule rule : redactionEngine.getActiveRuleset().getRules()) {
            if (rule.getDataType() == SensitiveDataType.EMAIL_ADDRESS) {
                rule.setEnabled(false);
                break;
            }
        }
        RedactionResult result = redactionEngine.redact(prompt);

        // Check that because the rule is disabled, the text remains completely unredacted
        assertEquals(prompt, result.getRedactedText());
        assertEquals(0, result.getTotalDetections());
        assertNull(result.getTypeCounts().get(SensitiveDataType.EMAIL_ADDRESS));
    }

    //Test for null or empty input
    @Test
    void testNullAndEmptyInputs() {

        // 1. Test Null input
        RedactionResult nullResult = redactionEngine.redact(null);
        assertNull(nullResult.getRedactedText());
        assertEquals(0, nullResult.getTotalDetections());
        assertTrue(nullResult.getTypeCounts().isEmpty());

        // 2. Test Empty String input
        RedactionResult emptyResult = redactionEngine.redact("");
        assertEquals("", emptyResult.getRedactedText());
        assertEquals(0, emptyResult.getTotalDetections());
        assertTrue(emptyResult.getTypeCounts().isEmpty());
    }


    //*Tests for Regular Expressions

    // Email tests
    @Test
    void testEmailRedaction() {
        // 1. A basic email address.
        String prompt1 = "john@gmail.com";
        String expected1 = "[REDACTED EMAIL]";
        String actual1 = redactionEngine.redactPrompt(prompt1);
        assertEquals(expected1, actual1);

        // 2. An organisation email address
        String prompt2 = "john@university.edu.au and john@connect.university.edu.au";
        String expected2 = "[REDACTED EMAIL] and [REDACTED EMAIL]";
        String actual2 = redactionEngine.redactPrompt(prompt2);
        assertEquals(expected2, actual2);
    }


    // Phone number tests
    // Checks mobile numbers that are unseparated, ones that are hyphenated, and ones separated by spaces.
    @Test
    void testPhoneNumberRedaction() {
        // 1. Mobile numbers used domestically start with 04.
        String prompt1 = "0483838336, 0434-523-873, 0434 523 873, 0543838556";
        String expected1 = "[REDACTED PHONE], [REDACTED PHONE], [REDACTED PHONE], 0543838556";
        String actual1 = redactionEngine.redactPrompt(prompt1);
        assertEquals(expected1, actual1);

        // 2. Mobile numbers used internationally start with +61
        String prompt2 = "+61466450989, +61466-450-989, +61466 450 989, +61 466 450 989, +61 466-450-989, 61466123112";
        String expected2 = "[REDACTED PHONE], [REDACTED PHONE], [REDACTED PHONE], [REDACTED PHONE], [REDACTED PHONE], [REDACTED PHONE]";
        String actual2 = redactionEngine.redactPrompt(prompt2);
        assertEquals(expected2, actual2);

        // Landline numbers start with an area code dependent on the state or territory in parentheses,
        // followed by 8 digits
        String prompt3 = "(07) 3838 3939, (02)38383939, (03) 3838-3939";
        String expected3 = "[REDACTED PHONE], [REDACTED PHONE], [REDACTED PHONE]";
        String actual3 = redactionEngine.redactPrompt(prompt3);
        assertEquals(expected3, actual3);
    }


    //Street address tests
    @Test
    void testStreetAddressRedaction() {
        String prompt = "70 test street, 110 TEST ST, 19/44 Guardia Avenue";
        String expected = "[REDACTED ADDRESS], [REDACTED ADDRESS], [REDACTED ADDRESS]";
        String actual = redactionEngine.redactPrompt(prompt);
        assertEquals(expected, actual);
    }

    // Credit Card Tests
    @Test
    void testCreditCardRedaction() {
        // 1. Mastercards start between 51 - 55, or between 2221 - 2720, and are 16 digits long (formatted as 4-4-4).
        String prompt1 = "5120-1200-2393-3200, 5593 3923 2302 3203, 5323 8658 3848 3939, 2700 2181 2191 2191, 2730 2181 2191 2191, 3120 3282 3292 3202";
        String expected1 = "[REDACTED CREDIT CARD], [REDACTED CREDIT CARD], [REDACTED CREDIT CARD], [REDACTED CREDIT CARD], 2730 2181 2191 2191, 3120 3282 3292 3202";
        String actual1 = redactionEngine.redactPrompt(prompt1);
        assertEquals(expected1, actual1);

        // 2. Visa cards start with 4 and are 16 digits long (formatted as 4-4-4).
        String prompt2 = "4123 4567 8912 3456, 4929 1234 5678 9123";
        String expected2 = "[REDACTED CREDIT CARD], [REDACTED CREDIT CARD]";
        String actual2 = redactionEngine.redactPrompt(prompt2);
        assertEquals(expected2, actual2);

        // 3. AMEX cards start with 34 or 37, and are 15 digits long (formatted as 4-6-5).
        String prompt3 = "3720 120032 23332, 3420 120032 23332";
        String expected3 = "[REDACTED CREDIT CARD], [REDACTED CREDIT CARD]";
        String actual3 = redactionEngine.redactPrompt(prompt3);
        assertEquals(expected3, actual3);
    }

    //IP Address tests
    @Test
    void testIPAddressRedaction() {
        // Testing both IPv4 and IPv6
        String prompt = "192.168.1.1, 2001:0db8:85a3:0000:0000:8a2e:0370:7334, 2001:db8:2231:aaec::4a4a:2100.";
        String expected = "[REDACTED IP], [REDACTED IP], [REDACTED IP].";
        assertEquals(expected, redactionEngine.redactPrompt(prompt));
    }


    //BSB&ACC tests
    @Test
    void testBankAccountRedaction() {
        String prompt1 = "064-123 1234 5678, 064-123 acc 12345678.";
        String expected1 = "[REDACTED BANK ACCOUNT], [REDACTED BANK ACCOUNT].";
        assertEquals(expected1, redactionEngine.redactPrompt(prompt1));

    }

    // Passport number tests
    @Test
    void testPassportRedaction() {
        // 1 or 2 letter prefixes with 7 digits
        String prompt = "E1234567, PA1234567.";
        String expected = "[REDACTED PASSPORT NUMBER], [REDACTED PASSPORT NUMBER].";
        assertEquals(expected, redactionEngine.redactPrompt(prompt));
    }

    //API key tests
    @Test
    void testAPIKeys() {
        String prompt = "AWS: AKIAIOSFODNN7EXAMPLE\n" +
                "GCP: AIzaSyB-exampleKey1234567890\n" +
                "Google OAuth: ya29.a0AfH6SMC1234567890abcdefghijklmnopqrstuvwxyz\n" +
                "GitHub: ghp_16C7e42F292c6912E7710c838347Ae178B4a\n" +
                "Slack: xoxb-123456789012-123456789012-abcdefghijklmnopqrstuvwx\n" +
                "SendGrid: SG.abcdefghijklmnopqrstuvwxyz.1234567890ABCDEFGHIJKL\n" +
                "OpenAI/Stripe: sk_live_abc123DEF456ghi789JKL098\n" +
                "JWT: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        String expected = "AWS: [REDACTED API KEY]\n" +
                "GCP: [REDACTED API KEY]\n" +
                "Google OAuth: [REDACTED API KEY]\n" +
                "GitHub: [REDACTED API KEY]\n" +
                "Slack: [REDACTED API KEY]\n" +
                "SendGrid: [REDACTED API KEY]\n" +
                "OpenAI/Stripe: [REDACTED API KEY]\n" +
                "JWT: [REDACTED API KEY]";

        assertEquals(expected, redactionEngine.redactPrompt(prompt));
    }

    //*'Context-Aware' Tests.  These are regular expressions that require specific
    // phrases in the vicinity of the value in order to match it.

    //TFN tests
    @Test
    void test_TFN_Redaction() {
        // 1. Should catch because of the "TFN" keyword nearby
        String prompt1 = "Please update his TFN to 123 456 789 in the system.";
        String expected1 = "Please update his [REDACTED TFN] in the system.";
        assertEquals(expected1, redactionEngine.redactPrompt(prompt1));

        // 2. Should NOT catch because it's just a random 9-digit number with no context
        String prompt2 = "The employee ID is 123 456 789.";
        assertEquals(prompt2, redactionEngine.redactPrompt(prompt2));
    }

    //Password tests
    @Test
    void testPasswordRedaction() {
        // Testing different assignment formats and shorthands
        String prompt = "db_password = \"superSecret123\". pwd is hunter2. pass: Hello. Pass - ABC123";
        String expected = "db_[REDACTED PASSWORD]. [REDACTED PASSWORD]. [REDACTED PASSWORD]. [REDACTED PASSWORD]";
        assertEquals(expected, redactionEngine.redactPrompt(prompt));
    }

    //DOB tests
    @Test
    void test_DOB_Redaction() {
        // 1. Numeric DOB with context keyword
        String prompt1 = "The patient dob is 12/10/1990 for the record.";
        String expected1 = "The patient [REDACTED DOB] for the record.";
        assertEquals(expected1, redactionEngine.redactPrompt(prompt1));

        // 2. Written DOB without a year, with context keyword
        String prompt2 = "Her birthdate is 3rd of March.";
        String expected2 = "Her [REDACTED DOB].";
        assertEquals(expected2, redactionEngine.redactPrompt(prompt2));

        // 3. Should NOT catch generic dates missing the keywords
        String prompt3 = "The project is due on 12/10/2026.";
        assertEquals(prompt3, redactionEngine.redactPrompt(prompt3));
    }

    //ABN number tests
    @Test
    void test_ABN_Redaction() {
        // Testing the 2-3-3-3 spacing with keyword
        String prompt = "The contractor's ABN is 51824753556. ABN is 51 824 753 556.";
        String expected = "The contractor's [REDACTED ABN]. [REDACTED ABN].";
        assertEquals(expected, redactionEngine.redactPrompt(prompt));
    }

    //Medicare number tests
    @Test
    void testMedicareRedaction() {
        // 1. 10-digit number with context keyword
        String prompt1 = "Can you update Medicare card 2123 456 789 in the database?";
        String expected1 = "Can you update [REDACTED MEDICARE] in the database?";
        assertEquals(expected1, redactionEngine.redactPrompt(prompt1));

        // 2. Should NOT catch 10-digit number without context
        String prompt2 = "The generated ID code is 2123456789.";
        assertEquals(prompt2, redactionEngine.redactPrompt(prompt2));
    }

    //Driver's licence Number tests
    @Test
    void testDriversLicenceRedaction() {
        // 1. 9-digit number with context keyword
        String prompt1 = "His QLD licence is 123 456 789.";
        String expected1 = "His [REDACTED DRIVER'S LICENCE].";
        assertEquals(expected1, redactionEngine.redactPrompt(prompt1));

        // 2. Should NOT catch 9-digit number without context
        String prompt2 = "Transaction ID 123456789 failed.";
        assertEquals(prompt2, redactionEngine.redactPrompt(prompt2));
    }

    //Centrelink CRN tests
    @Test
    void testCentrelinkRedaction() {
        // 1. 9 digits + 1 letter with context keyword
        String prompt1 = "Centrelink CRN 111 222 333 A is on file.";
        String expected1 = "[REDACTED CENTRELINK NUMBER] is on file.";
        assertEquals(expected1, redactionEngine.redactPrompt(prompt1));

        // 2. Exact format without context
        String prompt2 = "Product code 111 222 333 A.";
        assertEquals(prompt2, redactionEngine.redactPrompt(prompt2));
    }

    //Extensive prompt test
    @Test
    void testExtensivePromptRedaction() {
        String prompt = "Hello, my name is daniel and my email is daniel@gmail.com, my phone number is 0466783112, other information of mine: PA1234567, 5123 1232 3832 3832, 78 Balmoral St, password: POO, 065-182 1928 2919.";
        String expected = "Hello, my name is daniel and my email is [REDACTED EMAIL], my phone number is [REDACTED PHONE], other information of mine: [REDACTED PASSPORT NUMBER], [REDACTED CREDIT CARD], [REDACTED ADDRESS], [REDACTED PASSWORD], [REDACTED BANK ACCOUNT].";
        assertEquals(expected, redactionEngine.redactPrompt(prompt));
    }
}



