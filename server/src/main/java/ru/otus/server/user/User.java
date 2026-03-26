package ru.otus.server.user;

public class User {
    private String login;
    private String password;
    private String username;
    private UserRole role;

    public User(String login, String password, String username) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.role = UserRole.USER;
    }

    public User(String login, String password, String username, UserRole role) {
        this.login = login;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String toString() {
        return "LOGIN: " + this.login +" PASS: " + this.password + " USERNAME: " + this.username + " ROLE: " + this.role;
    }



}