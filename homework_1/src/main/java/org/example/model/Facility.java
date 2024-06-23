package org.example.model;

public abstract class Facility {
    private final String idNumber;

    public Facility(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    @Override
    public String toString() {
        return "Facility{'" + idNumber + '\'' + '}';
    }
}
