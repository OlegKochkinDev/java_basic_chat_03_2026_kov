package ru.otus.server.auth;

import java.sql.*;

public class AuthDataBase {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement psInsert;
    private static boolean isConnected;


    public static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:auth_db.db");
            statement = connection.createStatement();
            System.out.println("Connected to database auth_db");
            isConnected = true;
        }catch (SQLException | ClassNotFoundException e) {
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
