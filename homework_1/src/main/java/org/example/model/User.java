package org.example.model;

public class User {
    private final String login;
    private String password;
    private boolean isAdmin = false;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login, String password, boolean isAdmin) {
        this(login, password);
        this.isAdmin = isAdmin;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                '}';
    }
}
