package com.codetrack.solutionbank;

public class FibonacciSolution implements SolutionBank {

    @Override
    public String solve(String input) {
        try {
            int n = Integer.parseInt(input.split("=")[1].trim());
            if (n < 0) return "Error: Input must be non-negative";
            if (n == 0) return "0";
            if (n == 1) return "1";

            long a = 0, b = 1;
            for (int i = 2; i <= n; i++) {
                long temp = a + b;
                a = b;
                b = temp;
            }
            return String.valueOf(b);
        } catch (Exception e) {
            return "Error: Invalid input format. Expected input=n";
        }
    }
}
