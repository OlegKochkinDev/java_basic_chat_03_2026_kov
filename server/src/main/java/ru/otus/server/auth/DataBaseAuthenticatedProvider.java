package ru.otus.server.auth;

import ru.otus.server.ClientHandler;
import ru.otus.server.Server;

public class DataBaseAuthenticatedProvider implements AuthenticatedProvider {
    private Server server;

    public DataBaseAuthenticatedProvider(Server server) {
        this.server = server;


    }

    @Override
    public void initialize() {

    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {
        return false;
    }

    @Override
    public boolean register(ClientHandler clientHandler, String login, String password, String username) {
        return false;
    }
}
