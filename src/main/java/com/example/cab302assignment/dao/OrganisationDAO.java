package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Organisation;

import java.util.List;

public interface OrganisationDAO {
    void addOrganisation(Organisation org);
    Organisation getOrganisationById(int orgId);
    Organisation getOrganisationByName(String name);
    List<Organisation> getAllOrganisations();
    void updateOrganisation(Organisation org);
    void deleteOrganisation(int orgId);
}
