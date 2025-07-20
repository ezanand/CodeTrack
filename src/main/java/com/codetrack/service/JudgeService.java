package com.codetrack.service;

import com.codetrack.model.Problem;
import com.codetrack.model.Problem.TestCase;
import com.codetrack.solutionbank.SolutionBank;
import com.codetrack.util.TimerUtil;
import com.codetrack.util.TimerUtil.TimedResult;

import java.util.List;
import java.util.concurrent.*;

public class JudgeService {

    private static final long EXECUTION_TIMEOUT_MS = 2000; // 2 seconds

    public JudgeResult judgeSolution(Problem problem, SolutionBank solution) {
        long totalExecutionTime = 0;
        String overallStatus = "Accepted";

        List<TestCase> testCases = problem.getTestCases();
        if (testCases.isEmpty()) {
            System.out.println("Warning: No test cases found for problem '" + problem.getTitle() + "'. Assuming Accepted.");
            return new JudgeResult("Accepted", 0);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            System.out.println("\n--- Running Test Case " + (i + 1) + " ---");
            System.out.println("Input: " + testCase.getInput());
            if (testCase.getDescription() != null && !testCase.getDescription().isEmpty()) {
                System.out.println("Description: " + testCase.getDescription());
            }

            String actualOutput = null;
            long testCaseExecutionTime = 0;
            String testCaseStatus = "Pending";

            try {
                Callable<String> solutionTask = () -> solution.solve(testCase.getInput());
                Future<String> future = executor.submit(solutionTask);

                TimedResult<String> timedResult = TimerUtil.measureExecutionTime(() -> {
                    try {
                        return future.get(EXECUTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException e) {
                        future.cancel(true);
                        throw new RuntimeException("Time Limit Exceeded", e);
                    } catch (Exception e) {
                        throw new RuntimeException("Runtime Error during execution", e);
                    }
                });

                actualOutput = timedResult.getResult();
                testCaseExecutionTime = timedResult.getExecutionTimeMs();

                System.out.println("Actual Output: " + actualOutput);
                System.out.println("Expected Output: " + testCase.getExpectedOutput());
                System.out.println("Execution Time: " + testCaseExecutionTime + " ms");

                if (isValidTwoSumOutput(testCase.getInput(), actualOutput, testCase.getExpectedOutput())) {
                    testCaseStatus = "Passed";
                    System.out.println("Status: Passed");
                } else {
                    testCaseStatus = "Wrong Answer";
                    overallStatus = "Wrong Answer";
                    System.out.println("Status: Wrong Answer");
                }

            } catch (RuntimeException e) {
                if (e.getMessage().equals("Time Limit Exceeded")) {
                    testCaseStatus = "Time Limit Exceeded";
                    overallStatus = "Time Limit Exceeded";
                    System.err.println("Status: Time Limit Exceeded (>" + EXECUTION_TIMEOUT_MS + " ms)");
                } else if (e.getMessage().startsWith("Runtime Error")) {
                    testCaseStatus = "Runtime Error";
                    overallStatus = "Runtime Error";
                    System.err.println("Status: Runtime Error - " + e.getCause().getMessage());
                } else {
                    testCaseStatus = "Unknown Error";
                    overallStatus = "Unknown Error";
                    System.err.println("Status: Unknown Error - " + e.getMessage());
                }
                if (testCaseStatus.equals("Time Limit Exceeded")) {
                    testCaseExecutionTime = EXECUTION_TIMEOUT_MS;
                }
            } catch (Exception e) {
                testCaseStatus = "Unexpected Error";
                overallStatus = "Unexpected Error";
                System.err.println("Status: Unexpected Error during judging - " + e.getMessage());
            } finally {
                totalExecutionTime += testCaseExecutionTime;
            }
        }

        executor.shutdownNow();
        return new JudgeResult(overallStatus, totalExecutionTime);
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    // Flexible validation logic for Two Sum style output
    private boolean isValidTwoSumOutput(String input, String actualOutput, String expectedOutput) {
        try {
            if (!expectedOutput.matches("\\[\\d+,\\d+]") || !actualOutput.matches("\\[\\d+,\\d+]")) {
                return normalize(actualOutput).equals(normalize(expectedOutput));
            }

            // Parse nums from input string like "nums=[2,7,11,15],target=9"
            int startIdx = input.indexOf('[');
            int endIdx = input.indexOf(']');
            String[] numParts = input.substring(startIdx + 1, endIdx).split(",");
            int[] nums = new int[numParts.length];
            for (int i = 0; i < numParts.length; i++) {
                nums[i] = Integer.parseInt(numParts[i].trim());
            }

            int target = Integer.parseInt(input.substring(input.indexOf("target=") + 7).trim());

            // Parse indices from actual output
            String[] indexParts = actualOutput.replaceAll("[\\[\\]]", "").split(",");
            if (indexParts.length != 2) return false;
            int idx1 = Integer.parseInt(indexParts[0].trim());
            int idx2 = Integer.parseInt(indexParts[1].trim());

            if (idx1 == idx2 || idx1 >= nums.length || idx2 >= nums.length) return false;

            return nums[idx1] + nums[idx2] == target;

        } catch (Exception e) {
            return normalize(actualOutput).equals(normalize(expectedOutput));
        }
    }

    public static class JudgeResult {
        private final String status;
        private final long totalExecutionTimeMs;

        public JudgeResult(String status, long totalExecutionTimeMs) {
            this.status = status;
            this.totalExecutionTimeMs = totalExecutionTimeMs;
        }

        public String getStatus() {
            return status;
        }

        public long getTotalExecutionTimeMs() {
            return totalExecutionTimeMs;
        }

        @Override
        public String toString() {
            return "JudgeResult{" +
                    "status='" + status + '\'' +
                    ", totalExecutionTimeMs=" + totalExecutionTimeMs +
                    '}';
        }
    }
}
