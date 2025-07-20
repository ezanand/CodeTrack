package com.codetrack.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

public class Problem {
    private int id;
    private String title;
    private String description;
    private String difficulty;
    private String testCasesJson;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator
    public Problem(@JsonProperty("id") int id,
                   @JsonProperty("title") String title,
                   @JsonProperty("description") String description,
                   @JsonProperty("difficulty") String difficulty,
                   @JsonProperty("testCasesJson") String testCasesJson) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.testCasesJson = testCasesJson;
    }

    public Problem(String title, String description, String difficulty, String testCasesJson) {
        this(0, title, description, difficulty, testCasesJson);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getTestCasesJson() {
        return testCasesJson;
    }

    public List<TestCase> getTestCases() {
        try {
            if (testCasesJson == null || testCasesJson.isEmpty()) return List.of();
            return objectMapper.readValue(
                    testCasesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TestCase.class)
            );
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing test cases for problem " + title + ": " + e.getMessage());
            return List.of();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setTestCasesJson(String testCasesJson) {
        this.testCasesJson = testCasesJson;
    }

    public static class TestCase {
        private String input;
        private String expectedOutput;
        private String description;

        @JsonCreator
        public TestCase(@JsonProperty("input") String input,
                        @JsonProperty("expectedOutput") String expectedOutput,
                        @JsonProperty("description") String description) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.description = description;
        }

        public String getInput() {
            return input;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public String getDescription() {
            return description;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "TestCase{" +
                    "input='" + input + '\'' +
                    ", expectedOutput='" + expectedOutput + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestCase)) return false;
            TestCase tc = (TestCase) o;
            return Objects.equals(input, tc.input) &&
                   Objects.equals(expectedOutput, tc.expectedOutput) &&
                   Objects.equals(description, tc.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(input, expectedOutput, description);
        }
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", description='" + preview(description) + '\'' +
                ", testCasesJson='" + preview(testCasesJson) + '\'' +
                '}';
    }

    private String preview(String value) {
        if (value == null) return "";
        return value.length() > 50 ? value.substring(0, 50) + "..." : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Problem)) return false;
        Problem that = (Problem) o;
        return id == that.id &&
               Objects.equals(title, that.title) &&
               Objects.equals(description, that.description) &&
               Objects.equals(difficulty, that.difficulty) &&
               Objects.equals(testCasesJson, that.testCasesJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, difficulty, testCasesJson);
    }
}
