package org.example.service;

import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CoworkingTest {
    Coworking coworking;

    @BeforeEach
    void setUp() {
        coworking = new Coworking();
    }

    @Test
    void addFacility() {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).contains(room);
    }

    @Test
    void same_id_same_class_not_added() {
        Workstation workstation1 = new Workstation("ws", "Celeron");
        Workstation workstation2 = new Workstation("ws", "Core i5");
        coworking.addFacility(workstation1);
        coworking.addFacility(workstation2);
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation1).doesNotContain(workstation2);
    }

    @Test
    void same_id_diff_class_not_added() {
        Workstation workstation = new Workstation("001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).doesNotContain(room);
    }

    @Test
    void getFacility() {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var retrieved = coworking.getFacility("ws001");
        assertThat(retrieved).isSameAs(workstation);
    }

    @Test
    void removeFacility() {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        coworking.removeFacility("cr001");
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).doesNotContain(room);
    }

    @Test
    void addBooking() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 35);
        var start = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end = LocalDateTime.of(2024, 7, 1, 17, 0);
        var expectedBooking1 = new Booking(workstation, start, end, user);
        var expectedBooking2 = new Booking(room, start, end, user);
        coworking.addBooking(user, workstation, start, end);
        coworking.addBooking(user, room, start, end);
        assertThat(coworking.viewAllBookings()).hasSize(2)
                .contains(expectedBooking1, expectedBooking2);
    }

    @Test
    void overlapping_booking_not_added() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 1, 17, 0);
        var start2 = LocalDateTime.of(2024, 7, 1, 12, 0);
        var end2 = LocalDateTime.of(2024, 7, 2, 11, 0);
        var booking1 = new Booking(workstation, start1, end1, user);
        var booking2 = new Booking(workstation, start2, end2, user);
        assertThat(booking1.isOverlapping(booking2)).isTrue();
        assertThat(booking1).isEqualTo(booking2);
        coworking.addBooking(user, workstation, start1, end1);
        assertThat(coworking.addBooking(user, workstation, start2, end2)).isFalse();
        var actualBookings = coworking.viewAllBookings();
        assertThat(actualBookings).hasSize(1);
        assertThat(actualBookings.get(0).getStart()).isEqualTo(booking1.getStart());
    }

    @Test
    void removeBooking() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 35);
        var start = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end = LocalDateTime.of(2024, 7, 1, 17, 0);
        var workstationBooking = new Booking(workstation, start, end, user);
        var roomBooking = new Booking(room, start, end, user);
        coworking.addBooking(user, workstation, start, end);
        coworking.addBooking(user, room, start, end);
        assertThat(coworking.removeBooking(workstation, start, end, user)).isTrue();
        var actualBookings = coworking.viewAllBookings();
        assertThat(actualBookings).hasSize(1).contains(roomBooking).doesNotContain(workstationBooking);
    }

    @Test
    void wrong_user_cant_remove_booking() {
        User user1 = new User("u1", "pwd1");
        User user2 = new User("u2", "pwd2");
        Workstation workstation = new Workstation("ws001", "Celeron");
        var start = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end = LocalDateTime.of(2024, 7, 1, 17, 0);
        var workstationBooking = new Booking(workstation, start, end, user1);
        coworking.addBooking(user1, workstation, start, end);
        assertThatThrownBy(() -> coworking.removeBooking(workstation, start, end, user2))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(coworking.viewAllBookings()).hasSize(1)
                .contains(workstationBooking);
    }

    @Test
    void admin_can_remove_others_booking() {
        User user = new User("u1", "pwd1");
        User admin = new User("adm1", "apwd1", true);
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 35);
        var start = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end = LocalDateTime.of(2024, 7, 1, 17, 0);
        var workstationBooking = new Booking(workstation, start, end, user);
        var roomBooking = new Booking(room, start, end, user);
        coworking.addBooking(user, workstation, start, end);
        coworking.addBooking(user, room, start, end);
        assertThat(coworking.removeBooking(workstation, start, end, admin)).isTrue();
        assertThat(coworking.viewAllBookings()).hasSize(1)
                .contains(roomBooking).doesNotContain(workstationBooking);
    }

}