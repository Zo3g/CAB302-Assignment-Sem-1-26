package com.example.cab302assignment.dao;

import com.example.cab302assignment.model.Organisation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockOrganisationDAO implements OrganisationDAO {
    private final List<Organisation> organisations = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void addOrganisation(Organisation org) {
        for (Organisation existing : organisations) {
            if (existing.getName().equals(org.getName())) return;
        }
        org.setOrgId(nextId++);
        if (org.getCreatedAt() == null) org.setCreatedAt(LocalDateTime.now());
        organisations.add(org);
    }

    @Override
    public Organisation getOrganisationById(int orgId) {
        return organisations.stream()
            .filter(o -> o.getOrgId() == orgId)
            .findFirst()
            .orElse(null);
    }

    @Override
    public Organisation getOrganisationByName(String name) {
        return organisations.stream()
            .filter(o -> o.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Organisation> getAllOrganisations() {
        return new ArrayList<>(organisations);
    }

    @Override
    public void updateOrganisation(Organisation org) {
        for (int i = 0; i < organisations.size(); i++) {
            if (organisations.get(i).getOrgId() == org.getOrgId()) {
                organisations.set(i, org);
                return;
            }
        }
    }

    @Override
    public void deleteOrganisation(int orgId) {
        organisations.removeIf(o -> o.getOrgId() == orgId);
    }
}
