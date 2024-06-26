package org.example.view;

import org.example.model.*;
import org.example.service.Coworking;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ResponseBuilder {
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static String listFacilities(List<Facility> facilities) {
        StringBuilder response = new StringBuilder("\nCoworking has the following facilities:\n");
        getConferenceRooms(facilities, response);
        getWorkstations(facilities, response);
        return response.toString();
    }

    public static String listFreeSlots(Map<Facility, TreeSet<Booking>> slots) {
        StringBuilder response = new StringBuilder("\nAvailable booking slots:\n");
        slots.forEach((facility, freeSlots) -> {
            response.append(facility.getIdNumber().toUpperCase()).append(": ");
            freeSlots.forEach(slot -> response.append(formatter.format(slot.getStart())).append(" - ")
                    .append(formatter.format(slot.getEnd())).append(", "));
            response.append("\b\b.\n");
        });
        return response.toString();
    }

    public static String listBookings(User user, Coworking coworking) {
        StringBuilder response = new StringBuilder(String.format("\nBookings placed by `%s`:\n", user.getLogin()));
        var userBookings = coworking.viewAllBookings().stream()
                .filter(booking -> booking.getUser().equals(user))
                .collect(Collectors.toCollection(TreeSet::new));
        if (userBookings.isEmpty()) {
            response.append("\b None");
            return response.toString();
        }
        userBookings.forEach(booking -> {
            response.append(booking.getFacility().getIdNumber().toUpperCase())
                    .append(" from ").append(booking.getStart().toString())
                    .append(" to ").append(booking.getEnd().toString()).append('\n');
        });
        return response.toString();
    }

    private static void getConferenceRooms(List<Facility> facilities, StringBuilder response) {
        TreeSet<Facility> rooms = facilities.stream()
                .filter(facility -> facility.getClass().equals(ConferenceRoom.class))
                .collect(Collectors.toCollection(TreeSet::new));
        if (!rooms.isEmpty()) {
            response.append("\nConference Rooms:\n");
            rooms.forEach(room -> response.append(room.getIdNumber().toUpperCase()).append(" - ")
                    .append(((ConferenceRoom) room).getSeats()).append(" seats\n"));
        }
    }

    private static void getWorkstations(List<Facility> facilities, StringBuilder response) {
        TreeSet<Facility> stations = facilities.stream()
                .filter(facility -> facility.getClass().equals(Workstation.class))
                .collect(Collectors.toCollection(TreeSet::new));
        if (!stations.isEmpty()) {
            response.append("\nWorkstations:\n");
            stations.forEach(s -> response.append(s.getIdNumber().toUpperCase()).append(" - ")
                    .append(((Workstation) s).getDescription()).append('\n'));
        }
    }

}
