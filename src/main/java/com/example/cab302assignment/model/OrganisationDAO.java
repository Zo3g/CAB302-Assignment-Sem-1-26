package com.example.cab302assignment.model;

public interface OrganisationDAO {
    // CRUD
    public void addOrganisation(Organisation organisation);
    public void updateOrganisation(Organisation organisation);
    public void deleteOrganisation(int orgId);
    public Organisation getOrganisationById(int OrgId);
    // GetAllOrg?
}