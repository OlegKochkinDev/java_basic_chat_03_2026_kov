package ru.otus.server.auth;

import ru.otus.server.ClientHandler;
import ru.otus.server.ConsoleColors;
import ru.otus.server.Server;
import ru.otus.server.user.User;
import ru.otus.server.user.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataBaseAuthenticatedProvider implements AuthenticatedProvider {
    private Server server;
    private List<User> users;


    public DataBaseAuthenticatedProvider(Server server) {
        this.server = server;
        initialize();
        this.users = AuthDataBase.readUsers();
        for (User user : this.users) {
            System.out.println(user.toString());
        }
    }

    @Override
    public void initialize() {
        System.out.println("Сервер аутентификации запущен в режиме Базы Данных");

    }

    private String getUsernameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                return u.getUsername();
            }
        }
        return null;
    }

    private UserRole getUserRoleByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u.getRole();
            }
        }
        return null;
    }

    @Override
    public boolean authenticate(ClientHandler clientHandler, String login, String password) {

        {
            String authUsername = getUsernameByLoginAndPassword(login, password);
            if (authUsername == null) {
                clientHandler.sendMsg("Некоректный логин / пароль");
                return false;
            }
            if (server.isUsernameBusy(authUsername)) {
                clientHandler.sendMsg("Указанная учетная запись уже занята");
                return false;
            }
            clientHandler.setUsername(authUsername);
            clientHandler.sendMsg("Вы подключились под ником: " + authUsername);
            server.subscribe(clientHandler);
            clientHandler.setRole(getUserRoleByUsername(authUsername));
            if (clientHandler.getRole() == UserRole.ADMIN) {
                clientHandler.sendMsg(ConsoleColors.RED_BOLD + "  Вы подключились под ролью Администратора!"+ConsoleColors.RESET);
            }
            clientHandler.sendMsg("/authok " + authUsername);
            return true;
        }
    }

    private boolean isLoginAlreadyExists(String login) {
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUsernameAlreadyExists(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean register(ClientHandler clientHandler, String login, String password, String username) {
        if (login.trim().length() < 3) {
            clientHandler.sendMsg("Логин должен состоять из 3+ символов");
            return false;
        }
        if (username.trim().length() < 3) {
            clientHandler.sendMsg("Имя пользователя должна состоять из 3+ символов");
            return false;
        }
        if (isLoginAlreadyExists(login)) {
            clientHandler.sendMsg("Указанный логин уже занят");
            return false;
        }
        if (isUsernameAlreadyExists(username)) {
            clientHandler.sendMsg("Указанное имя пользователя уже занято");
            return false;
        }
        User newUser = new User(login, password, username);
        try {
            AuthDataBase.insertUser(newUser);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        users.add(newUser);
        clientHandler.setUsername(username);
        clientHandler.sendMsg("Вы успешно зарегистрировались и подключились под ником: " + username);
        server.subscribe(clientHandler);
        clientHandler.sendMsg("/regok " + username);
        return true;
    }
}
