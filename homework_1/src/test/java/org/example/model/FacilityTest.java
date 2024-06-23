package org.example.model;

import org.example.TestUtils;
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
        TestUtils.addFiveBookings(facility, coworking, date);
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
        TestUtils.addFiveBookingsInclAnotherFacility(facility, coworking);
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
        TestUtils.addFiveBookings(facility, coworking, date);
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

}