package org.example.service;

import org.example.model.ConferenceRoom;
import org.example.model.Facility;
import org.example.model.User;
import org.example.model.Workstation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CoworkingTest {
    Coworking coworking;

    @BeforeEach
    void setUp() {
        coworking = new Coworking();
    }

    @Test
    void addFacility() {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).contains(room);
    }

    @Test
    void same_id_same_class_not_added() {
        Workstation workstation1 = new Workstation("ws", "Celeron");
        Workstation workstation2 = new Workstation("ws", "Core i5");
        coworking.addFacility(workstation1);
        coworking.addFacility(workstation2);
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation1).doesNotContain(workstation2);
    }

    @Test
    void same_id_diff_class_not_added() {
        Workstation workstation = new Workstation("001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).doesNotContain(room);
    }

    @Test
    void getFacility() {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        var retrieved = coworking.getFacility("ws001");
        assertThat(retrieved).isSameAs(workstation);
    }

    @Test
    void removeFacility() {
        Workstation workstation = new Workstation("ws001", "Celeron");
        ConferenceRoom room = new ConferenceRoom("cr001", 7);
        coworking.addFacility(workstation);
        coworking.addFacility(room);
        coworking.removeFacility("cr001");
        var facilities = new ArrayList<Facility>(coworking.viewAllFacilities());
        assertThat(facilities).contains(workstation).doesNotContain(room);
    }

}