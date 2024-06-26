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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        if (isAdmin() != user.isAdmin()) return false;
        return getLogin().equals(user.getLogin());
    }

    @Override
    public int hashCode() {
        int result = getLogin().hashCode();
        result = 31 * result + (isAdmin() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                '}';
    }
}
