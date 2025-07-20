package com.codetrack.gui;

import com.codetrack.dao.DatabaseManager;
import com.codetrack.dao.ProblemDAO;
import com.codetrack.dao.SubmissionDAO;
import com.codetrack.model.Problem;
import com.codetrack.model.Submission;
import com.codetrack.service.JudgeService;
import com.codetrack.service.ProblemService;
import com.codetrack.service.SubmissionService;
import com.codetrack.solutionbank.FibonacciSolution;
import com.codetrack.solutionbank.PalindromeCheckSolution;
import com.codetrack.solutionbank.ReverseWordsSolution;
import com.codetrack.solutionbank.SolutionBank;
import com.codetrack.solutionbank.TwoSumSolution;
import com.codetrack.solutionbank.ValidParenthesesSolution;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CodeTrackGUI extends JFrame {

    private final ProblemService problemService;
    private final SubmissionService submissionService;
    private final JudgeService judgeService;
    private final Map<Integer, Supplier<SolutionBank>> solutionBank;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private HomePanel homePanel;
    private ProblemListPanel problemListPanel;
    private ProblemDetailPanel problemDetailPanel;
    private SubmissionListPanel submissionListPanel;
    private AddProblemPanel addProblemPanel;

    public CodeTrackGUI() {
        super("CodeTrack - Mini Coding Assessment Portal");

        DatabaseManager.initializeDatabase();
        ProblemDAO problemDAO = new ProblemDAO();
        SubmissionDAO submissionDAO = new SubmissionDAO();
        this.problemService = new ProblemService(problemDAO);
        this.submissionService = new SubmissionService(submissionDAO);
        this.judgeService = new JudgeService();
        this.solutionBank = new ConcurrentHashMap<>();

        loadInitialProblemsAndMapSolutions();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        createAndAddPanels();
        cardLayout.show(mainPanel, "Home");
    }

    private void loadInitialProblemsAndMapSolutions() {
            problemService.loadProblemsFromJson("problems.json");
        
        List<Problem> problems = problemService.getAllProblems();
      for (Problem p : problems) {
    switch (p.getTitle().toLowerCase().trim()) {
        case "two sum":
            solutionBank.put(p.getId(), TwoSumSolution::new);
            break;
        case "palindrome check":
            solutionBank.put(p.getId(), PalindromeCheckSolution::new);
            break;
        case "fibonacci":
            solutionBank.put(p.getId(), FibonacciSolution::new);
            break;
        case "reverse words":
            solutionBank.put(p.getId(), ReverseWordsSolution::new);
            break;
        case "valid parentheses":
            solutionBank.put(p.getId(), ValidParenthesesSolution::new);
            break;
        // Add more cases as needed
    }
}

    }

    private void createAndAddPanels() {
        homePanel = new HomePanel(
                this::showProblemList,
                this::showSubmissionList,
                () -> showProblemForm(null));
        mainPanel.add(homePanel, "Home");

        problemListPanel = new ProblemListPanel(
                problemService,
                this::showProblemDetail,
                (problemId, problem) -> showProblemForm(problem),
                this::deleteProblem,
                () -> cardLayout.show(mainPanel, "Home"));
        mainPanel.add(problemListPanel, "ProblemList");

        problemDetailPanel = new ProblemDetailPanel(
                problemService,
                judgeService,
                solutionBank,
                (outputStream) -> {
                    System.setOut(new PrintStream(outputStream));
                    System.setErr(new PrintStream(outputStream));
                },
                () -> {
                    System.setOut(System.out);
                    System.setErr(System.err);
                },
                submissionService,
                this::showProblemList,
                this::deleteProblem);
        mainPanel.add(problemDetailPanel, "ProblemDetail");

        submissionListPanel = new SubmissionListPanel(
                submissionService,
                () -> cardLayout.show(mainPanel, "Home"));
        mainPanel.add(submissionListPanel, "SubmissionList");

        addProblemPanel = new AddProblemPanel(
                problemService,
                solutionBank,
                () -> cardLayout.show(mainPanel, "Home"),
                this::showProblemList);
        mainPanel.add(addProblemPanel, "AddProblem");
    }

    private void showProblemList() {
        problemListPanel.populateProblemTable();
        cardLayout.show(mainPanel, "ProblemList");
    }

    private void showProblemDetail(int problemId) {
        problemDetailPanel.displayProblem(problemId);
        cardLayout.show(mainPanel, "ProblemDetail");
    }

    private void showSubmissionList() {
        submissionListPanel.populateSubmissionTable();
        cardLayout.show(mainPanel, "SubmissionList");
    }

    private void showProblemForm(Problem problem) {
        addProblemPanel.loadProblemForEdit(problem);
        cardLayout.show(mainPanel, "AddProblem");
    }

    private void deleteProblem(int problemId) {
        List<Submission> submissionsToDelete = submissionService.getSubmissionsByProblemId(problemId);
        for (Submission sub : submissionsToDelete) {
            submissionService.deleteSubmission(sub.getId());
        }

        if (problemService.deleteProblem(problemId)) {
            JOptionPane.showMessageDialog(this,
                    "Problem (ID: " + problemId + ") and its submissions deleted successfully.",
                    "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
            showProblemList();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete problem (ID: " + problemId + ").",
                    "Deletion Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CodeTrackGUI app = new CodeTrackGUI();
            app.setVisible(true);
        });
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(65, 105, 225));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 80, 200), 1),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));
        return button;
    }

    public static class TextAreaOutputStream extends java.io.OutputStream {
        private final JTextArea textArea;
        private final PrintStream originalStream;
        private final StringBuilder buffer = new StringBuilder();

        public TextAreaOutputStream(JTextArea textArea, PrintStream originalStream) {
            this.textArea = textArea;
            this.originalStream = originalStream;
        }

        @Override
        public void write(int b) {
            buffer.append((char) b);
            if (b == '\n')
                flush();
            originalStream.write(b);
        }

        @Override
        public void flush() {
            SwingUtilities.invokeLater(() -> {
                textArea.append(buffer.toString());
                buffer.setLength(0);
            });
        }
    }
}
