package org.example.view;

import org.example.model.Booking;
import org.example.model.ConferenceRoom;
import org.example.model.Facility;
import org.example.model.Workstation;

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
