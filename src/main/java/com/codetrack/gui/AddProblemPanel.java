package com.codetrack.gui;

import com.codetrack.model.Problem;
import com.codetrack.service.ProblemService;
import com.codetrack.solutionbank.SolutionBank;
import com.codetrack.solutionbank.TwoSumSolution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class AddProblemPanel extends JPanel {

    private final ProblemService problemService;
    private final Map<Integer, Supplier<SolutionBank>> solutionBank;
    private final Runnable onBackToHome;
    private final Runnable onProblemActionComplete;

    private JLabel formTitleLabel;
    private JTextField addTitleField;
    private JTextArea addDescriptionArea;
    private JComboBox<String> addDifficultyComboBox;
    private JPanel addTestCasesPanel;

    private Problem problemToEdit;

    public AddProblemPanel(ProblemService problemService, Map<Integer, Supplier<SolutionBank>> solutionBank,
            Runnable onBackToHome, Runnable onProblemActionComplete) {
        this.problemService = problemService;
        this.solutionBank = solutionBank;
        this.onBackToHome = onBackToHome;
        this.onProblemActionComplete = onProblemActionComplete;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 245, 255));

        formTitleLabel = new JLabel("Add New Problem", SwingConstants.CENTER);
        formTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        formTitleLabel.setForeground(new Color(33, 33, 50));
        add(formTitleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(250, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createLabel("Problem Title:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addTitleField = new JTextField(30);
        addTitleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(addTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridheight = 2;
        gbc.weighty = 0.5;
        addDescriptionArea = new JTextArea(8, 30);
        addDescriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addDescriptionArea.setLineWrap(true);
        addDescriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(addDescriptionArea);
        formPanel.add(descScrollPane, gbc);
        gbc.gridheight = 1;
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createLabel("Difficulty:"), gbc);
        gbc.gridx = 1;
        addDifficultyComboBox = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
        addDifficultyComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(addDifficultyComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel tcHeader = new JLabel("Test Cases:", SwingConstants.CENTER);
        tcHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tcHeader.setForeground(new Color(52, 73, 94));
        formPanel.add(tcHeader, gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        addTestCasesPanel = new JPanel();
        addTestCasesPanel.setLayout(new BoxLayout(addTestCasesPanel, BoxLayout.Y_AXIS));
        addTestCasesPanel.setBackground(new Color(245, 245, 255));
        JScrollPane tcScrollPane = new JScrollPane(addTestCasesPanel);
        tcScrollPane.setPreferredSize(new Dimension(420, 200));
        formPanel.add(tcScrollPane, gbc);
        gbc.weighty = 0;

        JButton addTestCaseButton = CodeTrackGUI.createStyledButton("Add Test Case");
        addTestCaseButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addTestCaseButton.addActionListener(e -> addTestCaseInputFields(null));
        gbc.gridy = 6;
        formPanel.add(addTestCaseButton, gbc);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(formPanel.getBackground());
        wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 6, 6, new Color(200, 200, 240)),
                BorderFactory.createLineBorder(new Color(180, 180, 220))));
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 255));

        JButton backButton = CodeTrackGUI.createStyledButton("Back to Home");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.addActionListener(e -> onBackToHome.run());
        buttonPanel.add(backButton);

        JButton submitButton = CodeTrackGUI.createStyledButton("Save Problem");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.addActionListener(e -> submitProblem());
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(33, 33, 33));
        return label;
    }

    private void addTestCaseInputFields(Problem.TestCase tc) {
        JPanel tcRowPanel = new JPanel(new GridBagLayout());
        tcRowPanel.setName("testCasePanel_" + System.currentTimeMillis());
        tcRowPanel.setBackground(new Color(255, 255, 250));
        tcRowPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 5, 5, new Color(210, 210, 240)),
                        BorderFactory.createLineBorder(new Color(160, 160, 200))),
                "Test Case " + (addTestCasesPanel.getComponentCount() + 1),
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        tcRowPanel.add(createLabel("Input:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField inputField = new JTextField(25);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputField.setName("inputField");
        if (tc != null)
            inputField.setText(tc.getInput());
        tcRowPanel.add(inputField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        tcRowPanel.add(createLabel("Expected Output:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField outputField = new JTextField(25);
        outputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        outputField.setName("expectedOutputField");
        if (tc != null)
            outputField.setText(tc.getExpectedOutput());
        tcRowPanel.add(outputField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        tcRowPanel.add(createLabel("Description (Optional):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField descField = new JTextField(25);
        descField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descField.setName("descriptionField");
        if (tc != null && tc.getDescription() != null)
            descField.setText(tc.getDescription());
        tcRowPanel.add(descField, gbc);

        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(new Color(231, 76, 60));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        removeButton.setFocusPainted(false);
        removeButton.addActionListener(e -> {
            addTestCasesPanel.remove(tcRowPanel);
            reindexTestCases();
            addTestCasesPanel.revalidate();
            addTestCasesPanel.repaint();
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        tcRowPanel.add(removeButton, gbc);

        addTestCasesPanel.add(tcRowPanel);
        addTestCasesPanel.revalidate();
        addTestCasesPanel.repaint();
    }

    private void reindexTestCases() {
        Component[] components = addTestCasesPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JPanel) {
                JPanel tcRowPanel = (JPanel) components[i];
                ((TitledBorder) tcRowPanel.getBorder()).setTitle("Test Case " + (i + 1));
            }
        }
    }

    private void submitProblem() {
        String title = addTitleField.getText().trim();
        String description = addDescriptionArea.getText().trim();
        String difficulty = (String) addDifficultyComboBox.getSelectedItem();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Description cannot be empty.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Problem.TestCase> testCases = new ArrayList<>();
        for (Component comp : addTestCasesPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel tcRowPanel = (JPanel) comp;
                JTextField inputField = (JTextField) findComponentByName(tcRowPanel, "inputField");
                JTextField expectedOutputField = (JTextField) findComponentByName(tcRowPanel, "expectedOutputField");
                JTextField descriptionField = (JTextField) findComponentByName(tcRowPanel, "descriptionField");

                if (inputField != null && expectedOutputField != null) {
                    String input = inputField.getText().trim();
                    String expectedOutput = expectedOutputField.getText().trim();
                    String tcDescription = (descriptionField != null) ? descriptionField.getText().trim() : "";

                    if (input.isEmpty() || expectedOutput.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Test Case input and expected output cannot be empty.",
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    testCases.add(new Problem.TestCase(input, expectedOutput,
                            tcDescription.isEmpty() ? null : tcDescription));
                }
            }
        }

        String testCasesJson = "[]";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            testCasesJson = objectMapper.writeValueAsString(testCases);
        } catch (JsonProcessingException e) {
            JOptionPane.showMessageDialog(this, "Error converting test cases to JSON: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = false;
        String actionMessage = "";

        if (problemToEdit == null) {
            Problem newProblem = new Problem(title, description, difficulty, testCasesJson);
            Optional<Problem> addedProblem = problemService.addProblem(newProblem);
            if (addedProblem.isPresent()) {
                success = true;
                actionMessage = "Problem '" + addedProblem.get().getTitle() + "' added successfully with ID: "
                        + addedProblem.get().getId();
                if ("Two Sum".equalsIgnoreCase(addedProblem.get().getTitle())) {
                    solutionBank.put(addedProblem.get().getId(), TwoSumSolution::new);
                    System.out.println("GUI: TwoSumSolution mapped to this new problem.");
                }
            } else {
                actionMessage = "Failed to add problem. A problem with this title might already exist.";
            }
        } else {
            problemToEdit.setTitle(title);
            problemToEdit.setDescription(description);
            problemToEdit.setDifficulty(difficulty);
            problemToEdit.setTestCasesJson(testCasesJson);
            if (problemService.updateProblem(problemToEdit)) {
                success = true;
                actionMessage = "Problem '" + problemToEdit.getTitle() + "' (ID: " + problemToEdit.getId()
                        + ") updated successfully.";
                if ("Two Sum".equalsIgnoreCase(problemToEdit.getTitle())) {
                    solutionBank.put(problemToEdit.getId(), TwoSumSolution::new);
                    System.out.println("GUI: TwoSumSolution re-mapped to updated Problem ID: " + problemToEdit.getId());
                } else {
                    solutionBank.remove(problemToEdit.getId());
                }
            } else {
                actionMessage = "Failed to update problem '" + problemToEdit.getTitle() + "'.";
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, actionMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
            onProblemActionComplete.run();
        } else {
            JOptionPane.showMessageDialog(this, actionMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName()))
                return component;
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    public void resetForm() {
        formTitleLabel.setText("Add New Problem");
        problemToEdit = null;
        addTitleField.setText("");
        addDescriptionArea.setText("");
        addDifficultyComboBox.setSelectedIndex(0);
        addTestCasesPanel.removeAll();
        addTestCaseInputFields(null);
        addTestCasesPanel.revalidate();
        addTestCasesPanel.repaint();
    }

    public void loadProblemForEdit(Problem problem) {
        if (problem == null) {
            resetForm();
            return;
        }

        this.problemToEdit = problem;
        formTitleLabel.setText("Edit Problem (ID: " + problem.getId() + ")");
        addTitleField.setText(problem.getTitle());
        addDescriptionArea.setText(problem.getDescription());
        addDifficultyComboBox.setSelectedItem(problem.getDifficulty());

        addTestCasesPanel.removeAll();
        List<Problem.TestCase> testCases = problem.getTestCases();
        if (testCases.isEmpty()) {
            addTestCaseInputFields(null);
        } else {
            for (Problem.TestCase tc : testCases) {
                addTestCaseInputFields(tc);
            }
        }
        addTestCasesPanel.revalidate();
        addTestCasesPanel.repaint();
    }
}
