package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.RedactionResult;

/**
 * DAO interface for redaction results.
 *
 * <p>A redaction result is what we get back after running a prompt through the
 * redaction engine - the cleaned up text plus counts of what was found. Each
 * result is tied to one prompt, so everything here works off the prompt ID.</p>
 */
public interface RedactionResultDAO {
    /**
     * Saves a redaction result to the database.
     *
     * @param result the result to add
     */
    void addResult(RedactionResult result);

    /**
     * Gets the redaction result for a given prompt.
     *
     * @param promptId the ID of the prompt
     * @return the result for that prompt, or null if there isn't one
     */
    RedactionResult getByPromptId(int promptId);

    /**
     * Deletes the redaction result linked to a prompt.
     *
     * @param promptId the ID of the prompt whose result we want gone
     */
    void deleteByPromptId(int promptId);
}
