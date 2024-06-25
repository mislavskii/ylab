package org.example;

import org.example.model.Facility;
import org.example.model.User;
import org.example.service.Coworking;
import org.example.utils.Initializer;
import org.example.utils.MemberAlreadyExistsException;
import org.example.utils.MemberNotFoundException;
import org.example.utils.WrongPasswordException;
import org.example.view.ResponseBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final Coworking coworking = new Coworking();
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");

    private static final String COMMANDS =
            """
            1 - List facilities
            2 - Check availability
            3 - Place booking""";

    public static void main(String[] args) throws MemberAlreadyExistsException {
        coworking.createAdminUser("admin", "admin");
        System.out.println("Hello! Welcome to Coworking!");
        Initializer.populateFacilities(coworking);

        loginOrRegister();

    }

    static void loginOrRegister() {
        User user = null;
        while (user == null) {
            System.out.println("Please enter username to log into the system or create new account, mere Enter to exit:");
            String login = SCANNER.nextLine();
            if (login.isEmpty()) {
                System.exit(0);
            }
            System.out.println("Please enter password to proceed with logging in or create new account:");
            String password = SCANNER.nextLine();
            try {
                user = coworking.authenticateUser(login, password);
            } catch (MemberNotFoundException e) {
                runRegistrationRoutine(login, password);
            } catch (WrongPasswordException e) {
                System.out.println("Incorrect password. Please try again.");
            }
        }
        System.out.println("Access granted.");
        executeCommands(user);
    }

    private static void runRegistrationRoutine(String login, String password) {
        System.out.printf("Login `%s` not yet registered. Create new account Y/any?\n", login);
        String input = SCANNER.nextLine();
        if (input.equalsIgnoreCase("y")) {
            System.out.println("Please confirm your password:");
            String rePassword = SCANNER.nextLine();
            if (!password.equals(rePassword)) {
                System.out.println("Passwords don't match. Please repeat.");
                return;
            }
            try {
                coworking.registerNewUser(login, password);
            } catch (MemberAlreadyExistsException ex) {
                System.out.println("This login is already registered. Please try a different one.");
            }
            System.out.printf("New user login `%s` registered. Please log in with your new credentials.\n", login);
        }
    }

    public static void executeCommands(User user) {
        while (true) {
            if (user == null) {
                throw new NullPointerException("The user is not defined.");
            }
            System.out.println("Please enter command, mere Enter to log out\n" + COMMANDS);
            String command = SCANNER.nextLine();
            if (command.isEmpty()) {
                break;
            }
            String response;
            switch (command) {
                case "1" -> {
                    response = ResponseBuilder.listFacilities(coworking.viewAllFacilities());
                    System.out.println(response);
                }
                case "2" -> {
                    System.out.println("Please enter date (YY-MM-DD):");
                    String textDate = SCANNER.nextLine();
                    LocalDate parsedDate = LocalDate.parse(textDate, DATE_FORMATTER);
                    response = ResponseBuilder.listFreeSlots(coworking.getAvailableBookingSlots(parsedDate));
                    System.out.println(response);
                }
                case "3" -> placeBooking(user);
                default -> System.out.println("Unknown command. Please try again.");
            }
        }
        loginOrRegister();
    }

    private static void placeBooking(User user) {
        System.out.println(
                "Please enter facility number, start datetime (YY-MM-DD HH:MM), end datetime (YY-MM-DD HH:MM), " +
                        "separated by commas. Mere Enter to return to the menu:"
        );
        String bookingData = SCANNER.nextLine();
        if (bookingData.isEmpty()) {
            return;
        }
        var split = bookingData.split(",\\s*");
        Facility facility = null;
        try {
            facility = coworking.getFacility(split[0].toLowerCase());
        } catch (MemberNotFoundException e) {
            System.out.println("Facility not found. Please consider resubmitting.");
            return;
        }
        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            start = LocalDateTime.parse(split[1], DATE_TIME_FORMATTER);
            end = LocalDateTime.parse(split[2], DATE_TIME_FORMATTER);
        } catch (Exception e) {
            System.out.println("Error parsing booking details. Please consider resubmitting.");
            return;
        }
        String response = coworking.addBooking(user, facility, start, end) ?
                "Booking placed successfully." : "Something went wrong. Please consider retry.";
        System.out.println(response);
    }

}