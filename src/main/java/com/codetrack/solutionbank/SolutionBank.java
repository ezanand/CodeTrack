package com.codetrack.solutionbank;

/**
 * Functional interface representing the solution logic for a coding problem.
 * Each problem will have an implementation of this interface.
 * It takes a String input (which can be parsed into appropriate data types)
 * and returns a String output.
 */
@FunctionalInterface
public interface SolutionBank {
    /**
     * Solves a coding problem given a string input.
     * The implementation should handle parsing the input string into necessary data structures
     * and converting the result back into a string.
     *
     * @param input The input for the problem as a string.
     * @return The output of the solution as a string.
     */
    String solve(String input);
}