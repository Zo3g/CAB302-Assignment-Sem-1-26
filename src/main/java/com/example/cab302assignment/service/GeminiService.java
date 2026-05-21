package com.example.cab302assignment.service;
import com.example.cab302assignment.model.RedactionResult;
import com.example.cab302assignment.model.RiskAnalysis;
import com.example.cab302assignment.model.UserRiskScore;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

/**
 * {@link LLMService} implementation backed by Google's Gemini API.
 *
 * <p>This service is responsible for sending redacted prompt text to a
 * Gemini model and returning a structured risk assessment. The Gemini API
 * key is resolved at construction time from from a {@code .env} file.</p>
 *
 * <p>The model used is {@code gemini-2.5-flash}, and the prompt is formatted
 * by {@link #buildSystemPrompt(String)} to constrain the response to a
 * fixed {@code Score / Risk / Effect} format that downstream parsers
 * rely on.</p>
 */
public class GeminiService implements LLMService{
    private final Client client;

    /**
     * Creates a new {@code GeminiService} and initialises the underlying
     * Gemini {@link Client}.
     *
     * <p>The API key is loaded in the following order, with the first
     * non-blank value being used:</p>
     * <ol>
     *   <li>The {@code GEMINI_API_KEY} entry of a {@code .env} file, ignored if missing).</li>
     *   <li>The {@code GEMINI_API_KEY} environment variable.</li>
     * </ol>
     *
     * @throws IllegalStateException if no API key can be found in either
     *                               location
     */
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

    /**
     * Analyses the risk of a redacted prompt by sending it to the Gemini
     * model.
     *
     * <p>The input is expected to have already had sensitive values replaced
     * with bracketed placeholders (typically by a {@link RedactionResult}).
     * The model returns text in the {@code Score / Risk / Effect} format
     * defined by {@link #buildSystemPrompt(String)}.</p>
     *
     * @param redactedText the prompt text after redaction
     * @return the raw model response describing the risk assessment
     */
    @Override
    public String analysePromptRisk(String redactedText) {
        // Get system prompt
        String prompt = buildSystemPrompt(redactedText);

        // Get AI response
        String res = getAiResponse(prompt);
        //System.out.println(res);
        return res;
    }

    /**
     * Calculates an aggregate {@link UserRiskScore} for the given user.
     *
     * <p>This implementation is currently a stub and always returns
     * {@code null}; aggregate scoring is performed locally by
     * {@link UserRiskScore#recalculate(List)} rather than via the Gemini
     * API.</p>
     *
     * @param userId  identifier of the user
     * @param history the user's historical risk analyses
     * @return {@code null} (not implemented)
     */
    @Override
    public UserRiskScore calculateUserRiskScore(int userId, List<RiskAnalysis> history) {
        return null;
    }

    /**
     * Sends the supplied prompt to the {@code gemini-2.5-flash} model and
     * returns the raw response text.
     *
     * @param prompt the fully formatted prompt to send to Gemini
     * @return the text portion of the {@link GenerateContentResponse}
     */
    private String getAiResponse(String prompt){
        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
        System.out.println("ai response"+ response);
        return response.text();
    }

    /**
     * Builds the system prompt sent to Gemini for risk analysis.
     *
     * <p>The prompt instructs the model to act as a cybersecurity compliance
     * auditor, evaluate the redacted text for compliance risk, and respond
     * in a strict format:</p>
     * <pre>
     *   Score: &lt;NO|LOW|MEDIUM|HIGH|CRITICAL&gt;
     *   Risk: &lt;3 bullet points&gt;
     *   Effect: &lt;3 bullet points&gt;
     * </pre>
     *
     * <p>Downstream parsing depends on this exact structure, so changes
     * here must be coordinated with the response parsing logic.</p>
     *
     * @param redactedText the redacted prompt text to embed in the system
     *                     prompt
     * @return the formatted prompt string ready to send to the model
     */
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
