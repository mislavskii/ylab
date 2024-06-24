package org.example;

import org.example.model.User;
import org.example.service.Coworking;
import org.example.utils.Initializer;
import org.example.utils.MemberAlreadyExistsException;
import org.example.utils.MemberNotFoundException;
import org.example.utils.WrongPasswordException;
import org.example.view.ResponseBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static final Coworking coworking = new Coworking();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

    private static final String COMMANDS =
            """
            1 - List facilities
            2 - Check availability""";

    public static void main(String[] args) throws MemberAlreadyExistsException {
        coworking.createAdminUser("admin", "admin");
        System.out.println("Hello! Welcome to Coworking!");
        User testUser = new User("tu1", "tpwd1");
        Initializer.populateFacilities(coworking);

//        User user = loginOrRegister();
        executeCommands(testUser);

    }

    static User loginOrRegister() {
        User user = null;
        System.out.println("Please enter username to log into the system or create new account, mere Enter to exit:");
        String login = scanner.nextLine();
        if (login.isEmpty()) {System.exit(0);}
        System.out.println("Please enter password to proceed with loging in or create new account:");
        String password = scanner.nextLine();
        try {
            user = coworking.authenticateUser(login, password);
        } catch (MemberNotFoundException e) {
            runRegistrationRoutine(login, password);
        } catch (WrongPasswordException e) {
            System.out.println("Incorrect password. Please try again.");
            loginOrRegister();
        }
        System.out.println("Access granted.");
        return user;
    }

    private static void runRegistrationRoutine(String login, String password) {
        System.out.printf("Login `%S` not yet registered. Create new account Y/any?\n", login);
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("y")) {
            System.out.println("Please confirm your password:");
            String rePassword = scanner.nextLine();
            if (!password.equals(rePassword)) {
                System.out.println("Passwords don't match. Please repeat.");
                loginOrRegister();
            }
            try {
                coworking.registerNewUser(login, password);
            } catch (MemberAlreadyExistsException ex) {
                System.out.println("This login is already registered. Please try a different one.");
                loginOrRegister();
            }
            System.out.printf("New user login `%s` registered. Please log in with your new credentials.\n", login);
        }
        loginOrRegister();
    }

    public static void executeCommands(User user) {
        if (user == null) {
            throw new NullPointerException("The user is not defined.");
        }
        System.out.println("Please enter command, mere Enter to log out\n" + COMMANDS);
        String command = scanner.nextLine();
        if (command.isEmpty()) {
            loginOrRegister();
        }
        String response;
        switch(command) {
            case "1":
                response = ResponseBuilder.listFacilities(coworking.viewAllFacilities());
                System.out.println(response);
                break;
            case "2":
                System.out.println("Please enter date (YY.MM.DD):");
                String textDate = scanner.nextLine();
                LocalDate parsedDate = LocalDate.parse(textDate, formatter);
                response = ResponseBuilder.listFreeSlots(coworking.getAvailableBookingSlots(parsedDate));
                System.out.println(response);
                break;
            default:
                executeCommands(user);
        }
        executeCommands(user);
    }

}