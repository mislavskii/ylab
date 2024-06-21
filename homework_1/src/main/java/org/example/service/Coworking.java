package org.example.service;

import org.example.model.Booking;
import org.example.model.Facility;
import org.example.model.User;
import org.example.model.Workstation;

import java.util.*;

public class Coworking {
    private final HashMap<String, Facility> facilities;
    private final HashMap<String, User> users;
    private HashSet<Booking> bookings;
    private final Scanner scanner = new Scanner(System.in);

    public Coworking() {
        this.facilities = new HashMap<>();
        this.users = new HashMap<>();
        this.bookings = new HashSet<>();
    }

    private void registerNewUser(String login) {
        System.out.println("Please enter a password:");
        String password = scanner.nextLine();
        users.put(login, new User(login, password));
        System.out.println("New user registered. Please enter next command.");
        String command = scanner.nextLine();
        executeCommand(command);
    }

    public void authenticateUser() {
        int maxTrials = 3;
        System.out.println("Please enter username to log into the system or create new account, mere Enter to exit:");
        String login = scanner.nextLine();
        if (login.isEmpty()) {System.exit(0);}
        if (users.containsKey(login)) {
            System.out.println("Username recognized. Please enter your password or mere Enter to try another username:");
            String password = scanner.nextLine();
            while (!password.equals(users.get(login).getPassword()) && maxTrials > 0) {
                if (password.isEmpty()) {
                    authenticateUser();
                }
                maxTrials--;
                System.out.println("Incorrect password. Please try again. Mere Enter to exit.");
                password = scanner.nextLine();
            }
            if (maxTrials == 0) {
                authenticateUser();
            }
            System.out.println("Access granted. Please enter command");
            String command = scanner.nextLine();
            executeCommand(command);
        } else {
            System.out.println("There is no such user yet. Create new account Y/any?");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("y")) {
                registerNewUser(login);
            }
            authenticateUser();
        }
    }

    private void executeCommand(String command) {
        if (command.isEmpty()) {
            authenticateUser();
        }
        System.out.println(command);
        System.out.println("Please enter next command, mere Enter to log out");
        executeCommand(scanner.nextLine());
    }

    public void addFacility(Facility facility) {
        if (facilities.putIfAbsent(facility.getIdNumber(), facility) != null) {
            System.out.println("Facility already exists, could not be added");
        }
    }

    public Facility getFacility(String idNumber) {
        return facilities.get(idNumber);
    }

    public List<? extends Facility> viewAllFacilities() {
        var view = new ArrayList<>(facilities.values());
        return Collections.unmodifiableList(view);
    }

    public Facility removeFacility(String idNumber) {
        return facilities.remove(idNumber);
    }

}
