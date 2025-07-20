package com.codetrack.dao;

import com.codetrack.model.Submission;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubmissionDAO {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Optional<Submission> addSubmission(Submission submission) {
        String sql = "INSERT INTO submissions(problem_id, submitted_code, status, execution_time_ms, submission_time) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, submission.getProblemId());
            pstmt.setString(2, submission.getSubmittedCode());
            pstmt.setString(3, submission.getStatus());
            pstmt.setLong(4, submission.getExecutionTimeMs());
            pstmt.setString(5, submission.getSubmissionTime().format(FORMATTER));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        submission.setId(generatedKeys.getInt(1));
                        return Optional.of(submission);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding submission: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Submission> getSubmissionById(int id) {
        String sql = "SELECT id, problem_id, submitted_code, status, execution_time_ms, submission_time FROM submissions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Submission(
                        rs.getInt("id"),
                        rs.getInt("problem_id"),
                        rs.getString("submitted_code"),
                        rs.getString("status"),
                        rs.getLong("execution_time_ms"),
                        LocalDateTime.parse(rs.getString("submission_time"), FORMATTER)
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting submission by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Submission> getSubmissionsByProblemId(int problemId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT id, problem_id, submitted_code, status, execution_time_ms, submission_time FROM submissions WHERE problem_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, problemId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                submissions.add(new Submission(
                        rs.getInt("id"),
                        rs.getInt("problem_id"),
                        rs.getString("submitted_code"),
                        rs.getString("status"),
                        rs.getLong("execution_time_ms"),
                        LocalDateTime.parse(rs.getString("submission_time"), FORMATTER)
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting submissions by problem ID: " + e.getMessage());
        }
        return submissions;
    }

    public List<Submission> getAllSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT id, problem_id, submitted_code, status, execution_time_ms, submission_time FROM submissions";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                submissions.add(new Submission(
                        rs.getInt("id"),
                        rs.getInt("problem_id"),
                        rs.getString("submitted_code"),
                        rs.getString("status"),
                        rs.getLong("execution_time_ms"),
                        LocalDateTime.parse(rs.getString("submission_time"), FORMATTER)
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all submissions: " + e.getMessage());
        }
        return submissions;
    }

    public boolean deleteSubmission(int id) {
        String sql = "DELETE FROM submissions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting submission: " + e.getMessage());
        }
        return false;
    }
}
