package org.example.model;

import org.example.service.Coworking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class FacilityTest {
    Coworking coworking;

    @BeforeEach
    void setUp() {
        coworking = new Coworking();
    }

    @Test
    void getBookingsForDate() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        addFiveBookings(facility);
        var relevantBookings = facility.getBookingsForDate(date, coworking.viewAllBookings());
        assertThat(relevantBookings).hasSize(3);
        var allBookings = new ArrayList<>(coworking.viewAllBookings());
        allBookings.sort(null);
        assertThat(relevantBookings.first().getEnd()).isEqualTo(allBookings.get(1).getEnd());
        assertThat(relevantBookings.last().getStart()).isEqualTo(allBookings.get(3).getStart());
    }

    @Test
    void only_this_facility_bookings_picked() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        addFiveBookingsInclAnotherFacility(facility);
        var relevantBookings = facility.getBookingsForDate(date, coworking.viewAllBookings());
        assertThat(relevantBookings).hasSize(2);
        relevantBookings.forEach(boo -> assertThat(boo.getFacility()).isEqualTo(facility));
    }

    @Test
    void no_bookings_empty_list() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 6, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 6, 17, 0);
        coworking.addBooking(null, facility, start1, end1);
        var start5 = LocalDateTime.of(2024, 7, 8, 17, 0);
        var end5 = LocalDateTime.of(2024, 7, 8, 22, 0);
        coworking.addBooking(null, facility, start5, end5);
        assertThat(coworking.viewAllBookings()).hasSize(2);
        var relevantBookings = facility.getBookingsForDate(date, coworking.viewAllBookings());
        assertThat(relevantBookings).isEmpty();
    }

    @Test
    void booking_overstretching_both_ends_listed() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 6, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 8, 17, 0);
        coworking.addBooking(null, facility, start1, end1);
        var relevantBookings = facility.getBookingsForDate(date, coworking.viewAllBookings());
        assertThat(relevantBookings).hasSize(1);
        System.out.println("Booked: " + relevantBookings);
    }

    @Test
    void getFreeBookingSlotsForDate() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        addFiveBookings(facility);
        var freeSlots = facility.getFreeBookingSlotsForDate(date, coworking.viewAllBookings());
        assertThat(freeSlots).hasSize(2);
        System.out.println("Free: " + freeSlots);
    }

    @Test
    void overstretching_booking_free_slots_empty() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 6, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 8, 17, 0);
        coworking.addBooking(null, facility, start1, end1);
        assertThat(coworking.viewAllBookings()).hasSize(1);
        var freeSlots = facility.getFreeBookingSlotsForDate(date, coworking.viewAllBookings());
        assertThat(freeSlots).isEmpty();
    }

    @Test
    void single_booking_end_in_handled() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 6, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 7, 17, 0);
        coworking.addBooking(null, facility, start1, end1);
        assertThat(coworking.viewAllBookings()).hasSize(1);
        var freeSlots = facility.getFreeBookingSlotsForDate(date, coworking.viewAllBookings());
        assertThat(freeSlots).hasSize(1);
        var freeSlot = freeSlots.first();
        assertThat(freeSlot.getStart()).isEqualTo(end1);
        assertThat(freeSlot.getEnd()).isEqualTo(LocalDateTime.of(date, LocalTime.MAX));
    }

    @Test
    void single_booking_start_in_handled() {
        LocalDate date = LocalDate.of(2024, 7, 7);
        Facility facility = new Workstation("ws001", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 7, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 8, 17, 0);
        coworking.addBooking(null, facility, start1, end1);
        assertThat(coworking.viewAllBookings()).hasSize(1);
        var freeSlots = facility.getFreeBookingSlotsForDate(date, coworking.viewAllBookings());
        assertThat(freeSlots).hasSize(1);
        var freeSlot = freeSlots.first();
        assertThat(freeSlot.getStart()).isEqualTo(LocalDateTime.of(date, LocalTime.MIN));
        assertThat(freeSlot.getEnd()).isEqualTo(start1);
    }


    // UTILITY METHODS
    private void addFiveBookings(Facility facility) {
        var start1 = LocalDateTime.of(2024, 7, 6, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 6, 17, 0);
        coworking.addBooking(null, facility, start1, end1);
        var start2 = LocalDateTime.of(2024, 7, 6, 19, 0);
        var end2 = LocalDateTime.of(2024, 7, 7, 6, 0);
        coworking.addBooking(null, facility, start2, end2);
        var start3 = LocalDateTime.of(2024, 7, 7, 11, 0);
        var end3 = LocalDateTime.of(2024, 7, 7, 17, 0);
        coworking.addBooking(null, facility, start3, end3);
        var start4 = LocalDateTime.of(2024, 7, 7, 21, 0);
        var end4 = LocalDateTime.of(2024, 7, 8, 11, 0);
        coworking.addBooking(null, facility, start4, end4);
        var start5 = LocalDateTime.of(2024, 7, 8, 17, 0);
        var end5 = LocalDateTime.of(2024, 7, 8, 22, 0);
        coworking.addBooking(null, facility, start5, end5);
        assertThat(coworking.viewAllBookings()).hasSize(5);
    }

    private void addFiveBookingsInclAnotherFacility(Facility facility) {
        User user = new User("u1", "pwd1");
        Facility other = new Workstation("ws002", "Celeron");
        var start1 = LocalDateTime.of(2024, 7, 6, 11, 0);
        var end1 = LocalDateTime.of(2024, 7, 6, 17, 0);
        coworking.addBooking(user, other, start1, end1);
        var start2 = LocalDateTime.of(2024, 7, 6, 19, 0);
        var end2 = LocalDateTime.of(2024, 7, 7, 6, 0);
        coworking.addBooking(user, facility, start2, end2);
        var start3 = LocalDateTime.of(2024, 7, 7, 11, 0);
        var end3 = LocalDateTime.of(2024, 7, 7, 17, 0);
        coworking.addBooking(user, facility, start3, end3);
        var start4 = LocalDateTime.of(2024, 7, 7, 21, 0);
        var end4 = LocalDateTime.of(2024, 7, 8, 11, 0);
        coworking.addBooking(user, other, start4, end4);
        var start5 = LocalDateTime.of(2024, 7, 8, 17, 0);
        var end5 = LocalDateTime.of(2024, 7, 8, 22, 0);
        coworking.addBooking(user, facility, start5, end5);
        assertThat(coworking.viewAllBookings()).hasSize(5);
    }

}