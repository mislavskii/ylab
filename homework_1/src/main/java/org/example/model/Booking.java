package org.example.model;

import java.time.LocalDateTime;

public class Booking implements Comparable<Booking> {
    private final Facility facility;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final User user;

    public Booking(Facility facility, LocalDateTime start, LocalDateTime end, User user) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time is before or equal to start time");
        }
        this.facility = facility;
        this.start = start;
        this.end = end;
        this.user = user;
    }

    public Facility getFacility() {
        return facility;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        if (!getFacility().equals(booking.getFacility())) return false;
        return !getEnd().isBefore(booking.getStart()) && !getStart().isAfter(booking.getEnd());
    }

    @Override
    public int hashCode() {
        int result = getFacility().hashCode();
        result = 31 * result + getStart().hashCode();
        result = 31 * result + getEnd().hashCode();
        return result;
    }

    @Override
    public int compareTo(Booking other) {
        if (getStart().isBefore(other.getStart())) {
            return -1;
        }
        if (getStart().isAfter(other.getStart())) {
            return 1;
        }
        return 0;
    }

}
