package com.codetrack.solutionbank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Example implementation of the SolutionBank for the "Two Sum" problem.
 * Problem Description: Given an array of integers nums and an integer target,
 * return indices of the two numbers such that they add up to target.
 * You may assume that each input would have exactly one solution, and you may not use the same element twice.
 *
 * Input format: "nums=[1,2,3,4,5],target=7"
 * Output format: "[index1,index2]" (e.g., "[2,3]")
 */
public class TwoSumSolution implements SolutionBank {

    @Override
    public String solve(String input) {
        try {
            // Expected input format: "nums=[...],target=..."
            // Find the index of the closing bracket of the nums array
            int closingBracketIndex = input.indexOf("]");
            if (closingBracketIndex == -1) {
                throw new IllegalArgumentException("Invalid input format: missing ']' for nums array.");
            }

            // Extract the part containing the nums array, including brackets
            String numsPartWithBrackets = input.substring(0, closingBracketIndex + 1);
            // Extract the content inside the brackets: "1,2,3,4,5"
            String numsContent = numsPartWithBrackets.substring(numsPartWithBrackets.indexOf("[") + 1, numsPartWithBrackets.indexOf("]"));

            // Parse the nums array
            int[] nums = Arrays.stream(numsContent.split(","))
                               .map(String::trim)
                               .mapToInt(Integer::parseInt)
                               .toArray();

            // Extract the target part
            int targetStartIndex = input.indexOf("target=", closingBracketIndex); // Start search after nums array
            if (targetStartIndex == -1) {
                throw new IllegalArgumentException("Invalid input format: missing 'target='.");
            }
            String targetPart = input.substring(targetStartIndex);
            int target = Integer.parseInt(targetPart.split("=")[1].trim());

            // Core Two Sum logic using a HashMap
            Map<Integer, Integer> numMap = new HashMap<>();
            for (int i = 0; i < nums.length; i++) {
                int complement = target - nums[i];
                if (numMap.containsKey(complement)) {
                    // Found the two numbers
                    return "[" + numMap.get(complement) + "," + i + "]";
                }
                numMap.put(nums[i], i);
            }

            // Should not reach here if "exactly one solution" assumption holds
            return "[]"; // No solution found (though problem states there will be one)

        } catch (NumberFormatException e) {
            System.err.println("Error parsing numbers in TwoSumSolution: " + e.getMessage());
            return "Error: Invalid number format in input.";
        } catch (IllegalArgumentException e) {
            System.err.println("Error in TwoSumSolution input format: " + e.getMessage());
            return "Error: Invalid input format.";
        } catch (Exception e) {
            System.err.println("An unexpected error occurred in TwoSumSolution: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging unexpected issues
            return "Error: An unexpected runtime issue occurred.";
        }
    }
}
