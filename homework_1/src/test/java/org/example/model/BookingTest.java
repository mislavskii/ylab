package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {
    Workstation workstation1;
    Workstation workstation2;
    ConferenceRoom room1;
    ConferenceRoom room2;

    User user1;

    User user2;

    @BeforeEach
    void setUp() {
        workstation1 = new Workstation("ws001", "Celeron");
        workstation2 = new Workstation("ws002", "Core i5");
        room1 = new ConferenceRoom("cr001", 7);
        room2 = new ConferenceRoom("cr002", 35);
        user1 = new User("u1", "pwd1");
        user2 = new User("u2", "pwd2");
    }

    @Test
    void booking_end_before_start_throws_exception() {
        assertThatThrownBy( () -> {
            Booking b1 = new Booking(
                    workstation1,
                    LocalDateTime.of(2024, 6, 22, 10, 0),
                    LocalDateTime.of(2024, 6, 22, 9, 59),
                    user1
            );
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void booking_end_same_start_throws_exception() {
        assertThatThrownBy( () -> {
            Booking b1 = new Booking(
                    workstation1,
                    LocalDateTime.of(2024, 6, 22, 10, 0),
                    LocalDateTime.of(2024, 6, 22, 10, 0),
                    user1
            );
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void same_time_same_facility_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user2
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void diff_time_same_facility_not_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 12, 1),
                LocalDateTime.of(2024, 6, 22, 16, 0),
                user1
        );
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void same_time_diff_facility_same_class_not_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user1
        );
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void same_time_diff_facility_diff_class_not_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user1
        );
        Booking b2 = new Booking(
                room1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0),
                user1
        );
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void one_within_other_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 10, 0),
                user2
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void one_starts_same_time_other_ends_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 16, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 10, 0),
                user2
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void one_starts_before_other_ends_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 10, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user2
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void diff_start_same_end_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user2
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void same_start_diff_end_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user2
        );
        Booking b2 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void earlier_start_smaller() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 1),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        assertThat(b1).isLessThan(b2);
    }

    @Test
    void later_start_greater() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 1),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        assertThat(b1).isGreaterThan(b2);
    }

    @Test
    void same_start_compare_as_equal() {
        Booking b1 = new Booking(
                workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0),
                user1
        );
        Booking b2 = new Booking(
                workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 12, 0),
                user1
        );
        assertThat(b1).isEqualByComparingTo(b2);
    }

}