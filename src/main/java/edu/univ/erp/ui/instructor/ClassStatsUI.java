package edu.univ.erp.ui.instructor;

import edu.univ.erp.service.AssessmentService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClassStatsUI extends JDialog {

    private final int sectionId;
    private final int courseId;

    private final AssessmentService service = new AssessmentService();

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241); // Indigo
    private static final Color SECONDARY_COLOR = new Color(251, 146, 60); // Orange
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94); // Green
    private static final Color DANGER_COLOR = new Color(239, 68, 68); // Red
    private static final Color BACKGROUND = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public ClassStatsUI(Window parent, int sectionId, int courseId) {
        super(parent, "Class Statistics", ModalityType.APPLICATION_MODAL);
        this.sectionId = sectionId;
        this.courseId = courseId;

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createStatsPanel(), BorderLayout.CENTER);

        setSize(700, 650);
        setLocationRelativeTo(parent);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel titleLabel = new JLabel("Class Statistics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Performance overview and grade distribution");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        try {
            double avg = service.getClassAverage(sectionId);
            double max = service.getClassMax(sectionId);
            double min = service.getClassMin(sectionId);
            int count = service.getStudentCount(sectionId);

            // Stats cards at the top
            JPanel statsCardsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
            statsCardsPanel.setBackground(BACKGROUND);

            statsCardsPanel.add(createStatCard("Students", String.valueOf(count), PRIMARY_COLOR));
            statsCardsPanel.add(createStatCard("Average", String.format("%.2f", avg), SUCCESS_COLOR));
            statsCardsPanel.add(createStatCard("Highest", String.format("%.2f", max), SECONDARY_COLOR));
            statsCardsPanel.add(createStatCard("Lowest", String.format("%.2f", min), DANGER_COLOR));

            panel.add(statsCardsPanel, BorderLayout.NORTH);

            // Histogram panel
            List<Double> scores = service.getScores(sectionId);
            JPanel histogramContainer = createHistogramContainer(scores);
            panel.add(histogramContainer, BorderLayout.CENTER);

        } catch (Exception e) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.setBackground(CARD_BACKGROUND);
            errorPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER_COLOR, 2),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JLabel errorLabel = new JLabel("Error loading statistics: " + e.getMessage());
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(DANGER_COLOR);

            errorPanel.add(errorLabel, BorderLayout.CENTER);
            panel.add(errorPanel, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 16, 20, 16)
        ));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(TEXT_SECONDARY);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueText.setForeground(accentColor);
        valueText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(labelText);
        card.add(Box.createVerticalStrut(8));
        card.add(valueText);

        return card;
    }

    private JPanel createHistogramContainer(List<Double> scores) {
        JPanel container = new JPanel(new BorderLayout(0, 12));
        container.setBackground(CARD_BACKGROUND);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Grade Distribution");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);

        ScoreHistogramPanel histogram = new ScoreHistogramPanel(scores);
        histogram.setPreferredSize(new Dimension(600, 300));
        histogram.setBackground(CARD_BACKGROUND);

        container.add(titleLabel, BorderLayout.NORTH);
        container.add(histogram, BorderLayout.CENTER);

        return container;
    }
}