package com.codetrack.solutionbank;

public class PalindromeCheckSolution implements SolutionBank {

    @Override
    public String solve(String input) {
        try {
            String word = input.split("=")[1].trim();
            String cleaned = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            int i = 0, j = cleaned.length() - 1;
            while (i < j) {
                if (cleaned.charAt(i++) != cleaned.charAt(j--)) return "false";
            }
            return "true";
        } catch (Exception e) {
            return "Error: Invalid input format. Expected input=word";
        }
    }
}
