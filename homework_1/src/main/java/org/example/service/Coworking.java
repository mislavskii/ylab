package org.example.service;

import org.example.model.Booking;
import org.example.model.Facility;
import org.example.model.User;

import java.time.LocalDateTime;
import java.util.*;

// TODO: move most of the user auth, reg, commands logic to Main
// TODO: test booking handling

public class Coworking {
    private final Map<String, Facility> facilities;
    private final Map<String, User> users;
    private final HashSet<Booking> bookings;
    private final Scanner scanner = new Scanner(System.in);

    public Coworking() {
        this.facilities = new HashMap<>();
        this.users = new TreeMap<>();
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

    public List<Facility> viewAllFacilities() {
        var view = new ArrayList<>(facilities.values());
        return Collections.unmodifiableList(view);
    }

    public void removeFacility(String idNumber) {
        facilities.remove(idNumber);
    }

    public boolean addBooking(User user, Facility facility, LocalDateTime start, LocalDateTime end) {
        return bookings.add(new Booking(facility, start, end, user));
    }

    public boolean removeBooking(
            Facility facility, LocalDateTime start, LocalDateTime end, User user
    ) throws NoSuchElementException, UnsupportedOperationException {
        Booking toRemove = bookings.stream()
                .filter(booking -> booking.getFacility().equals(facility)
                        && booking.getStart().isEqual(start)
                        && booking.getEnd().isEqual(end))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
        if ( !(toRemove.getUser().equals(user) || user.isAdmin()) ) {
            throw new UnsupportedOperationException("Current user doesn't have the privilege to remove this booking");
        }
        return bookings.remove(toRemove);
    }

    public List<Booking> viewAllBookings() {
        var view = new ArrayList<>(bookings);
        return Collections.unmodifiableList(view);
    }

}
