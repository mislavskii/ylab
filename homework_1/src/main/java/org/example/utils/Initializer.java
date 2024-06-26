package org.example.utils;

import org.example.model.ConferenceRoom;
import org.example.model.Facility;
import org.example.model.User;
import org.example.model.Workstation;
import org.example.service.Coworking;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Initializer {

    public static void initialize(Coworking coworking) {
        populateFacilities(coworking);
        addAUserAndAdmin(coworking);
        addSomeBookings(coworking);
    }


    public static void populateFacilities(Coworking coworking) {
        Set<Facility> facilities = new HashSet<>(
                Set.of(
                        new Workstation("ws001", "Celeron"),
                        new Workstation("ws002", "Core i5"),
                        new Workstation("ws003", "Core i7"),
                        new ConferenceRoom("cr001", 17),
                        new ConferenceRoom("cr002", 11)
                )
        );
        facilities.forEach(facility -> {
            try {
                coworking.addFacility(facility);
            } catch (MemberAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void addAUserAndAdmin(Coworking coworking) {
        try {
            coworking.createAdminUser("admin", "admin");
            coworking.registerNewUser("u1", "pwd1");
        } catch (MemberAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addSomeBookings(Coworking coworking) {
        try {
            coworking.addBooking(
                    new User("u1", "pwd1"),
                    coworking.getFacility("ws002"),
                    LocalDateTime.of(2024, 7, 7, 11, 0),
                    LocalDateTime.of(2024, 7, 7, 13, 0)
            );
            coworking.addBooking(
                    new User("u1", "pwd1"),
                    coworking.getFacility("ws002"),
                    LocalDateTime.of(2024, 7, 7, 15, 0),
                    LocalDateTime.of(2024, 7, 7, 17, 0)
            );
            coworking.addBooking(
                    new User("u1", "pwd1"),
                    coworking.getFacility("cr002"),
                    LocalDateTime.of(2024, 7, 6, 11, 0),
                    LocalDateTime.of(2024, 7, 7, 17, 0)
            );
        } catch (MemberNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
