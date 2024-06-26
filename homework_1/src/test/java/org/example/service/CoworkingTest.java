package org.example.service;

import org.example.TestUtils;
import org.example.model.*;
import org.example.utils.MemberAlreadyExistsException;
import org.example.utils.MemberNotFoundException;
import org.example.utils.WrongPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssertions.thenNoException;

class CoworkingTest {
    Coworking coworking;

    @BeforeEach
    void setUp() {
        coworking = new Coworking();
    }

    @Test
    void createAdminUser() throws MemberAlreadyExistsException, MemberNotFoundException, WrongPasswordException {
        String login = "adm1";
        String password = "apwd1";
        coworking.createAdminUser(login, password);
        assertThat(coworking.getUser(login, password).isAdmin()).isTrue();
    }

    @Test
    void registerNewUser() throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        assertThatThrownBy(() -> coworking.getUser(login, password)).isInstanceOf(MemberNotFoundException.class);
        coworking.registerNewUser(login, password);
        thenNoException().isThrownBy(() -> coworking.getUser(login, password));
    }

    @Test
    void register_existing_user_throws () throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThatThrownBy(() -> coworking.registerNewUser(login, "other"))
                .isInstanceOf(MemberAlreadyExistsException.class);
    }

    @Test
    void authenticateUser() throws MemberAlreadyExistsException, MemberNotFoundException, WrongPasswordException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThat(coworking.authenticateUser(login, password).getLogin()).isEqualTo(login);
    }

    @Test
    void auth_with_wrong_password_throws() throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThatThrownBy(() -> coworking.authenticateUser(login, "wrong"))
                .isInstanceOf(WrongPasswordException.class);
    }

    @Test
    void auth_non_existent_throws() throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThatThrownBy(() -> coworking.authenticateUser("non-existent", password))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void getUser() throws MemberAlreadyExistsException, MemberNotFoundException, WrongPasswordException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThat(coworking.getUser(login, password).getLogin()).isEqualTo(login);
    }

    @Test
    void get_user_wrong_password_throws () throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThatThrownBy(() -> coworking.getUser(login, "wrong"))
                .isInstanceOf(WrongPasswordException.class);
    }

    @Test
    void get_non_existent_user_throws () throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThatThrownBy(() -> coworking.getUser("non-existent", password))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void removeUser() throws MemberAlreadyExistsException, MemberNotFoundException, WrongPasswordException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThat(coworking.removeUser(login, password)).isTrue();
        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> coworking.getUser(login, password));
    }

    @Test
    void remove_user_wrong_password_throws() throws MemberAlreadyExistsException {
        String login = "u1";
        String password = "pwd1";
        coworking.registerNewUser(login, password);
        assertThatExceptionOfType(WrongPasswordException.class)
                .isThrownBy(() -> coworking.removeUser(login, "wrong"));
        thenNoException().isThrownBy(() -> coworking.getUser(login, password));
    }

    @Test
    void addFacility() throws MemberAlreadyExistsException {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var facilities = new ArrayList<>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).contains(room);
    }

    @Test
    void same_id_same_class_not_added() throws MemberAlreadyExistsException {
        Workstation workstation1 = new Workstation("ws", "Celeron");
        Workstation workstation2 = new Workstation("ws", "Core i5");
        coworking.addFacility(workstation1);
        assertThatThrownBy(() -> coworking.addFacility(workstation2)).isInstanceOf(MemberAlreadyExistsException.class);
        var facilities = new ArrayList<>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation1).doesNotContain(workstation2);
    }

    @Test
    void same_id_diff_class_not_added() throws MemberAlreadyExistsException {
        Workstation workstation = new Workstation("001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("001", 7);
        coworking.addFacility(workstation);
        assertThatThrownBy(() ->coworking.addFacility(room)).isInstanceOf(MemberAlreadyExistsException.class);
        var facilities = new ArrayList<>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).doesNotContain(room);
    }

    @Test
    void getFacility() throws MemberAlreadyExistsException, MemberNotFoundException {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var retrieved = coworking.getFacility("ws001");
        assertThat(retrieved).isSameAs(workstation);
    }

    @Test
    void removeFacility() throws MemberAlreadyExistsException, MemberNotFoundException {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        coworking.removeFacility("cr001");
        var facilities = new ArrayList<>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).doesNotContain(room);
    }

    @Test
    void addBooking() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 35);
        var start = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end = LocalDateTime.of(2024, 7, 1, 17, 0);
        var expectedBooking1 = new Booking(user, workstation, start, end);
        var expectedBooking2 = new Booking(user, room, start, end);
        coworking.addBooking(user, workstation, start, end);
        coworking.addBooking(user, room, start, end);
        assertThat(coworking.viewAllBookings()).hasSize(2)
                .contains(expectedBooking1, expectedBooking2);
    }

    @Test
    void whole_day_bookings_added() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 35);
        var start = LocalDateTime.of(2024, 7, 7, 0, 0);
        var end = LocalDateTime.of(2024, 7, 7, 23, 59);
        coworking.addBooking(user, workstation, start, end);
        coworking.addBooking(user, room, start, end);
        assertThat(coworking.viewAllBookings()).hasSize(2);
    }

    @Test
    void overlapping_booking_not_added() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 1, 17, 0);
        var start2 = LocalDateTime.of(2024, 7, 1, 12, 0);
        var end2 = LocalDateTime.of(2024, 7, 2, 11, 0);
        var booking1 = new Booking(user, workstation, start1, end1);
        var booking2 = new Booking(user, workstation, start2, end2);
        assertThat(booking1.isOverlapping(booking2)).isTrue();
        assertThat(booking1).isEqualTo(booking2);
        coworking.addBooking(user, workstation, start1, end1);
        assertThat(coworking.addBooking(user, workstation, start2, end2)).isFalse();
        var actualBookings = new TreeSet<>(coworking.viewAllBookings());
        assertThat(actualBookings).hasSize(1);
        assertThat(actualBookings.first().getStart()).isEqualTo(booking1.getStart());
    }

    @Test
    void same_start_same_end_not_added() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 1, 17, 0);
        var booking1 = new Booking(user, workstation, start1, end1);
        var booking2 = new Booking(null, workstation, start1, end1);
        assertThat(booking1.isOverlapping(booking2)).isTrue();
        assertThat(booking1).isEqualTo(booking2);
        coworking.addBooking(user, workstation, start1, end1);
        assertThat(coworking.addBooking(user, workstation, start1, end1)).isFalse();
        var actualBookings = coworking.viewAllBookings();
        assertThat(actualBookings).hasSize(1);
    }

    @Test
    void removeBooking() {
        User user = new User("u1", "pwd1");
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 35);
        var start = LocalDateTime.of(2024, 7, 1, 11, 0);
        var end = LocalDateTime.of(2024, 7, 1, 17, 0);
        var workstationBooking = new Booking(user, workstation, start, end);
        var roomBooking = new Booking(user, room, start, end);
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
        var workstationBooking = new Booking(user1, workstation, start, end);
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
        var workstationBooking = new Booking(user, workstation, start, end);
        var roomBooking = new Booking(user, room, start, end);
        coworking.addBooking(user, workstation, start, end);
        coworking.addBooking(user, room, start, end);
        assertThat(coworking.removeBooking(workstation, start, end, admin)).isTrue();
        assertThat(coworking.viewAllBookings()).hasSize(1)
                .contains(roomBooking).doesNotContain(workstationBooking);
    }

    @Test
    void getAvailableBookingSlots() throws MemberAlreadyExistsException {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Set<Facility> facilities = new HashSet<>(
                Set.of(
                new Workstation("ws001", "Celeron"),
                new Workstation("ws002", "Core i5"),
                new ConferenceRoom("cr001", 17))
        );
        assertThat(facilities).hasSize(3);
        facilities.forEach(facility -> {
            try {
                coworking.addFacility(facility);
            } catch (MemberAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
            TestUtils.addFiveBookings(facility, coworking, date);
        });
        var anotherFacility = new ConferenceRoom("cr002", 11);
        coworking.addFacility(anotherFacility);
        TestUtils.addTwoOutsideBookings(anotherFacility, coworking, date);
        assertThat(coworking.viewAllBookings()).hasSize(17);
        var freeSlots = coworking.getAvailableBookingSlots(date);
        assertThat(freeSlots).hasSize(4);
        freeSlots.values().forEach(System.out::println);
    }

}