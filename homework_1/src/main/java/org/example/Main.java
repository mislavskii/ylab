package org.example;

import org.example.service.Coworking;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello Coworking world!");

        Coworking coworking = new Coworking();

        coworking.authenticateUser();

    }
}