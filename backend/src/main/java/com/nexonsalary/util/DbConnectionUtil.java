package com.nexonsalary.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/nexonsalary";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";

    private DbConnectionUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}