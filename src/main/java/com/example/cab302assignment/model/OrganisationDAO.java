package com.example.cab302assignment.model;

public interface OrganisationDAO {
    // CRUD
    public void addOrganisation(Organisation organisation);
    public void updateOrganisation(Organisation organisation);
    public void deleteOrganisation(int orgId);
    public Organisation getOrganisationByID(int OrgId);
    public Organisation getOrganisationByUser(int userId);
    // GetAllOrg?
}