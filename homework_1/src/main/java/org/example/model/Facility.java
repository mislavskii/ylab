package org.example.model;

import java.time.*;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class Facility implements Comparable<Facility> {
    public static int INTER_BOOKING_GAP = 15;
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
        Booking first = bookedSlots.first();
        if (first.getStart().isBefore(startOfDay.plusMinutes(INTER_BOOKING_GAP))
                && first.getEnd().isAfter(endOfDay.minusMinutes(INTER_BOOKING_GAP))) {
            return freeSlots;
        }
        LocalDateTime begin = first.getStart().isBefore(startOfDay.minusMinutes(INTER_BOOKING_GAP)) ?
                first.getEnd().plusMinutes(INTER_BOOKING_GAP) : startOfDay;
        LocalDateTime end = first.getEnd().isAfter(endOfDay.plusMinutes(INTER_BOOKING_GAP)) ?
                first.getStart().minusMinutes(INTER_BOOKING_GAP) : endOfDay;
        if (first.getStart().isBefore(startOfDay.minusMinutes(INTER_BOOKING_GAP))) {
            bookedSlots.pollFirst();
        }
        if (bookedSlots.isEmpty()) {
            Booking freeSlot = new Booking(null, this, begin, end);
            freeSlots.add(freeSlot);
            return freeSlots;
        }
        while (!bookedSlots.isEmpty()) {
            var nextBookedSlot = bookedSlots.pollFirst();
            end = nextBookedSlot.getStart().minusMinutes(INTER_BOOKING_GAP);
            try {
                var freeSlot = new Booking(null, this, begin, end);
                if (Duration.between(freeSlot.getStart(), freeSlot.getEnd()).toMinutes() >= INTER_BOOKING_GAP) {
                    freeSlots.add(freeSlot);
                }
            } catch (IllegalArgumentException ignored) {}
            begin = nextBookedSlot.getEnd().plusMinutes(INTER_BOOKING_GAP);
        }
        if (begin.isBefore(endOfDay)) {
            freeSlots.add(new Booking(null, this, begin, endOfDay));
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
                LocalDateTime.of(date.minusDays(1), LocalTime.MIN.minusMinutes(INTER_BOOKING_GAP)),
                LocalDateTime.of(date.plusDays(1), LocalTime.MIN.plusMinutes(INTER_BOOKING_GAP))
        );
    }

    @Override
    public int compareTo(Facility other) { return idNumber.compareTo(other.getIdNumber()); }

    @Override
    public String toString() {
        return "Facility{'" + idNumber + '\'' + '}';
    }
}
