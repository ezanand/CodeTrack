package com.codetrack.service;

import com.codetrack.dao.SubmissionDAO;
import com.codetrack.model.Submission;

import java.util.List;
import java.util.Optional;

public class SubmissionService {
    private final SubmissionDAO submissionDAO;

    public SubmissionService(SubmissionDAO submissionDAO) {
        this.submissionDAO = submissionDAO;
    }

    public Optional<Submission> addSubmission(Submission submission) {
        System.out.println("Recording submission for problem ID: " + submission.getProblemId());
        return submissionDAO.addSubmission(submission);
    }

    public Optional<Submission> getSubmissionById(int id) {
        return submissionDAO.getSubmissionById(id);
    }

    public List<Submission> getSubmissionsByProblemId(int problemId) {
        return submissionDAO.getSubmissionsByProblemId(problemId);
    }

    public List<Submission> getAllSubmissions() {
        return submissionDAO.getAllSubmissions();
    }

    public boolean deleteSubmission(int id) {
        System.out.println("Deleting submission with ID: " + id);
        return submissionDAO.deleteSubmission(id);
    }
}
