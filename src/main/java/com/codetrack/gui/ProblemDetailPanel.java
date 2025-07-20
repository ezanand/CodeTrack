package com.codetrack.gui;

import com.codetrack.model.Problem;
import com.codetrack.model.Submission;
import com.codetrack.service.JudgeService;
import com.codetrack.service.ProblemService;
import com.codetrack.service.SubmissionService;
import com.codetrack.solutionbank.SolutionBank;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.OutputStream;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ProblemDetailPanel extends JPanel {

    private final ProblemService problemService;
    private final JudgeService judgeService;
    private final SubmissionService submissionService;
    private final Map<Integer, Supplier<SolutionBank>> solutionBank;
    private final Consumer<OutputStream> redirectOutput;
    private final Runnable restoreOutput;
    private final Runnable onBack;
    private final Consumer<Integer> onDeleteProblem;

    private JLabel titleLabel;
    private JLabel difficultyLabel;
    private JTextArea descriptionArea;
    private JTextArea testCasesArea;
    private JTextArea codeInputArea;
    private JTextArea resultArea;

    private int selectedProblemId;

    public ProblemDetailPanel(ProblemService problemService,
            JudgeService judgeService,
            Map<Integer, Supplier<SolutionBank>> solutionBank,
            Consumer<OutputStream> redirectOutput,
            Runnable restoreOutput,
            SubmissionService submissionService,
            Runnable onBack,
            Consumer<Integer> onDeleteProblem) {
        this.problemService = problemService;
        this.judgeService = judgeService;
        this.solutionBank = solutionBank;
        this.redirectOutput = redirectOutput;
        this.restoreOutput = restoreOutput;
        this.submissionService = submissionService;
        this.onBack = onBack;
        this.onDeleteProblem = onDeleteProblem;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(235, 245, 255));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(new Color(235, 245, 255));

        titleLabel = new JLabel("Problem Title");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));

        difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        difficultyLabel.setForeground(new Color(70, 130, 180));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        labelPanel.setBackground(new Color(235, 245, 255));
        labelPanel.add(titleLabel);
        labelPanel.add(difficultyLabel);

        headerPanel.add(labelPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Split Pane Center Section
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplit.setResizeWeight(0.5);
        verticalSplit.setBorder(null);

        // Upper Info Panel
        JSplitPane infoSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoSplit.setResizeWeight(0.5);
        infoSplit.setBorder(null);

        descriptionArea = createTextArea(false);
        JScrollPane descScroll = wrapInCard(descriptionArea, "Description");

        testCasesArea = createTextArea(false);
        JScrollPane testScroll = wrapInCard(testCasesArea, "Test Cases");

        infoSplit.setLeftComponent(descScroll);
        infoSplit.setRightComponent(testScroll);

        // Lower Code & Output Panel
        codeInputArea = createTextArea(true);
        codeInputArea.setText("// Write your simulated Java code here");
        JScrollPane codeScroll = wrapInCard(codeInputArea, "Simulated Code");

        resultArea = createTextArea(false);
        resultArea.setText("Execution Output will appear here.");
        JScrollPane outputScroll = wrapInCard(resultArea, "Execution Output");

        JSplitPane codeSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        codeSplit.setResizeWeight(0.5);
        codeSplit.setBorder(null);
        codeSplit.setLeftComponent(codeScroll);
        codeSplit.setRightComponent(outputScroll);

        verticalSplit.setTopComponent(infoSplit);
        verticalSplit.setBottomComponent(codeSplit);
        add(verticalSplit, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(new Color(235, 245, 255));

        JButton backBtn = CodeTrackGUI.createStyledButton("← Back to Problems");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        backBtn.addActionListener(e -> onBack.run());

        JButton runBtn = CodeTrackGUI.createStyledButton("▶ Run Simulated Solution");
        runBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        runBtn.addActionListener(e -> runSimulation());

        footerPanel.add(backBtn);
        footerPanel.add(runBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JTextArea createTextArea(boolean editable) {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(editable);
        return area;
    }

    private JScrollPane wrapInCard(JTextArea area, String title) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 1, true),
                title,
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(60, 60, 120)));
        scrollPane.setBackground(new Color(250, 250, 255));
        return scrollPane;
    }

    public void displayProblem(int problemId) {
        this.selectedProblemId = problemId;

        Optional<Problem> optional = problemService.getProblemById(problemId);
        if (optional.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Problem not found.", "Error", JOptionPane.ERROR_MESSAGE);
            onBack.run();
            return;
        }

        Problem problem = optional.get();
        titleLabel.setText(problem.getTitle());
        difficultyLabel.setText("Difficulty: " + problem.getDifficulty());
        descriptionArea.setText(problem.getDescription());

        StringBuilder cases = new StringBuilder();
        List<Problem.TestCase> testCases = problem.getTestCases();
        if (testCases.isEmpty()) {
            cases.append("No test cases defined.");
        } else {
            for (int i = 0; i < testCases.size(); i++) {
                Problem.TestCase tc = testCases.get(i);
                cases.append("Test Case ").append(i + 1).append(":\n");
                cases.append("  Input: ").append(tc.getInput()).append("\n");
                cases.append("  Expected Output: ").append(tc.getExpectedOutput()).append("\n");
                if (tc.getDescription() != null && !tc.getDescription().isEmpty()) {
                    cases.append("  Description: ").append(tc.getDescription()).append("\n");
                }
                cases.append("\n");
            }
        }
        testCasesArea.setText(cases.toString().trim());
        resultArea.setText("Execution Output will appear here.");
    }

    private void runSimulation() {
        resultArea.setText("Running judge...\n");

        Optional<Problem> optional = problemService.getProblemById(selectedProblemId);
        if (optional.isEmpty()) {
            resultArea.append("Error: Problem not found.\n");
            return;
        }

        Problem problem = optional.get();
        Supplier<SolutionBank> solution = solutionBank.get(problem.getId());

        if (solution == null) {
            resultArea.append("No predefined solution available for this problem.\n");
            return;
        }

        SolutionBank executor = solution.get();

        new SwingWorker<JudgeService.JudgeResult, String>() {
            @Override
            protected JudgeService.JudgeResult doInBackground() {
                redirectOutput.accept(new CodeTrackGUI.TextAreaOutputStream(resultArea, System.out));
                resultArea.append("--- Running Judge ---\n");
                return judgeService.judgeSolution(problem, executor);
            }

            @Override
            protected void done() {
                try {
                    JudgeService.JudgeResult result = get();
                    resultArea.append("\n--- Result ---\n");
                    resultArea.append("Status: " + result.getStatus() + "\n");
                    resultArea.append("Time: " + result.getTotalExecutionTimeMs() + " ms\n");

                    String simulatedCode = codeInputArea.getText();
                    Submission submission = new Submission(problem.getId(), simulatedCode,
                            result.getStatus(), result.getTotalExecutionTimeMs());

                    Optional<Submission> saved = submissionService.addSubmission(submission);
                    if (saved.isPresent()) {
                        resultArea.append("Submission recorded with ID: " + saved.get().getId() + "\n");
                    } else {
                        resultArea.append("Failed to record submission.\n");
                    }
                } catch (Exception e) {
                    resultArea.append("Error during judge: " + e.getMessage() + "\n");
                    e.printStackTrace(System.err);
                } finally {
                    restoreOutput.run();
                }
            }
        }.execute();
    }
}
