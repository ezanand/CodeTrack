package com.codetrack.service;

import com.codetrack.dao.ProblemDAO;
import com.codetrack.model.Problem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class ProblemService {
    private final ProblemDAO problemDAO;
    private final ObjectMapper objectMapper;

    public ProblemService(ProblemDAO problemDAO) {
        this.problemDAO = problemDAO;
        this.objectMapper = new ObjectMapper();
    }

    public Optional<Problem> addProblem(Problem problem) {
        System.out.println("Adding problem: " + problem.getTitle());
        return problemDAO.addProblem(problem);
    }

    public Optional<Problem> getProblemById(int id) {
        return problemDAO.getProblemById(id);
    }

    public List<Problem> getAllProblems() {
        return problemDAO.getAllProblems();
    }

    public boolean updateProblem(Problem problem) {
        System.out.println("Updating problem: " + problem.getTitle());
        return problemDAO.updateProblem(problem);
    }

    public boolean deleteProblem(int id) {
        System.out.println("Deleting problem with ID: " + id);
        return problemDAO.deleteProblem(id);
    }

    public int loadProblemsFromJson(String jsonFilePath) {
        int count = 0;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(jsonFilePath)) {
            if (is == null) {
                System.err.println("File not found: " + jsonFilePath);
                return 0;
            }
            List<Problem> problems = objectMapper.readValue(is, new TypeReference<>() {});
            for (Problem p : problems) {
                Problem newProblem = new Problem(p.getTitle(), p.getDescription(), p.getDifficulty(), p.getTestCasesJson());
                if (addProblem(newProblem).isPresent()) {
                    count++;
                }
            }
            System.out.println("Loaded " + count + " problems from " + jsonFilePath);
        } catch (IOException e) {
            System.err.println("Failed to load problems: " + e.getMessage());
        }
        return count;
    }
}
