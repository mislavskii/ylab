package org.example;

import org.example.model.Facility;
import org.example.model.User;
import org.example.model.Workstation;
import org.example.service.Coworking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestUtils {

    public static void addFiveBookings(Facility facility, Coworking coworking, LocalDate date) {
        User user = new User("u1", "pwd1");
        var dayBefore = date.minusDays(1);
        var dayAfter = date.plusDays(1);
        var start1 = LocalDateTime.of(dayBefore, LocalTime.of(11, 0));
        var end1 = LocalDateTime.of(dayBefore, LocalTime.of(17, 0));
        coworking.addBooking(user, facility, start1, end1);
        var start2 = LocalDateTime.of(dayBefore, LocalTime.of(19, 0));
        var end2 = LocalDateTime.of(date, LocalTime.of(6, 0));
        coworking.addBooking(user, facility, start2, end2);
        var start3 = LocalDateTime.of(date, LocalTime.of(11, 0));
        var end3 = LocalDateTime.of(date, LocalTime.of(17, 0));
        coworking.addBooking(user, facility, start3, end3);
        var start4 = LocalDateTime.of(date, LocalTime.of(21, 0));
        var end4 = LocalDateTime.of(dayAfter, LocalTime.of(11, 0));
        coworking.addBooking(user, facility, start4, end4);
        var start5 = LocalDateTime.of(dayAfter, LocalTime.of(17, 0));
        var end5 = LocalDateTime.of(dayAfter, LocalTime.of(22, 0));
        coworking.addBooking(user, facility, start5, end5);
    }

    public static void addFiveBookingsInclAnotherFacility(Facility facility, Coworking coworking) {
        User user = new User("u1", "pwd1");
        Facility other = new Workstation("other00", "Celeron");
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
    }

    public static void addTwoOutsideBookings(Facility facility, Coworking coworking, LocalDate date) {
        var user = new User("u1", "pwd1");
        coworking.addBooking(
                user, facility,
                LocalDateTime.of(date.minusDays(1), LocalTime.of(11, 0)),
                LocalDateTime.of(date.minusDays(1), LocalTime.of(17, 0))
        );
        coworking.addBooking(
                user, facility,
                LocalDateTime.of(date.plusDays(1), LocalTime.of(11, 0)),
                LocalDateTime.of(date.plusDays(1), LocalTime.of(17, 0))
        );
    }

    public static void addThreeBookingsAmidstDay(Facility facility, Coworking coworking, LocalDate date) {
        var user = new User("u1", "pwd1");
        var start2 = LocalDateTime.of(date, LocalTime.of(6, 0));
        var end2 = LocalDateTime.of(date, LocalTime.of(8, 0));
        coworking.addBooking(user, facility, start2, end2);
        var start3 = LocalDateTime.of(date, LocalTime.of(11, 0));
        var end3 = LocalDateTime.of(date, LocalTime.of(17, 0));
        coworking.addBooking(user, facility, start3, end3);
        var start4 = LocalDateTime.of(date, LocalTime.of(19, 0));
        var end4 = LocalDateTime.of(date, LocalTime.of(21, 0));
        coworking.addBooking(user, facility, start4, end4);
    }

    public static void addTwoOutstretchingBookingsUnder45Apart(Facility facility, Coworking coworking, LocalDate date) {
        var user = new User("u1", "pwd1");
        coworking.addBooking(
                user, facility,
                LocalDateTime.of(date.minusDays(1), LocalTime.of(17, 0)),
                LocalDateTime.of(date, LocalTime.of(11, 0))
        );
        coworking.addBooking(
                user, facility,
                LocalDateTime.of(date, LocalTime.of(11, 44)),
                LocalDateTime.of(date.plusDays(1), LocalTime.of(17, 0))
        );
    }

    public static void addTwoInnerBookingsUnder45Apart(Facility facility, Coworking coworking, LocalDate date) {
        var user = new User("u1", "pwd1");
        coworking.addBooking(
                user, facility,
                LocalDateTime.of(date, LocalTime.of(8, 0)),
                LocalDateTime.of(date, LocalTime.of(11, 0))
        );
        coworking.addBooking(
                user, facility,
                LocalDateTime.of(date, LocalTime.of(11, 44)),
                LocalDateTime.of(date, LocalTime.of(17, 0))
        );
    }

    public static void addTwoInnerBookingsGapApart(Facility facility, Coworking coworking, LocalDate date, long gap) {
        var user = new User("u1", "pwd1");
        var start1 = LocalDateTime.of(date, LocalTime.of(8, 0));
        var end1 = LocalDateTime.of(date, LocalTime.of(11, 0));
        coworking.addBooking(user, facility, start1, end1);
        coworking.addBooking(
                user, facility,
                end1.plusMinutes(gap),
                LocalDateTime.of(date, LocalTime.of(17, 0))
        );
    }

}
