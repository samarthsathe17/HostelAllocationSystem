package com.hostel.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            Properties props = new Properties();
            InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties");
            if (in != null) {
                props.load(in);
            }

            String url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/hostel_db?serverTimezone=UTC");
            String user = props.getProperty("db.user", "root");
            String pass = props.getProperty("db.password", "samarth1234");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException | java.io.IOException e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}