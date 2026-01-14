package edu.univ.erp.ui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.AdminSystemStats;
import edu.univ.erp.ui.common.MainFrame;

public class CourseManagement extends JPanel {

    private static final Color mainTeal = new Color(0, 128, 128);
    private static final Color lightGrey = new Color(249, 250, 251);
    private static final Color darkBlue = new Color(17, 24, 39);
    private static final Color greenBright = new Color(16, 185, 129);
    private static final Color orangeBright = new Color(245, 158, 11);

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnBack, btnSections;
    private AdminSystemStats stats;

    public CourseManagement() {
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(lightGrey);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(mainTeal);

        JLabel subtitleLabel = new JLabel("View and manage all courses in the system");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(lightGrey);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        tablePanel.setBackground(Color.WHITE);

        // Create table
        String[] columnNames = {"Course ID", "Course Title", "Sections"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Arial", Font.PLAIN, 13));
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        courseTable.getTableHeader().setBackground(mainTeal);
        courseTable.getTableHeader().setForeground(Color.WHITE);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(400);
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(lightGrey);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnAdd = createButton("Add Course", greenBright);
        btnEdit = createButton("Edit Course", mainTeal);
        btnDelete = createButton("Delete Course", orangeBright);
//        btnRefresh = createButton("Refresh", darkBlue);
        btnSections = createButton("Manage Sections", new Color(100, 149, 237));

        // Button actions
        btnAdd.addActionListener(e -> addCourse());
        btnEdit.addActionListener(e -> editCourse());
        btnDelete.addActionListener(e -> deleteCourse());
//        btnRefresh.addActionListener(e -> loadCourses());
        btnSections.addActionListener(e -> manageSections());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSections);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 36));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadCourses() {
        // Clear existing rows
        tableModel.setRowCount(0);

        String query = "SELECT c.course_id, c.title, " +
                "(SELECT COUNT(*) FROM sections s WHERE s.course_id = c.course_id) as section_count " +
                "FROM courses c ORDER BY c.course_id";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // Make sure we're using erp_db
            stmt.execute("USE erp_db");

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getInt("section_count")
                };
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addCourse() {
        JTextField titleField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Course Title:"));
        panel.add(titleField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Course title cannot be empty!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                // Use erp_db
                conn.createStatement().execute("USE erp_db");

                // Generate a simple code from title
                String code = title.substring(0, Math.min(6, title.length())).toUpperCase().replaceAll("\\s+", "");

                pstmt.setString(1, code);
                pstmt.setString(2, title);
                pstmt.setInt(3, 3); // Default credits
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Course added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadCourses(); // Refresh table

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error adding course: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to edit!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentTitle = (String) tableModel.getValueAt(selectedRow, 1);

        JTextField titleField = new JTextField(currentTitle, 20);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Course ID:"));
        panel.add(new JLabel(String.valueOf(courseId)));
        panel.add(new JLabel("Course Title:"));
        panel.add(titleField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();

            if (newTitle.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Course title cannot be empty!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "UPDATE courses SET title = ? WHERE course_id = ?";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                // Use erp_db
                conn.createStatement().execute("USE erp_db");

                pstmt.setString(1, newTitle);
                pstmt.setInt(2, courseId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Course updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadCourses(); // Refresh table

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating course: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseTitle = (String) tableModel.getValueAt(selectedRow, 1);
        int sectionCount = (int) tableModel.getValueAt(selectedRow, 2);

        if (sectionCount > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete course with existing sections!\n" +
                            "Please delete all sections first.",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete:\n" + courseTitle + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM courses WHERE course_id = ?";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                // Use erp_db
                conn.createStatement().execute("USE erp_db");

                pstmt.setInt(1, courseId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Course deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadCourses(); // Refresh table

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting course: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void manageSections() {

        int row = courseTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = (int) tableModel.getValueAt(row, 0);
        String courseTitle = (String) tableModel.getValueAt(row, 1);

        ManageSectionDialog dlg =
                new ManageSectionDialog(SwingUtilities.getWindowAncestor(this), courseId, courseTitle);

        dlg.setVisible(true);

        // refresh section count after changes
        loadCourses();
    }


    public static void showDialog(Component parent) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "User Management",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        CourseManagement panel = new CourseManagement();

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

}