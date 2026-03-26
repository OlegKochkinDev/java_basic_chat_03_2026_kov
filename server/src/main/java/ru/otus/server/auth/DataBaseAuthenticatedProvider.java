package ru.otus.server.auth;

import ru.otus.server.ClientHandler;
import ru.otus.server.Server;
import ru.otus.server.user.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataBaseAuthenticatedProvider implements AuthenticatedProvider {
    private Server server;
    private List<User> users;


    public DataBaseAuthenticatedProvider(Server server) {
        this.server = server;
        initialize();
        this.users = new CopyOnWriteArrayList<>();

    }

    @Override
    public void initialize() {
        System.out.println("Сервер аутентификации запущен в режиме Базы Данных");

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
