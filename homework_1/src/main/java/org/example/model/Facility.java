package org.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class Facility {
    private final String idNumber;

    public Facility(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public TreeSet<Booking> getFreeBookingSlotsForDate(LocalDate date, Collection<Booking> allBookings) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay =  LocalDateTime.of(date, LocalTime.MAX);
        TreeSet<Booking> freeSlots = new TreeSet<>();
        TreeSet<Booking> bookedSlots = getBookingsForDate(date, allBookings);
        if (bookedSlots.isEmpty()) {
            Booking freeSlot = new Booking(null, this, startOfDay, endOfDay);
            freeSlots.add(freeSlot);
            return freeSlots;
        }
        Booking first = bookedSlots.pollFirst();
        if (first.getStart().isBefore(startOfDay) && first.getEnd().isAfter(endOfDay)) {
            return freeSlots;
        }
        var begin = first.getStart().isBefore(startOfDay) ? first.getEnd() : startOfDay;
        var end = first.getEnd().isAfter(endOfDay) ? first.getStart() : endOfDay;
        if (bookedSlots.isEmpty()) {
            Booking freeSlot = new Booking(null, this, begin, end);
            freeSlots.add(freeSlot);
            return freeSlots;
        }
        while (!bookedSlots.isEmpty()) {
            var nextBookedSlot = bookedSlots.pollFirst();
            end = nextBookedSlot.getStart();
            freeSlots.add(new Booking(null, this, begin, end));
            begin = nextBookedSlot.getEnd();
        }
        return freeSlots;
    }

    public TreeSet<Booking> getBookingsForDate(LocalDate date, Collection<Booking> allBookings) {
        Booking dummy = getDummyBooking(date);
        return allBookings.stream()
                .filter(boo -> boo.getFacility().equals(this))
                .filter(boo -> boo.equals(dummy))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private Booking getDummyBooking(LocalDate date) {
        return new Booking(
                null, this,
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date, LocalTime.MAX)
        );
    }

    @Override
    public String toString() {
        return "Facility{'" + idNumber + '\'' + '}';
    }
}
