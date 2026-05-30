package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Organisation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A fake (in-memory) version of {@link OrganisationDAO} for testing.
 *
 * <p>Keeps organisations in an ArrayList rather than a real database so the
 * tests stay fast and simple. It also hands out IDs itself and fills in a
 * createdAt time if one wasn't already set.</p>
 */
public class MockOrganisationDAO implements OrganisationDAO {
    /** The in-memory list standing in for the organisations table. */
    private final List<Organisation> organisations = new ArrayList<>();

    /** The next ID to hand out. */
    private int nextId = 1;

    /**
     * Adds an organisation, but skips it if the name is already used. Gives it
     * a new ID and sets the created time to now if it wasn't provided.
     *
     * @param org the organisation to add
     */
    @Override
    public void addOrganisation(Organisation org) {
        for (Organisation existing : organisations) {
            if (existing.getName().equals(org.getName())) return;
        }
        org.setOrgId(nextId++);
        if (org.getCreatedAt() == null) org.setCreatedAt(LocalDateTime.now());
        organisations.add(org);
    }

    /**
     * Finds an organisation by its ID using a stream.
     *
     * @param orgId the organisation's ID
     * @return the organisation, or null if not found
     */
    @Override
    public Organisation getOrganisationById(int orgId) {
        return organisations.stream()
            .filter(o -> o.getOrgId() == orgId)
            .findFirst()
            .orElse(null);
    }

    /**
     * Finds an organisation by its name.
     *
     * @param name the name to match
     * @return the organisation, or null if not found
     */
    @Override
    public Organisation getOrganisationByName(String name) {
        return organisations.stream()
            .filter(o -> o.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Returns a copy of all the organisations.
     *
     * @return a list of every organisation
     */
    @Override
    public List<Organisation> getAllOrganisations() {
        return new ArrayList<>(organisations);
    }

    /**
     * Finds the matching organisation by ID and replaces it with the new one.
     *
     * @param org the organisation with updated details
     */
    @Override
    public void updateOrganisation(Organisation org) {
        for (int i = 0; i < organisations.size(); i++) {
            if (organisations.get(i).getOrgId() == org.getOrgId()) {
                organisations.set(i, org);
                return;
            }
        }
    }

    /**
     * Removes the organisation with the given ID.
     *
     * @param orgId the ID of the organisation to delete
     */
    @Override
    public void deleteOrganisation(int orgId) {
        organisations.removeIf(o -> o.getOrgId() == orgId);
    }
}
