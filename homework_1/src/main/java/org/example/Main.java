package org.example;

import org.example.model.ConferenceRoom;
import org.example.model.Facility;
import org.example.model.User;
import org.example.model.Workstation;
import org.example.service.Coworking;
import org.example.utils.Initializer;
import org.example.utils.MemberAlreadyExistsException;
import org.example.utils.MemberNotFoundException;
import org.example.utils.WrongPasswordException;
import org.example.view.ResponseBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
            3 - Place booking
            4 - View your bookings
            5 - Remove your booking""";

    private static final String ADMIN_COMMANDS =
            """
            6 - View all bookings with filtering on facility / user
            7 - Remove any booking
            8 - Manage facilities""";

    public static void main(String[] args) {
        System.out.println("Hello! Welcome to Coworking!");
        Initializer.populate(coworking);

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

    private static void executeCommands(User user) {
        String prompt = "Please enter command, mere Enter to log out\n";
        while (true) {
            if (user == null) {
                throw new NullPointerException("The user is not defined.");
            }
            String userMenu = prompt + COMMANDS;
            String adminMenu = userMenu + '\n' + ADMIN_COMMANDS;
            String menu = user.isAdmin() ? adminMenu : userMenu;
            System.out.println(menu);
            SCANNER.reset();
            String command = SCANNER.nextLine();
            if (command.isEmpty()) {
                break;
            }
            switch (command) {
                case "1" -> viewFacilities();
                case "2" -> checkAvailability();
                case "3" -> placeBooking(user);
                case "4" -> viewOwnBookings(user);
                case "5", "7" -> removeBooking(user);
                case "6" -> viewBookingsAsAdmin(user);
                case "8" -> manageFacilities(user);
                default -> System.out.println("Unknown command. Please try again.");
            }
        }
        loginOrRegister();
    }

    private static void viewFacilities() {
        String response = ResponseBuilder.listFacilities(coworking.viewAllFacilities());
        System.out.println(response);
    }

    private static void checkAvailability() {
        System.out.println("Please enter date (YY-MM-DD):");
        String textDate = SCANNER.nextLine();
        LocalDate parsedDate = LocalDate.parse(textDate, DATE_FORMATTER);
        String response = ResponseBuilder.listFreeSlots(coworking.getAvailableBookingSlots(parsedDate));
        System.out.println(response);
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
        Map<String, Object> parsed = parseBookingDetails(bookingData);
        if (parsed == null) {
            return;
        }
        String response = coworking.addBooking(
                user, (Facility) parsed.get("facility"),
                (LocalDateTime) parsed.get("start"), (LocalDateTime) parsed.get("end")) ?
                "Booking placed successfully." : "Something went wrong. Please consider retry.";
        System.out.println(response);
    }

    private static void viewOwnBookings(User user)  {
        String response = ResponseBuilder.listUserBookings(user, coworking);
        System.out.println(response);
    }

    private static void removeBooking(User user) {
        System.out.println(
                "Please enter booking's facility number, start datetime (YY-MM-DD HH:MM), end datetime (YY-MM-DD HH:MM), " +
                        "separated by commas. Mere Enter to return to the menu:"
        );
        String bookingData = SCANNER.nextLine();
        Map<String, Object> parsed = parseBookingDetails(bookingData);
        if (parsed == null) return;
        String response;
        try {
            response = coworking.removeBooking(
                    (Facility) parsed.get("facility"),
                    (LocalDateTime) parsed.get("start"), (LocalDateTime) parsed.get("end"),
                    user) ?
                    "Booking successfully removed." : "Something went wrong. Please consider retry.";
        } catch (NoSuchElementException e) {
            System.out.println("Booking not found in the system.");
            return;
        } catch (UnsupportedOperationException e) {
            System.out.println("Can't remove another user's booking.");
            return;
        }
        System.out.println(response);
    }

    private static void viewBookingsAsAdmin(@NotNull User admin) {
        if (!admin.isAdmin()) {
            System.out.println("Unknown command. Please try again");
            return;
        }
        System.out.println("Enter `user <login>` (user u1) or `facility <id number>` (facility CR001) to apply filter, " +
                "mere Enter to proceed with no filtering.");
        var input = SCANNER.nextLine().trim().toLowerCase().split("\\s+");
        switch (input[0]) {
            case "user" -> viewLoginBookings(admin, input[1]);
            case "facility" -> viewFacilityBookings(admin, input[1]);
            default -> {
                String response = ResponseBuilder.listAllBookings(admin, coworking);
                System.out.println(response);
            }
        }
    }

    private static void manageFacilities(User admin) {
        if (!admin.isAdmin()) {
            System.out.println("Unknown command. Please try again");
            return;
        }
        System.out.println("Choose action:\n1 - Add new facility\n2 - Edit facility\n3 - Delete a facility");
        String input = SCANNER.nextLine();
        switch (input.trim()) {
            case "1" -> addNewFacility(admin);
            case "2" -> editFacility(admin);
            case "3" -> deleteFacility(admin);
            default -> System.out.println("Unknown command.");
        }
    }

    private static void addNewFacility(User admin) {
        if (!admin.isAdmin()) return;
        String response;
        System.out.println("Choose new facility type: \n1 - Workstation\n2 - Conference Room");
        String type = SCANNER.nextLine().trim();
        System.out.println("Enter new facility name:");
        String idNumber = SCANNER.nextLine().trim();
        if (type.equals("1")) {
            System.out.println("Enter workstation description: ");
            String description = SCANNER.nextLine().trim();
            response = ResponseBuilder.addNewWorkstation(admin, idNumber, description, coworking);
        } else if (type.equals("2")) {
            System.out.println("Enter number of seats: ");
            int seats = new Scanner(System.in).nextInt();  // TODO: catch
            response = ResponseBuilder.addNewConferenceRoom(admin, idNumber, seats, coworking);
        } else {
            response = "Unknown command.";
        }
        System.out.println(response);
    }
    private static void editFacility(User admin) {
        if (!admin.isAdmin()) return;
        String response = "";
        Facility facility;
        System.out.println("Enter facility name:");
        String idNumber = SCANNER.nextLine().trim().toLowerCase();
        try {
            facility = coworking.getFacility(idNumber);
        } catch (MemberNotFoundException e) {
            System.out.println("Facility with this name not found. Consider retry.");
            return;
        }
        if (facility.getClass().equals(ConferenceRoom.class)) {
            response = editConferenceRoom((ConferenceRoom) facility);
        }
        if (facility.getClass().equals(Workstation.class)) {
            response = editWorkstation ((Workstation) facility);
        }
        System.out.println(response);
    }
    private static String editConferenceRoom(ConferenceRoom room) {
        int newSeats;
        System.out.printf("Conference room `%S` has %d seats. You can change the number of seats. " +
                "Enter the new number, -1 to return to the menu:\n", room.getIdNumber(), room.getSeats());
        try {
            newSeats = new Scanner(System.in).nextInt();
        } catch (InputMismatchException e) {
            return  "Invalid input value. Please retry.";
        } catch (NoSuchElementException e) {  // this doesn't work on mere Enter
            return "";
        }
        if (newSeats == -1) {
            return "Edit cancelled.";
        }
        room.setSeats(newSeats);
        return String.format(
                "Conference room `%S` edited. The number of seats is set to %d.\n",
                room.getIdNumber(), room.getSeats()
        );
    }
    private static String editWorkstation(Workstation workstation) {
        System.out.printf("Workstation's `%S` description is `%s`. Enter new description to change it, " +
                "mere Enter to return to the menu.\n", workstation.getIdNumber(), workstation.getDescription());
        String newDescription = SCANNER.nextLine().trim();
        if (newDescription.isEmpty()) {
            return "";
        }
        workstation.setDescription(newDescription);
        return String.format(
                "Workstation's `%S` description set to `%s`\n",
                workstation.getIdNumber(), workstation.getDescription()
        );
    }

    private static void deleteFacility(User admin) {
        if (!admin.isAdmin()) return;
        String response;
        System.out.println("Enter name of the facility to be deleted:");
        String idNumber = SCANNER.nextLine().trim().toLowerCase();
        response = ResponseBuilder.deleteFacility(admin, idNumber, coworking);
        System.out.println(response);
    }

    private static void viewFacilityBookings(@NotNull User admin, String idNumber) {
        if (!admin.isAdmin()) return;
        String response = ResponseBuilder.listFacilityBookings(admin, idNumber, coworking);
        System.out.println(response);
    }

    private static void viewLoginBookings(User admin, String userLogin) {
        String response = ResponseBuilder.listUserBookings(admin, userLogin, coworking);
        System.out.println(response);
    }

    private static @Nullable Map<String, Object> parseBookingDetails(@NotNull String bookingData) {
        Map<String, Object> parsed = new HashMap<>();
        var split = bookingData.split(",\\s*");
        try {
            parsed.put("facility", coworking.getFacility(split[0].trim().toLowerCase()));
        } catch (MemberNotFoundException e) {
            System.out.println("Facility not found. Please consider retry.");
            return null;
        }
        try {
            parsed.put("start", LocalDateTime.parse(split[1].trim(), DATE_TIME_FORMATTER));
            parsed.put("end", LocalDateTime.parse(split[2].trim(), DATE_TIME_FORMATTER));
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing booking details. Please consider retry.");
            return null;
        }
        return parsed;
    }

}