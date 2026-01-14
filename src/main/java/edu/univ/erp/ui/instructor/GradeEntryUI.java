//package edu.univ.erp.ui.instructor;
//
//import edu.univ.erp.service.AssessmentService;
//import edu.univ.erp.domain.AssessmentComponent;
//import edu.univ.erp.domain.StudentScoreRow;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//public class GradeEntryUI extends JDialog {
//
//    private final int sectionId;
//    private final int courseId;
//
//    private final AssessmentService service = new AssessmentService();
//    private DefaultTableModel model;
//    private JTable table;
//
//    public GradeEntryUI(Window parent, int sectionId, int courseId) {
//        super(parent, "Enter Grades", ModalityType.APPLICATION_MODAL);
//        this.sectionId = sectionId;
//        this.courseId = courseId;
//
//        setLayout(new BorderLayout(10,10));
//        add(createTablePanel(), BorderLayout.CENTER);
//        add(createBottomPanel(), BorderLayout.SOUTH);
//
//        loadData();
//
//        setSize(950, 550);
//        setLocationRelativeTo(parent);
//    }
//
//    private JScrollPane createTablePanel() {
//        model = new DefaultTableModel() {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column > 1; // Student + Final not editable
//            }
//        };
//
//        table = new JTable(model);
//        table.setRowHeight(30);
//
//        return new JScrollPane(table);
//    }
//
//    private JPanel createBottomPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton saveBtn = new JButton("Save All Scores");
//
//        saveBtn.addActionListener(e -> saveScores());
//        panel.add(saveBtn);
//        return panel;
//    }
//
//    private void loadData() {
//        try {
//            List<AssessmentComponent> components = service.getComponentsForCourse(courseId);
//            List<StudentScoreRow> rows = service.getStudentsForSection(sectionId);
//
//            model.setRowCount(0);
//            model.setColumnCount(0);
//
//            model.addColumn("Enrollment ID");
//            model.addColumn("Student");
//
//            for (AssessmentComponent c : components) {
//                model.addColumn(c.getName() + " (/" + c.getMaxMarks() + ")");
//            }
//
//            model.addColumn("Final Grade");
//
//            for (StudentScoreRow r : rows) {
//
//                Object[] row = new Object[components.size() + 3];
//                int ci = 0;
//
//                row[ci++] = r.getEnrollmentId();
//                row[ci++] = r.getStudentName();
//
//                for (AssessmentComponent c : components) {
//                    Double sc = r.getScoreFor(c.getComponentId());
//                    row[ci++] = (sc == null ? "" : sc);
//                }
//
//                Double finalScore = service.getFinalScoreSafe(r.getEnrollmentId(), courseId);
//                row[ci] = (finalScore == null ? "" : finalScore);
//
//                model.addRow(row);
//            }
//
//            table.getColumnModel().getColumn(0).setMinWidth(0);
//            table.getColumnModel().getColumn(0).setMaxWidth(0);
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
//        }
//    }
//
//
//    private void saveScores() {
//        try {
//            List<AssessmentComponent> components = service.getComponentsForCourse(courseId);
//
//            for (int r = 0; r < model.getRowCount(); r++) {
//
//                int enrollmentId = Integer.parseInt(model.getValueAt(r, 0).toString());
//
//                int col = 2;
//                for (AssessmentComponent c : components) {
//
//                    Object val = model.getValueAt(r, col);
//
//                    if (val == null || val.toString().trim().isEmpty()) {
//                        col++;
//                        continue;
//                    }
//
//                    String rawStr = val.toString().trim();
//                    double rawScore;
//
//                    // -------------------
//                    // Validate numeric
//                    // -------------------
//                    try {
//                        rawScore = Double.parseDouble(rawStr);
//                    } catch (NumberFormatException ex) {
//                        JOptionPane.showMessageDialog(this,
//                                "Invalid score for student '" + model.getValueAt(r,1)
//                                        + "' for component '" + c.getName()
//                                        + "'. Enter a valid number.",
//                                "Invalid Input",
//                                JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    // -------------------
//                    // Validate range
//                    // -------------------
//                    if (rawScore < 0) {
//                        JOptionPane.showMessageDialog(this,
//                                "Score for component '" + c.getName()
//                                        + "' cannot be negative.",
//                                "Invalid Score",
//                                JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    if (rawScore > c.getMaxMarks()) {
//                        JOptionPane.showMessageDialog(this,
//                                "Score for component '" + c.getName()
//                                        + "' cannot exceed max marks (" + c.getMaxMarks() + ").",
//                                "Invalid Score",
//                                JOptionPane.ERROR_MESSAGE);
//                        return;
//                    }
//
//                    // -------------------
//                    // Save raw marks
//                    // -------------------
//                    service.saveSingleScore(enrollmentId, c.getComponentId(), rawScore);
//
//                    col++;
//                }
//
//                // Compute weighted final score
//                double finalScore = service.computeFinalGrade(enrollmentId, courseId);
//                model.setValueAt(finalScore, r, model.getColumnCount() - 1);
//            }
//
//            JOptionPane.showMessageDialog(this, "Scores saved successfully.");
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
//        }
//    }
//
//}


package edu.univ.erp.ui.instructor;

import edu.univ.erp.service.AssessmentService;
import edu.univ.erp.domain.AssessmentComponent;
import edu.univ.erp.domain.StudentScoreRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GradeEntryUI extends JDialog {

    private final int sectionId;
    private final int courseId;

    private final AssessmentService service = new AssessmentService();
    private DefaultTableModel model;
    private JTable table;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241); // Indigo
    private static final Color PRIMARY_HOVER = new Color(79, 82, 221);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94); // Green
    private static final Color SUCCESS_HOVER = new Color(22, 163, 74);
    private static final Color BACKGROUND = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TABLE_HEADER = new Color(243, 244, 246);
    private static final Color TABLE_HOVER = new Color(249, 250, 251);
    private static final Color EDITABLE_BG = new Color(255, 251, 235); // Light yellow tint
    private static final Color FINAL_GRADE_BG = new Color(240, 253, 244); // Light green tint

    public GradeEntryUI(Window parent, int sectionId, int courseId) {
        super(parent, "Enter Grades", ModalityType.APPLICATION_MODAL);
        this.sectionId = sectionId;
        this.courseId = courseId;

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadData();

        setSize(1100, 650);
        setLocationRelativeTo(parent);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel titleLabel = new JLabel("Grade Entry");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Enter scores for each assessment component");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 1; // Student + Final not editable
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(44);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setBackground(CARD_BACKGROUND);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Modern table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));

        // Custom cell renderer with visual cues for editable columns
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    // Student name column - white/light gray alternating
                    if (column == 1) {
                        c.setBackground(row % 2 == 0 ? CARD_BACKGROUND : TABLE_HOVER);
                    }
                    // Editable score columns - light yellow tint
                    else if (column > 1 && column < table.getColumnCount() - 1) {
                        c.setBackground(row % 2 == 0 ? EDITABLE_BG : new Color(255, 248, 220));
                    }
                    // Final grade column - light green tint
                    else if (column == table.getColumnCount() - 1) {
                        c.setBackground(row % 2 == 0 ? FINAL_GRADE_BG : new Color(220, 252, 231));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

                // Center align numeric columns
                if (column > 1) {
                    setHorizontalAlignment(JLabel.CENTER);
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                return c;
            }
        };

        // Apply renderer to all columns initially
        table.setDefaultRenderer(Object.class, customRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        // Add legend panel
        JPanel legendPanel = createLegendPanel();

        JPanel tableContainer = new JPanel(new BorderLayout(0, 12));
        tableContainer.setOpaque(false);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(legendPanel, BorderLayout.SOUTH);

        containerPanel.add(tableContainer, BorderLayout.CENTER);

        return scrollPane;
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        legendPanel.add(createLegendItem("Student Name", CARD_BACKGROUND));
        legendPanel.add(createLegendItem("Editable Scores", EDITABLE_BG));
        legendPanel.add(createLegendItem("Final Grade (Auto-calculated)", FINAL_GRADE_BG));

        return legendPanel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);

        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_SECONDARY);

        item.add(colorBox);
        item.add(label);

        return item;
    }

    private JPanel createBottomPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CARD_BACKGROUND);
        outerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        JButton saveBtn = createModernButton("Save All Scores", SUCCESS_COLOR, SUCCESS_HOVER);
        saveBtn.addActionListener(e -> saveScores());

        buttonPanel.add(saveBtn);

        outerPanel.add(buttonPanel, BorderLayout.EAST);

        return outerPanel;
    }

    private JButton createModernButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));

        return button;
    }

    private void loadData() {
        try {
            List<AssessmentComponent> components = service.getComponentsForCourse(courseId);
            List<StudentScoreRow> rows = service.getStudentsForSection(sectionId);

            model.setRowCount(0);
            model.setColumnCount(0);

            model.addColumn("Enrollment ID");
            model.addColumn("Student");

            for (AssessmentComponent c : components) {
                model.addColumn(c.getName() + " (/" + c.getMaxMarks() + ")");
            }

            model.addColumn("Final Grade");

            for (StudentScoreRow r : rows) {

                Object[] row = new Object[components.size() + 3];
                int ci = 0;

                row[ci++] = r.getEnrollmentId();
                row[ci++] = r.getStudentName();

                for (AssessmentComponent c : components) {
                    Double sc = r.getScoreFor(c.getComponentId());
                    row[ci++] = (sc == null ? "" : sc);
                }

                Double finalScore = service.getFinalScoreSafe(r.getEnrollmentId(), courseId);
                row[ci] = (finalScore == null ? "" : finalScore);

                model.addRow(row);
            }

            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);

        } catch (Exception e) {
            showModernError("Error loading data: " + e.getMessage());
        }
    }

    private void saveScores() {
        try {
            List<AssessmentComponent> components = service.getComponentsForCourse(courseId);

            for (int r = 0; r < model.getRowCount(); r++) {

                int enrollmentId = Integer.parseInt(model.getValueAt(r, 0).toString());

                int col = 2;
                for (AssessmentComponent c : components) {

                    Object val = model.getValueAt(r, col);

                    if (val == null || val.toString().trim().isEmpty()) {
                        col++;
                        continue;
                    }

                    String rawStr = val.toString().trim();
                    double rawScore;

                    // -------------------
                    // Validate numeric
                    // -------------------
                    try {
                        rawScore = Double.parseDouble(rawStr);
                    } catch (NumberFormatException ex) {
                        showModernError(
                                "Invalid score for student '" + model.getValueAt(r,1)
                                        + "' for component '" + c.getName()
                                        + "'.\n\nPlease enter a valid number.");
                        return;
                    }

                    // -------------------
                    // Validate range
                    // -------------------
                    if (rawScore < 0) {
                        showModernError(
                                "Score for component '" + c.getName()
                                        + "' cannot be negative.");
                        return;
                    }

                    if (rawScore > c.getMaxMarks()) {
                        showModernError(
                                "Score for component '" + c.getName()
                                        + "' cannot exceed max marks (" + c.getMaxMarks() + ").");
                        return;
                    }

                    // -------------------
                    // Save raw marks
                    // -------------------
                    service.saveSingleScore(enrollmentId, c.getComponentId(), rawScore);

                    col++;
                }

                // Compute weighted final score
                double finalScore = service.computeFinalGrade(enrollmentId, courseId);
                model.setValueAt(finalScore, r, model.getColumnCount() - 1);
            }

            showModernSuccess("All scores have been saved successfully!");

        } catch (Exception e) {
            showModernError("Error saving scores: " + e.getMessage());
        }
    }

    private void showModernError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showModernSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

}