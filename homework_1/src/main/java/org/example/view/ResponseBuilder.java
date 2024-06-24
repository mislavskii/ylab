package org.example.view;

import org.example.model.ConferenceRoom;
import org.example.model.Facility;
import org.example.model.Workstation;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ResponseBuilder {

    public static String listFacilities(List<Facility> facilities) {
        StringBuilder response = new StringBuilder("\nCoworking has the following facilities:\n");
        getConferenceRooms(facilities, response);
        getWorkstations(facilities, response);
        return response.toString();
    }

    public static String listFreeSlots() {
        return null;
    }

    private static void getConferenceRooms(List<Facility> facilities, StringBuilder response) {
        TreeSet<Facility> rooms = facilities.stream()
                .filter(facility -> facility.getClass().equals(ConferenceRoom.class))
                .collect(Collectors.toCollection(TreeSet::new));
        if (!rooms.isEmpty()) {
            response.append("\nConference Rooms:\n");
            rooms.forEach(room -> {
                response.append(room.getIdNumber().toUpperCase()).append(" - ")
                        .append(((ConferenceRoom) room).getSeats()).append(" seats\n");
            });
        }
    }

    private static void getWorkstations(List<Facility> facilities, StringBuilder response) {
        TreeSet<Facility> stations = facilities.stream()
                .filter(facility -> facility.getClass().equals(Workstation.class))
                .collect(Collectors.toCollection(TreeSet::new));
        if (!stations.isEmpty()) {
            response.append("\nWorkstations:\n");
            stations.forEach(s -> {
                response.append(s.getIdNumber().toUpperCase()).append(" - ")
                        .append(((Workstation) s).getDescription()).append('\n');
            });
        }
    }

}
