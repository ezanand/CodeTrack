package com.codetrack.solutionbank;

import java.util.Stack;

public class ValidParenthesesSolution implements SolutionBank {

    @Override
    public String solve(String input) {
        try {
            String expr = input.split("=")[1].trim();
            Stack<Character> stack = new Stack<>();
            for (char ch : expr.toCharArray()) {
                if (ch == '(' || ch == '[' || ch == '{') {
                    stack.push(ch);
                } else if (ch == ')' && !stack.isEmpty() && stack.peek() == '(') {
                    stack.pop();
                } else if (ch == ']' && !stack.isEmpty() && stack.peek() == '[') {
                    stack.pop();
                } else if (ch == '}' && !stack.isEmpty() && stack.peek() == '{') {
                    stack.pop();
                } else if (ch == ')' || ch == ']' || ch == '}') {
                    return "false";
                }
            }
            return stack.isEmpty() ? "true" : "false";
        } catch (Exception e) {
            return "Error: Invalid input format. Expected input=expression";
        }
    }
}
