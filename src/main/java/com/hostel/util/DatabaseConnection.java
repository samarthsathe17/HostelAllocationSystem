package com.hostel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            String host = System.getenv("MYSQLHOST");
            String port = System.getenv("MYSQLPORT");
            String database = System.getenv("MYSQLDATABASE");
            String user = System.getenv("MYSQLUSER");
            String password = System.getenv("MYSQLPASSWORD");

            if (host == null || host.trim().isEmpty() ||
                database == null || database.trim().isEmpty() ||
                user == null || user.trim().isEmpty() ||
                password == null) {
                String errorMsg = "Database connection error: Missing required environment variables. "
                        + "Please set MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, and MYSQLPASSWORD.";
                System.err.println(errorMsg);
                throw new SQLException(errorMsg);
            }

            if (port == null || port.trim().isEmpty()) {
                port = "3306";
            }

            String url = "jdbc:mysql://" + host.trim() + ":" + port.trim() + "/" + database.trim()
                    + "?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false";

            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to Database -> Host: " + host.trim() + ", Port: " + port.trim() + ", Database: " + database.trim());
            return DriverManager.getConnection(url, user.trim(), password);
        } catch (SQLException e) {
            System.err.println("Database Connection Failed: " + e.getMessage()
                    + " | SQLState: " + e.getSQLState()
                    + " | ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Database Driver Not Found: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}