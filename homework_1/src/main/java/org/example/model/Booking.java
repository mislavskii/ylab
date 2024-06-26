package org.example.model;

import java.time.LocalDateTime;

public class Booking implements Comparable<Booking> {
    private final Facility facility;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final User user;

    public Booking(User user, Facility facility, LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time is before or equal to start time");
        }
        this.user = user;
        this.facility = facility;
        this.start = start;
        this.end = end;
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

    public boolean isOverlapping(Booking other) { // TODO: add facility-specific gap
        return !getEnd().isBefore(other.getStart()) && !getStart().isAfter(other.getEnd());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        if (!getFacility().equals(booking.getFacility())) return false;
        return isOverlapping(booking);
    }

    @Override
    public int hashCode() {
        return getFacility().hashCode();
    }

    @Override
    public int compareTo(Booking other) {  // TODO: include end
        if (getStart().isBefore(other.getStart())) {
            return -1;
        }
        if (getStart().isAfter(other.getStart())) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Booking {" + user +
                ", facility=" + facility.getIdNumber() +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
