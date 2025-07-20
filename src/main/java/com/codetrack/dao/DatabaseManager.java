package com.codetrack.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:codetrack.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            String createProblemsTableSQL = "CREATE TABLE IF NOT EXISTS problems ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "title TEXT NOT NULL UNIQUE, "
                    + "description TEXT NOT NULL, "
                    + "difficulty TEXT NOT NULL, "
                    + "test_cases_json TEXT NOT NULL"
                    + ");";

            stmt.execute(createProblemsTableSQL);
            System.out.println("Table 'problems' checked/created successfully.");

            String createSubmissionsTableSQL = "CREATE TABLE IF NOT EXISTS submissions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "problem_id INTEGER NOT NULL, "
                    + "submitted_code TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "execution_time_ms INTEGER NOT NULL, "
                    + "submission_time TEXT NOT NULL, "
                    + "FOREIGN KEY (problem_id) REFERENCES problems(id)"
                    + ");";

            stmt.execute(createSubmissionsTableSQL);
            System.out.println("Table 'submissions' checked/created successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
