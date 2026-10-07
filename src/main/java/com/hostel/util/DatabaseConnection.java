package com.hostel.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    private DatabaseConnection() throws SQLException {
        loadProperties();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            System.err.println("Database Connection Creation Failed : " + ex.getMessage());
            ex.printStackTrace();
            throw new SQLException("Database driver not found", ex);
        }
    }

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (in != null) {
                props.load(in);
                this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/hostel_db?serverTimezone=UTC");
                this.username = props.getProperty("db.user", "root");
                this.password = props.getProperty("db.password", "samarth1234");
            } else {
                System.err.println("Could not find database.properties, using defaults");
                this.url = "jdbc:mysql://localhost:3306/hostel_db?serverTimezone=UTC";
                this.username = "root";
                this.password = "samarth1234";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.connection == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance.connection;
    }
}
