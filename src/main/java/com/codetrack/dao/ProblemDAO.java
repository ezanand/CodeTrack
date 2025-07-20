package com.codetrack.dao;

import com.codetrack.model.Problem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProblemDAO {

    public Optional<Problem> addProblem(Problem problem) {
        String sql = "INSERT INTO problems(title, description, difficulty, test_cases_json) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, problem.getTitle());
            pstmt.setString(2, problem.getDescription());
            pstmt.setString(3, problem.getDifficulty());
            pstmt.setString(4, problem.getTestCasesJson());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        problem.setId(generatedKeys.getInt(1));
                        return Optional.of(problem);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: problems.title")) {
                System.err.println("Error: A problem with title '" + problem.getTitle() + "' already exists.");
            } else {
                System.err.println("Error adding problem: " + e.getMessage());
            }
        }
        return Optional.empty();
    }

    public Optional<Problem> getProblemById(int id) {
        String sql = "SELECT id, title, description, difficulty, test_cases_json FROM problems WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Problem(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("difficulty"),
                        rs.getString("test_cases_json")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting problem by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Problem> getAllProblems() {
        List<Problem> problems = new ArrayList<>();
        String sql = "SELECT id, title, description, difficulty, test_cases_json FROM problems";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                problems.add(new Problem(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("difficulty"),
                        rs.getString("test_cases_json")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all problems: " + e.getMessage());
        }
        return problems;
    }

    public boolean updateProblem(Problem problem) {
        String sql = "UPDATE problems SET title = ?, description = ?, difficulty = ?, test_cases_json = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, problem.getTitle());
            pstmt.setString(2, problem.getDescription());
            pstmt.setString(3, problem.getDifficulty());
            pstmt.setString(4, problem.getTestCasesJson());
            pstmt.setInt(5, problem.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating problem: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteProblem(int id) {
        String sql = "DELETE FROM problems WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting problem: " + e.getMessage());
        }
        return false;
    }
}
