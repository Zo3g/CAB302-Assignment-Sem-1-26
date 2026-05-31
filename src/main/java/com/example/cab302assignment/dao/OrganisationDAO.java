package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Organisation;

import java.util.List;

/**
 * DAO interface for organisations.
 *
 * <p>This sets out all the database operations we need for organisations.
 * Like the other DAOs it's an interface so we can swap in either the real
 * SQLite version or a mock one when we're testing.</p>
 */
public interface OrganisationDAO {
    /**
     * Saves a new organisation to the database.
     *
     * @param org the organisation to add
     */
    void addOrganisation(Organisation org);

    /**
     * Looks up an organisation by its ID.
     *
     * @param orgId the organisation's ID
     * @return the organisation, or null if it doesn't exist
     */
    Organisation getOrganisationById(int orgId);

    /**
     * Looks up an organisation by its name.
     *
     * @param name the organisation name to search for
     * @return the matching organisation, or null if none found
     */
    Organisation getOrganisationByName(String name);

    /**
     * Gets all the organisations in the database.
     *
     * @return a list of every organisation
     */
    List<Organisation> getAllOrganisations();

    /**
     * Updates an organisation's details.
     *
     * @param org the organisation with updated info (matched by ID)
     */
    void updateOrganisation(Organisation org);

    /**
     * Deletes an organisation by its ID.
     *
     * @param orgId the ID of the organisation to delete
     */
    void deleteOrganisation(int orgId);
}
