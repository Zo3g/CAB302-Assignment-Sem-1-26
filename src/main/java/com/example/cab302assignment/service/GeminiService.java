package com.example.cab302assignment.service;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.UserRiskScore;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

public class GeminiService implements LLMService{
    private final Client client;

    public GeminiService() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        String apiKey = dotenv.get("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("GEMINI_API_KEY");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing GEMINI_API_KEY.");
        }

        this.client = Client.builder().apiKey(apiKey).build();
    }

    @Override
    public String analysePromptRisk(String redactedText) {
        // Get system prompt
        String prompt = buildSystemPrompt(redactedText);

        // Get AI response
        String res = getAiResponse(prompt);
        //System.out.println(res);
        return res;
    }

    @Override
    public UserRiskScore calculateUserRiskScore(int userId, List<RiskAnalysis> history) {
        return null;
    }

    // Private helper methods
    private String getAiResponse(String prompt){
        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
        System.out.println("ai response"+ response);
        return response.text();
    }

    private String buildSystemPrompt(String redactedText){
        return """
            You are a senior cybersecurity compliance auditor.
            You will be provided with text that has been sanitized for security.
            Sensitive information is replaced with bracketed placeholders.
            
            Redacted text: 
            ---
            %s
            ---
            Objective:
            1. Analyze the categories of data represented by the placeholders and explain the specific compliance violations
            that would occur if all of those data were handled improperly (unredacted).
            2. Your task is to explain the compliance violation for the user to understand in simple words.
            3. Assess compliance risk only when context suggests actual handling of sensitive data, not mere mention.
            
            Important Rules:
            If sensitive data appears without operational context (e.g., isolated phone number, email, identifier):
            -> classify as: “Informational presence – no compliance breach inferred.”
            In this case:
                Do NOT infer processing, exposure, sharing, or misuse
                Do NOT mention any legal, regulatory, or compliance violations
                Do NOT include spam, phishing, fraud, or harm scenarios
                Effect must be strictly neutral, such as:
                   - No impact on systems or users can be determined from context
                   - No compliance or regulatory implications can be inferred
                   - Data presence alone does not indicate processing or exposure
            
            Determine an overall risk level based on the context of the redacted items.
                
            Constraints (You MUST adhere to this exact format):
            Score: <must be exactly one of: NO, LOW, MEDIUM, HIGH, CRITICAL>
            Risk: <short explanation in 3 bullet points>
            Effect: <effects of compliance violation in 3 bullet points>
            """.formatted(redactedText);
    }
}
