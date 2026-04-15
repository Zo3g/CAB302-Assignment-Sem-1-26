package com.example.cab302assignment.db;

import com.example.cab302assignment.dao.OrganisationDAO;
import com.example.cab302assignment.dao.OrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteOrganisationDAO;
import com.example.cab302assignment.dao.SqliteOrganisationMembershipDAO;
import com.example.cab302assignment.dao.SqliteUserDAO;
import com.example.cab302assignment.dao.UserDAO;
import com.example.cab302assignment.model.Organisation;
import com.example.cab302assignment.model.OrganisationMembership;
import com.example.cab302assignment.model.User;
import com.example.cab302assignment.model.enums.MemberRole;
import com.example.cab302assignment.service.PasswordUtil;

import java.time.LocalDateTime;

public class DebugSeeder {

    public static final String ORG_NAME = "Guardia Debug";
    public static final String DEBUG_PASSWORD = "Debug123";

    private static final String[][] DEBUG_USERS = {
        {"charlie@guardia.com", "Charlie"},
        {"joyanne@guardia.com", "Joyanne"},
        {"zoe@guardia.com",     "Zoe"},
        {"dan@guardia.com",     "Dan"},
        {"zefira@guardia.com",  "Zefira"},
    };

    public static void seed() {
        seed(new SqliteUserDAO(), new SqliteOrganisationDAO(), new SqliteOrganisationMembershipDAO());
    }

    public static void seed(UserDAO userDAO, OrganisationDAO orgDAO, OrganisationMembershipDAO membershipDAO) {
        Organisation org = orgDAO.getOrganisationByName(ORG_NAME);
        if (org == null) {
            org = new Organisation(ORG_NAME);
            orgDAO.addOrganisation(org);
        }

        for (String[] entry : DEBUG_USERS) {
            String email = entry[0];
            String name = entry[1];

            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                user = new User(email, name, PasswordUtil.hashPassword(DEBUG_PASSWORD));
                userDAO.addUser(user);
            }

            if (membershipDAO.getMembership(user.getUserId(), org.getOrgId()) == null) {
                membershipDAO.addMembership(new OrganisationMembership(
                    user.getUserId(), org.getOrgId(), MemberRole.MANAGER, true, LocalDateTime.now()
                ));
            }
        }
    }
}
