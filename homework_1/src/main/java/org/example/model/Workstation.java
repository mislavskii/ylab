package org.example.model;

import java.util.Collection;

public class Workstation extends Facility {
    private String description;

    public Workstation(String idNumber, String description) {
        super(idNumber);
        this.description = description;
    }
}
