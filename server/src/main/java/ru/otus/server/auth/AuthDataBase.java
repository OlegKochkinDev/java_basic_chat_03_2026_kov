package ru.otus.server.auth;

import ru.otus.server.user.User;
import ru.otus.server.user.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthDataBase {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psInsert;
    private static boolean isConnected;


    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:auth_db.db");
            statement = connection.createStatement();
            psInsert = connection.prepareStatement("insert into tbl_users (login, pass, username, user_role) values (?, ?, ?, ?);");
            System.out.println("Connected to database auth_db");

            isConnected = true;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void readDemo() throws SQLException {
        try (ResultSet rs = statement.executeQuery("SELECT * FROM tbl_users;")) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
            }
        }
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static List<User> readUsers() {
        List<User> users = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery("\n" +
                "select u.login, u.pass, u.username, role\n" +
                "from tbl_users u\n" +
                "    left join tbl_user_roles ur on ur.role_id = u.user_role;")) {
            while (rs.next()) {

                users.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), UserRole.fromString(rs.getString(4))));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public static void insertUser(User user) throws SQLException {
        psInsert.setString(1, user.getLogin());
        psInsert.setString(2, user.getPassword());
        psInsert.setString(3, user.getPassword());
        psInsert.setInt(4, 1);
        psInsert.executeUpdate();
    }


    private static void disconnect() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (psInsert != null) {
            try {
                psInsert.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
