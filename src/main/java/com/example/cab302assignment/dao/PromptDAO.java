package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Prompt;
import com.example.cab302assignment.model.PromptHistorySummary;

import java.util.List;

/**
 * DAO interface for prompts.
 *
 * <p>Prompts are the (already redacted) bits of text that users submit. This
 * interface covers saving them, fetching them back and building the history
 * summary we show on the dashboard.</p>
 */
public interface PromptDAO {
    /**
     * Saves a new prompt to the database.
     *
     * @param prompt the prompt to add
     */
    void addPrompt(Prompt prompt);

    /**
     * Finds a single prompt by its ID.
     *
     * @param promptId the prompt's ID
     * @return the prompt, or null if it isn't found
     */
    Prompt getPromptById(int promptId);

    /**
     * Gets all the prompts submitted by a particular user.
     *
     * @param userId the user's ID
     * @return a list of that user's prompts
     */
    List<Prompt> getPromptsByUser(int userId);

    /**
     * Gets all the prompts belonging to a particular organisation.
     *
     * @param orgId the organisation's ID
     * @return a list of prompts for that org
     */
    List<Prompt> getPromptsByOrg(int orgId);

    /**
     * Deletes a prompt by its ID.
     *
     * @param promptId the ID of the prompt to delete
     */
    void deletePrompt(int promptId);

    /**
     * Builds a summary of a user's prompt history for the dashboard table.
     *
     * <p>This one joins the prompt up with its risk analysis and redaction
     * result so the dashboard can show everything in one row.</p>
     *
     * @param userId the user's ID
     * @return a list of history summary rows for that user
     */
    List<PromptHistorySummary> getHistoryForUser(int userId);
}
