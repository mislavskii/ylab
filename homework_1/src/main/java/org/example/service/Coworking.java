package org.example.service;

import org.example.model.*;
import org.example.utils.MemberAlreadyExistsException;
import org.example.utils.MemberNotFoundException;
import org.example.utils.WrongPasswordException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Coworking {
    private final Map<String, Facility> facilities;
    private final Map<String, User> users;
    private final HashSet<Booking> bookings;

    public Coworking() {
        this.facilities = new HashMap<>();
        this.users = new TreeMap<>();
        this.bookings = new HashSet<>();
    }

    public void createAdminUser(String login, String password) throws MemberAlreadyExistsException {
        if (users.putIfAbsent(login, new User(
                login,
                password,
                true
        )) != null) {
            throw new MemberAlreadyExistsException();
        }
    }

    public void registerNewUser(String login, String password) throws MemberAlreadyExistsException {
        if (users.putIfAbsent(login, new User(login, password)) != null) {
            throw new MemberAlreadyExistsException();
        }
    }

    public User authenticateUser(String login, String password) throws MemberNotFoundException, WrongPasswordException {
        User user = users.get(login);
        if (user == null) {
            throw new MemberNotFoundException();
        }
        if (!user.getPassword().equals(password)) {
            throw new WrongPasswordException();
        }
        return user;
    }

    public User getUser(String login, String password) throws WrongPasswordException, MemberNotFoundException {
        User user = users.get(login);
        if (user == null) {
            throw new MemberNotFoundException();
        }
        if (!user.getPassword().equals(password)) {
            throw new WrongPasswordException();
        }
        return user;
    }

    public boolean removeUser(String login, String password) throws MemberNotFoundException, WrongPasswordException {
        User user = getUser(login, password);
        return users.remove(login, user);
    }

    public void addFacility(Facility facility) throws MemberAlreadyExistsException {
        if (facilities.putIfAbsent(facility.getIdNumber(), facility) != null) {
            throw new MemberAlreadyExistsException();
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
        return bookings.add(new Booking(user, facility, start, end));
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

    public Map<Facility, TreeSet<Booking>> getAvailableBookingSlots(LocalDate date) {
        Map<Facility, TreeSet<Booking>> freeSlots = new TreeMap<>();
        facilities.values().forEach(
                facility -> freeSlots.put(facility, facility.getFreeBookingSlotsForDate(date, bookings))
        );
        return freeSlots;
    }

}
