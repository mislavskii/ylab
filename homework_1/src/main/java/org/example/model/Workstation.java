package org.example.model;

public class Workstation extends Facility {
    private String description;

    public Workstation(String idNumber, String description) {
        super(idNumber);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
