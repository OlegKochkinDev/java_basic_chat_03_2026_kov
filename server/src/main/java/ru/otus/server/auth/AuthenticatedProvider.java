package ru.otus.server.auth;

import ru.otus.server.ClientHandler;

public interface AuthenticatedProvider {
    void initialize();

    boolean authenticate(ClientHandler clientHandler, String login, String password);

    boolean register(ClientHandler clientHandler, String login, String password, String username);
}
