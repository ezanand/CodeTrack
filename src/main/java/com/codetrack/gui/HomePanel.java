package com.codetrack.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel(Runnable onViewProblems, Runnable onViewSubmissions, Runnable onAddProblem) {
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 240, 255));

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(500, 500));
        cardPanel.setBackground(new Color(250, 250, 255));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 6, 6, new Color(210, 210, 240)), // Soft shadow
                new EmptyBorder(30, 30, 30, 30)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(15, 0, 15, 0);

        JLabel heading = new JLabel("CodeTrack", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 42));
        heading.setForeground(new Color(25, 25, 112));
        constraints.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(heading, constraints);

        JLabel tagline = new JLabel("Your Mini Coding Assessment Portal", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        tagline.setForeground(new Color(70, 130, 180));
        constraints.insets = new Insets(0, 0, 40, 0);
        cardPanel.add(tagline, constraints);

        JButton listProblemsBtn = CodeTrackGUI.createStyledButton("📋 List All Problems");
        listProblemsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        listProblemsBtn.addActionListener(e -> onViewProblems.run());
        cardPanel.add(listProblemsBtn, constraints);

        JButton viewSubmissionsBtn = CodeTrackGUI.createStyledButton("📑 View My Submissions");
        viewSubmissionsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        viewSubmissionsBtn.addActionListener(e -> onViewSubmissions.run());
        cardPanel.add(viewSubmissionsBtn, constraints);

        JButton addProblemBtn = CodeTrackGUI.createStyledButton("➕ Add New Problem (Manual)");
        addProblemBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        addProblemBtn.addActionListener(e -> onAddProblem.run());
        cardPanel.add(addProblemBtn, constraints);

        JButton exitBtn = CodeTrackGUI.createStyledButton("❌ Exit");
        exitBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        exitBtn.addActionListener(e -> System.exit(0));
        cardPanel.add(exitBtn, constraints);

        add(cardPanel);
    }
}
