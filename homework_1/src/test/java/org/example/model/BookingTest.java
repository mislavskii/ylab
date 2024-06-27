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
                    user1, workstation1,
                    LocalDateTime.of(2024, 6, 22, 10, 0),
                    LocalDateTime.of(2024, 6, 22, 9, 59)
            );
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void booking_end_same_start_throws_exception() {
        assertThatThrownBy( () -> {
            Booking b1 = new Booking(
                    user1, workstation1,
                    LocalDateTime.of(2024, 6, 22, 10, 0),
                    LocalDateTime.of(2024, 6, 22, 10, 0)
            );
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void same_time_same_facility_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        Booking b2 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void diff_time_same_facility_not_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        Booking b2 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 12, 1),
                LocalDateTime.of(2024, 6, 22, 16, 0)
        );
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void same_time_diff_facility_same_class_not_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        Booking b2 = new Booking(
                user1, workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void same_time_diff_facility_diff_class_not_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        Booking b2 = new Booking(
                user1, room1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        assertThat(b1).isNotEqualTo(b2);
    }

    @Test
    void one_within_other_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 10, 0)
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void one_starts_same_time_other_ends_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 16, 0)
        );
        Booking b2 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 10, 0)
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void one_starts_before_other_ends_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 10, 0)
        );
        Booking b2 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void diff_start_same_end_equal() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void same_start_diff_end_equal() {
        Booking b1 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 11, 0)
        );
        assertThat(b1).isEqualTo(b2);
    }

    @Test
    void same_time_same_facility_equal_by_compare_to() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        Booking b2 = new Booking(
                user2, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        assertThat(b1).isEqualByComparingTo(b2);
    }

    @Test
    void same_time_diff_facility_not_equal_by_comparing_to() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        Booking b2 = new Booking(
                user1, workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 22, 12, 0)
        );
        assertThat(b1).isNotEqualByComparingTo(b2);
    }

    @Test
    void earlier_start_smaller() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user1, workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 1),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        assertThat(b1).isLessThan(b2);
    }

    @Test
    void later_start_greater() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 1),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user1, workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        assertThat(b1).isGreaterThan(b2);
    }

    @Test
    void same_start_earlier_end_smaller() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user1, workstation2,
                LocalDateTime.of(2024, 6, 22, 10, 0),
                LocalDateTime.of(2024, 6, 23, 12, 0)
        );
        assertThat(b2).isLessThan(b1);
    }

    @Test
    void same_time_smaller_facility_smaller() {
        Booking b1 = new Booking(
                user1, workstation1,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        Booking b2 = new Booking(
                user2, workstation2,
                LocalDateTime.of(2024, 6, 22, 16, 0),
                LocalDateTime.of(2024, 6, 23, 16, 0)
        );
        assertThat(b1).isLessThan(b2);
    }
}