package org.example;

import org.example.model.User;
import org.example.service.Coworking;
import org.example.utils.MemberAlreadyExistsException;
import org.example.utils.MemberNotFoundException;
import org.example.utils.WrongPasswordException;

import java.util.Scanner;

public class Main {
    private static final Coworking coworking = new Coworking();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Hello! Welcome to Coworking!");

        loginOrRegister();

    }

    static void loginOrRegister() {
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
        System.out.println("Access granted. Please enter command");
        String command = scanner.nextLine();
        executeCommand(command, user);
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

    private static void executeCommand(String command, User user) {
        if (user == null) {
            throw new NullPointerException("The user is not defined.");
        }
        if (command.isEmpty()) {
            loginOrRegister();
        }
        System.out.println(command);
        System.out.println("Please enter next command, mere Enter to log out");
        executeCommand(scanner.nextLine(), user);
    }

}