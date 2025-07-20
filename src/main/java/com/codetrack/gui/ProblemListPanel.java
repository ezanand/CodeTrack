package com.codetrack.gui;

import com.codetrack.model.Problem;
import com.codetrack.service.ProblemService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class ProblemListPanel extends JPanel {

    private final ProblemService problemService;
    private final Consumer<Integer> onViewProblem;
    private final BiConsumer<Integer, Problem> onEditProblem;
    private final IntConsumer onDeleteProblem;
    private final Runnable onBackToHome;

    private JTable problemTable;
    private DefaultTableModel problemTableModel;

    public ProblemListPanel(ProblemService problemService, Consumer<Integer> onViewProblem,
            BiConsumer<Integer, Problem> onEditProblem, IntConsumer onDeleteProblem,
            Runnable onBackToHome) {
        this.problemService = problemService;
        this.onViewProblem = onViewProblem;
        this.onEditProblem = onEditProblem;
        this.onDeleteProblem = onDeleteProblem;
        this.onBackToHome = onBackToHome;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 248, 255));

        // Header
        JLabel header = new JLabel("Available Problems", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 36));
        header.setForeground(new Color(25, 25, 112));
        header.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 250), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        header.setOpaque(true);
        header.setBackground(new Color(230, 240, 255));
        add(header, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "ID", "Title", "Difficulty" };
        problemTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        problemTable = new JTable(problemTableModel);
        problemTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        problemTable.setRowHeight(28);
        problemTable.setGridColor(new Color(220, 220, 255));

        problemTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        problemTable.getTableHeader().setBackground(new Color(70, 130, 180));
        problemTable.getTableHeader().setForeground(Color.WHITE);
        problemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(problemTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 250), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        scrollPane.setBackground(new Color(250, 250, 255));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 250), 1, true),
                new EmptyBorder(10, 10, 10, 10)));

        JButton backButton = CodeTrackGUI.createStyledButton("← Back to Home");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.addActionListener(e -> onBackToHome.run());
        buttonPanel.add(backButton);

        JButton viewProblemButton = CodeTrackGUI.createStyledButton("🔍 View Selected Problem");
        viewProblemButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewProblemButton.addActionListener(e -> {
            int selectedRow = problemTable.getSelectedRow();
            if (selectedRow != -1) {
                int problemId = (int) problemTableModel.getValueAt(selectedRow, 0);
                onViewProblem.accept(problemId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a problem to view.", "No Problem Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(viewProblemButton);

        JButton editProblemButton = CodeTrackGUI.createStyledButton("✏️ Edit Selected Problem");
        editProblemButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        editProblemButton.setBackground(new Color(255, 165, 0));
        editProblemButton.setForeground(Color.WHITE);
        editProblemButton.addActionListener(e -> {
            int selectedRow = problemTable.getSelectedRow();
            if (selectedRow != -1) {
                int problemId = (int) problemTableModel.getValueAt(selectedRow, 0);
                problemService.getProblemById(problemId).ifPresent(problem -> {
                    onEditProblem.accept(problemId, problem);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Please select a problem to edit.", "No Problem Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(editProblemButton);

        JButton deleteProblemButton = CodeTrackGUI.createStyledButton("🗑️ Delete Selected Problem");
        deleteProblemButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteProblemButton.setBackground(new Color(220, 20, 60));
        deleteProblemButton.setForeground(Color.WHITE);
        deleteProblemButton.addActionListener(e -> {
            int selectedRow = problemTable.getSelectedRow();
            if (selectedRow != -1) {
                int problemId = (int) problemTableModel.getValueAt(selectedRow, 0);
                String problemTitle = (String) problemTableModel.getValueAt(selectedRow, 1);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete problem '" + problemTitle + "' (ID: " + problemId + ")?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    onDeleteProblem.accept(problemId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a problem to delete.", "No Problem Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(deleteProblemButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void populateProblemTable() {
    SwingUtilities.invokeLater(() -> {
        problemTableModel.setRowCount(0);
        List<Problem> problems = problemService.getAllProblems(); // make sure this returns latest
        for (Problem p : problems) {
            problemTableModel.addRow(new Object[] { p.getId(), p.getTitle(), p.getDifficulty() });
        }
        problemTableModel.fireTableDataChanged();
    });
}
}
