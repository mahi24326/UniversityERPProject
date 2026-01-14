package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.StudentAssessmentRow;
import edu.univ.erp.service.StudentGradeService;


public class StudentGrades extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(38, 38, 35);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private final int student_id;
    private JPanel coursesPanel;

    public StudentGrades(int student_id) {
        this.student_id = student_id;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("📊 My Grades & Performance");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(headerPanel, BorderLayout.NORTH);

        // Scrollable courses panel
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(BG_COLOR);

        loadGrades();

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_COLOR);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setBackground(Color.LIGHT_GRAY);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadGrades() {
        coursesPanel.removeAll();

        String query = "SELECT c.course_id, c.code, c.title, c.credits, e.enrollment_id, " +
                "u.username as instructor " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN instructors i ON s.instructor_id = i.instructor_id " +
                "JOIN auth_db.users u ON i.user_id = u.user_id " +
                "WHERE e.student_id = ? AND e.status IN ('enrolled', 'completed') " +
                "ORDER BY c.code";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, student_id);
            ResultSet rs = stmt.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int courseId = rs.getInt("course_id");
                String code = rs.getString("code");
                String title = rs.getString("title");
                int credits = rs.getInt("credits");
                int enrollmentId = rs.getInt("enrollment_id");
                String instructor = rs.getString("instructor");

                JPanel courseCard = createCourseCard(code, title, credits, instructor, enrollmentId, courseId);
                coursesPanel.add(courseCard);
                coursesPanel.add(Box.createVerticalStrut(15));
            }

            if (!hasData) {
                JLabel noDataLabel = new JLabel("No enrolled courses found");
                noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                noDataLabel.setForeground(TEXT_SECONDARY);
                coursesPanel.add(noDataLabel);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading grades: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private JPanel createCourseCard(String code, String title, int credits, String instructor, int enrollmentId, int courseId) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(BG_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);

        JLabel courseLabel = new JLabel(code + " - " + title);
        courseLabel.setFont(new Font("Arial", Font.BOLD, 16));
        courseLabel.setForeground(TEXT_PRIMARY);

        JLabel creditsLabel = new JLabel(credits + " Credits • " + instructor);
        creditsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        creditsLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(BG_COLOR);
        titlePanel.add(courseLabel);
        titlePanel.add(creditsLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Grades table
        JPanel gradesPanel = createGradesTable(enrollmentId, courseId);
        card.add(gradesPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createGradesTable(int enrollmentId, int courseId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        String[] columns = {"Assessment", "Score", "Weight %", "Contribution"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        StudentGradeService service = new StudentGradeService();

        try {
            List<StudentAssessmentRow> list = service.getAssessmentRows(enrollmentId, courseId);
            Double finalScore = service.getFinalScore(enrollmentId);

            boolean hasRows = false;

            for (StudentAssessmentRow r : list) {
                hasRows = true;

                model.addRow(new Object[] {
                        r.getComponent(),
                        r.getScore() == null ? "Not Graded" : String.format("%.2f", r.getScore()),
                        r.getWeight() + "%",
                        String.valueOf(r.getContribution())   // FIXED
                });

            }

            if (!hasRows) {
                model.addRow(new Object[] {"No assessments defined", "-", "-", "-"});
            }

            // Add final grade row
            model.addRow(new Object[] {
                    "Final Score",
                    finalScore == null ? "Not Computed" : String.format("%.2f", finalScore),
                    "-",
                    "-"
            });

        } catch (Exception e) {
            model.addRow(new Object[]{"Error loading grades", "-", "-", "-"});
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        styleGradesTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setPreferredSize(new Dimension(0, 120));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    private void styleGradesTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 11));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_PRIMARY);
    }


    public static void showDialog(Component parent, int student_id) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "My Grades", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        StudentGrades panel = new StudentGrades(student_id);
        dialog.add(panel);

        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
