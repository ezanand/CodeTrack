package com.codetrack.solutionbank;

public class ReverseWordsSolution implements SolutionBank {

    @Override
    public String solve(String input) {
        try {
            String sentence = input.split("=")[1].trim();
            String[] words = sentence.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = words.length - 1; i >= 0; i--) {
                sb.append(words[i]);
                if (i > 0) sb.append(" ");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error: Invalid input format. Expected input=sentence";
        }
    }
}
