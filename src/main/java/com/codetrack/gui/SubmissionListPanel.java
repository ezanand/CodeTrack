package com.codetrack.gui;

import com.codetrack.model.Submission;
import com.codetrack.service.SubmissionService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SubmissionListPanel extends JPanel {

    private final SubmissionService submissionService;
    private final Runnable onBackToHome;

    private JTable submissionTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SubmissionListPanel(SubmissionService submissionService, Runnable onBackToHome) {
        this.submissionService = submissionService;
        this.onBackToHome = onBackToHome;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 248, 255));

        // Heading Label
        JLabel heading = new JLabel("📄 Your Submissions", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        heading.setForeground(new Color(25, 25, 112));
        heading.setOpaque(true);
        heading.setBackground(new Color(230, 240, 255));
        heading.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 250), 1, true),
                new EmptyBorder(12, 20, 12, 20)));
        add(heading, BorderLayout.NORTH);

        // Table setup
        String[] columns = { "ID", "Problem ID", "Status", "Exec Time (ms)", "Submission Time" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        submissionTable = new JTable(tableModel);
        submissionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        submissionTable.setRowHeight(28);
        submissionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        submissionTable.getTableHeader().setBackground(new Color(70, 130, 180));
        submissionTable.getTableHeader().setForeground(Color.WHITE);
        submissionTable.setGridColor(new Color(220, 220, 255));
        submissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(submissionTable);
        scrollPane.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 250), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        scrollPane.setBackground(new Color(250, 250, 255));
        add(scrollPane, BorderLayout.CENTER);

        // Footer Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        footer.setBackground(new Color(240, 248, 255));
        footer.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 200, 250), 1, true),
                new EmptyBorder(8, 10, 8, 10)));

        JButton backBtn = CodeTrackGUI.createStyledButton("← Back to Home");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.addActionListener(e -> onBackToHome.run());
        footer.add(backBtn);

        add(footer, BorderLayout.SOUTH);
    }

    public void populateSubmissionTable() {
        tableModel.setRowCount(0);
        List<Submission> submissions = submissionService.getAllSubmissions();
        for (Submission sub : submissions) {
            tableModel.addRow(new Object[] {
                    sub.getId(),
                    sub.getProblemId(),
                    sub.getStatus(),
                    sub.getExecutionTimeMs(),
                    sub.getSubmissionTime().format(FORMATTER)
            });
        }
    }
}
