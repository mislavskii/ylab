package org.example.model;

public class ConferenceRoom extends Facility {
    private int seats;

    public ConferenceRoom(String idNumber, int seats) {
        super(idNumber);
        this.seats = seats;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
