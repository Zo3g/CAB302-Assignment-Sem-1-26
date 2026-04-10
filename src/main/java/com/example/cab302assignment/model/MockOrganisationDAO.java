package com.example.cab302assignment.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockOrganisationDAO implements OrganisationDAO {
    private List<Organisation> organisations = new ArrayList<>();
    private int autoIncrementedId = 1;

    public MockOrganisationDAO() {
        organisations.add(new Organisation(1, "OrgA", LocalDateTime.now()));
        organisations.add(new Organisation(2, "OrgB", LocalDateTime.now()));
    }

    @Override
    public void addOrganisation(Organisation organisation) {
        organisation.setId(autoIncrementedId++);
        organisations.add(organisation);
    }

    @Override
    public void updateOrganisation(Organisation organisation) {
        for (int i = 0; i < organisations.size(); i++) {
            if (organisations.get(i).getId() == organisation.getId()) {
                organisations.set(i, organisation);
                break;
            }
        }
    }

    @Override
    public void deleteOrganisation(int orgId) {
        organisations.removeIf(organisation -> organisation.getId() == orgId);
    }

    @Override
    public Organisation getOrganisationById(int orgId) {
        return organisations.stream()
            .filter(organisation -> organisation.getId() == orgId)
            .findFirst()
            .orElse(null);
    }
}
