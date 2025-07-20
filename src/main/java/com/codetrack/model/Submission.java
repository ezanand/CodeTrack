package com.codetrack.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Submission {
    private int id;
    private int problemId;
    private String submittedCode;
    private String status;
    private long executionTimeMs;
    private LocalDateTime submissionTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Submission(int id, int problemId, String submittedCode, String status, long executionTimeMs, LocalDateTime submissionTime) {
        this.id = id;
        this.problemId = problemId;
        this.submittedCode = submittedCode;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.submissionTime = submissionTime;
    }

    public Submission(int problemId, String submittedCode, String status, long executionTimeMs) {
        this(0, problemId, submittedCode, status, executionTimeMs, LocalDateTime.now());
    }

    public int getId() {
        return id;
    }

    public int getProblemId() {
        return problemId;
    }

    public String getSubmittedCode() {
        return submittedCode;
    }

    public String getStatus() {
        return status;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public void setSubmittedCode(String submittedCode) {
        this.submittedCode = submittedCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", problemId=" + problemId +
                ", status='" + status + '\'' +
                ", executionTimeMs=" + executionTimeMs + "ms" +
                ", submissionTime=" + submissionTime.format(FORMATTER) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Submission)) return false;
        Submission that = (Submission) o;
        return id == that.id &&
                problemId == that.problemId &&
                executionTimeMs == that.executionTimeMs &&
                Objects.equals(submittedCode, that.submittedCode) &&
                Objects.equals(status, that.status) &&
                Objects.equals(submissionTime, that.submissionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, problemId, submittedCode, status, executionTimeMs, submissionTime);
    }
}
