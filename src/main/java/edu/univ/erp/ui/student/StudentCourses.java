//package edu.univ.erp.ui.student;
//
//import javax.swing.*;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.*;
//import java.time.LocalDate;   // ← REQUIRED IMPORT
//import edu.univ.erp.db.DBUtil;
//
///**
// * Panel for browsing and registering courses
// * Opens in a dialog window
// */
//public class StudentCourses extends JPanel {
//
//    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
//    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);
//    private static final Color BG_COLOR = Color.WHITE;
//    private static final Color TEXT_PRIMARY = new Color(38, 38, 35);
//    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
//    private static final Color BORDER_COLOR = new Color(229, 231, 235);
//
//    private JTable coursesTable;
//    private DefaultTableModel tableModel;
//    private JTextField searchField;
//    private int student_id;
//    private boolean maintenanceMode;
//
//    public StudentCourses(int student_id, boolean maintenanceMode) {
//        this.student_id = student_id;
//        this.maintenanceMode = maintenanceMode;
//        initializeUI();
//        loadCourses();
//    }
//
//    private void initializeUI() {
//        setLayout(new BorderLayout(10, 10));
//        setBackground(BG_COLOR);
//        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JPanel headerPanel = createHeaderPanel();
//        add(headerPanel, BorderLayout.NORTH);
//
//        JPanel tablePanel = createTablePanel();
//        add(tablePanel, BorderLayout.CENTER);
//
//        JPanel buttonPanel = createButtonPanel();
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    private JPanel createHeaderPanel() {
//        JPanel panel = new JPanel(new BorderLayout(10, 10));
//        panel.setBackground(BG_COLOR);
//
//        JLabel titleLabel = new JLabel("📚 Available Courses");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        titleLabel.setForeground(TEXT_PRIMARY);
//
//        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        searchPanel.setBackground(BG_COLOR);
//
//        JLabel searchLabel = new JLabel("Search:");
//        searchLabel.setForeground(TEXT_SECONDARY);
//
//        searchField = new JTextField(20);
//        searchField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyReleased(KeyEvent e) {
//                filterCourses();
//            }
//        });
//
//        searchPanel.add(searchLabel);
//        searchPanel.add(searchField);
//
//        panel.add(titleLabel, BorderLayout.WEST);
//        panel.add(searchPanel, BorderLayout.EAST);
//
//        return panel;
//    }
//
//    private String getSemester(int month) {
//        if (month >= 1 && month <= 5) return "Winter";
//        if (month >= 6 && month <= 7) return "Summer";
//        return "Monsoon"; // Aug–Dec
//    }
//
//    private JPanel createTablePanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(BG_COLOR);
//
//        String[] columns = {
//                "Course Code", "Course Title", "Credits", "Section",
//                "Instructor", "Day/Time", "Room",
//                "Enrolled/Capacity", "Status"
//        };
//
//        tableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int col) { return false; }
//        };
//
//        coursesTable = new JTable(tableModel);
//        coursesTable.setAutoCreateRowSorter(true);
//
//        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
//        coursesTable.setRowSorter(sorter);
//
//        styleTable(coursesTable);
//
//        JScrollPane scrollPane = new JScrollPane(coursesTable);
//        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
//
//        panel.add(scrollPane, BorderLayout.CENTER);
//
//        return panel;
//    }
//
//    private JPanel createButtonPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
//        panel.setBackground(BG_COLOR);
//
//        JButton registerBtn = new JButton("Register Selected");
//        registerBtn.setFont(new Font("Arial", Font.BOLD, 13));
//        registerBtn.setForeground(Color.WHITE);
//        registerBtn.setBackground(PRIMARY_COLOR);
//        registerBtn.setFocusPainted(false);
//        registerBtn.setBorderPainted(false);
//        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        registerBtn.setPreferredSize(new Dimension(150, 40));
//
//        if (maintenanceMode) {
//            registerBtn.setEnabled(false);
//            registerBtn.setBackground(Color.GRAY);
//        }
//
//        registerBtn.addActionListener(e -> registerForCourse());
//
//        JButton closeBtn = new JButton("Close");
//        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
//        closeBtn.setForeground(TEXT_PRIMARY);
//        closeBtn.setBackground(Color.LIGHT_GRAY);
//        closeBtn.setFocusPainted(false);
//        closeBtn.setBorderPainted(false);
//        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        closeBtn.setPreferredSize(new Dimension(100, 40));
//
//        closeBtn.addActionListener(e -> {
//            Window window = SwingUtilities.getWindowAncestor(this);
//            if (window != null) window.dispose();
//        });
//
//        panel.add(registerBtn);
//        panel.add(closeBtn);
//
//        return panel;
//    }
//
//    private void styleTable(JTable table) {
//        table.setFont(new Font("Arial", Font.PLAIN, 12));
//        table.setRowHeight(35);
//        table.setShowGrid(true);
//        table.setGridColor(BORDER_COLOR);
//        table.setSelectionBackground(new Color(239, 246, 255));
//        table.setSelectionForeground(TEXT_PRIMARY);
//        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Arial", Font.BOLD, 12));
//        header.setBackground(new Color(249, 250, 251));
//        header.setForeground(TEXT_PRIMARY);
//        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
//    }
//
//    private void loadCourses() {
//        tableModel.setRowCount(0);
//
//        LocalDate today = LocalDate.now();
//        String currentSemester = getSemester(today.getMonthValue());
//
//        String query =
//                "SELECT c.code, c.title, c.credits, s.section_id, " +
//                        "CONCAT(s.day, ' ', s.time) AS schedule, s.room, s.capacity, " +
//                        "COUNT(e.enrollment_id) AS enrolled, " +
//                        "u.username AS instructor_name, " +
//                        "CASE " +
//                        "   WHEN EXISTS (SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = s.section_id AND status='enrolled') THEN 'Already Enrolled' " +
//                        "   WHEN COUNT(e.enrollment_id) >= s.capacity THEN 'Full' " +
//                        "   ELSE 'Available' " +
//                        "END AS status " +
//                        "FROM courses c " +
//                        "JOIN sections s ON c.course_id = s.course_id " +
//                        "JOIN instructors i ON s.instructor_id = i.instructor_id " +
//                        "JOIN auth_db.users u ON i.user_id = u.user_id " +
//                        "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status='enrolled' " +
//                        "WHERE s.semester = ? " +
//                        "GROUP BY s.section_id " +
//                        "ORDER BY c.code, s.day";
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, student_id);
//            stmt.setString(2, currentSemester);
//
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                tableModel.addRow(new Object[]{
//                        rs.getString("code"),
//                        rs.getString("title"),
//                        rs.getInt("credits"),
//                        rs.getInt("section_id"),
//                        rs.getString("instructor_name"),
//                        rs.getString("schedule"),
//                        rs.getString("room"),
//                        rs.getInt("enrolled") + "/" + rs.getInt("capacity"),
//                        rs.getString("status")
//                });
//            }
//
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this,
//                    "Error loading courses: " + e.getMessage(),
//                    "Database Error",
//                    JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void filterCourses() {
//        String search = searchField.getText().trim();
//        TableRowSorter<?> sorter = (TableRowSorter<?>) coursesTable.getRowSorter();
//
//        if (search.isEmpty()) {
//            sorter.setRowFilter(null);
//        } else {
//            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
//        }
//    }
//
//    private void registerForCourse() {
//        int selectedRow = coursesTable.getSelectedRow();
//
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this,
//                    "Please select a course to register",
//                    "No Selection",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int modelRow = coursesTable.convertRowIndexToModel(selectedRow);
//
//        int sectionId = (int) tableModel.getValueAt(modelRow, 3);
//        String courseCode = (String) tableModel.getValueAt(modelRow, 0);
//        String courseTitle = (String) tableModel.getValueAt(modelRow, 1);
//        String status = (String) tableModel.getValueAt(modelRow, 8);
//
//        if (!status.equals("Available")) {
//            JOptionPane.showMessageDialog(this,
//                    "Cannot register: " + status,
//                    "Registration Failed",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(
//                this,
//                "Register for " + courseCode + " - " + courseTitle + "?",
//                "Confirm Registration",
//                JOptionPane.YES_NO_OPTION);
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        String insertQuery =
//                "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
//
//            stmt.setInt(1, student_id);
//            stmt.setInt(2, sectionId);
//
//            stmt.executeUpdate();
//
//            JOptionPane.showMessageDialog(this,
//                    "Successfully registered for " + courseCode,
//                    "Success",
//                    JOptionPane.INFORMATION_MESSAGE);
//
//            loadCourses();
//
//        } catch (SQLException e) {
//            if (e.getMessage().contains("Duplicate entry")) {
//                JOptionPane.showMessageDialog(this,
//                        "You are already registered for this course",
//                        "Duplicate Registration",
//                        JOptionPane.WARNING_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "Registration failed: " + e.getMessage(),
//                        "Database Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        }
//    }
//
//    public static void showDialog(Component parent, int student_id, boolean maintenanceMode) {
//        JDialog dialog = new JDialog(
//                SwingUtilities.getWindowAncestor(parent),
//                "Browse & Register Courses",
//                Dialog.ModalityType.APPLICATION_MODAL);
//
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//
//        StudentCourses panel = new StudentCourses(student_id, maintenanceMode);
//        dialog.add(panel);
//
//        dialog.setSize(1000, 600);
//        dialog.setLocationRelativeTo(parent);
//        dialog.setVisible(true);
//    }
//}




package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import edu.univ.erp.db.DBUtil;

public class StudentCourses extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(38, 38, 35);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private int student_id;
    private boolean maintenanceMode;

    public StudentCourses(int student_id, boolean maintenanceMode) {
        this.student_id = student_id;
        this.maintenanceMode = maintenanceMode;
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);

        JLabel titleLabel = new JLabel("📚 Available Courses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(BG_COLOR);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(TEXT_SECONDARY);

        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCourses();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private String getSemester(int month) {
        if (month >= 1 && month <= 5) return "Winter";
        if (month >= 6 && month <= 7) return "Summer";
        return "Monsoon";
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        String[] columns = {
                "Course Code", "Course Title", "Credits", "Section",
                "Instructor", "Day/Time", "Room",
                "Enrolled/Capacity", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setAutoCreateRowSorter(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        coursesTable.setRowSorter(sorter);

        styleTable(coursesTable);

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(BG_COLOR);

        JButton registerBtn = new JButton("Register Selected");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 13));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBackground(PRIMARY_COLOR);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setPreferredSize(new Dimension(150, 40));

        if (maintenanceMode) {
            registerBtn.setEnabled(false);
            registerBtn.setBackground(Color.GRAY);
        }

        registerBtn.addActionListener(e -> registerForCourse());

        JButton dropBtn = new JButton("Drop Selected");
        dropBtn.setFont(new Font("Arial", Font.BOLD, 13));
        dropBtn.setForeground(Color.WHITE);
        dropBtn.setBackground(new Color(220, 38, 38));
        dropBtn.setFocusPainted(false);
        dropBtn.setBorderPainted(false);
        dropBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dropBtn.setPreferredSize(new Dimension(150, 40));
        dropBtn.addActionListener(e -> dropCourse());

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setForeground(TEXT_PRIMARY);
        closeBtn.setBackground(Color.LIGHT_GRAY);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });

        panel.add(registerBtn);
        panel.add(dropBtn);
        panel.add(closeBtn);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
    }

    private void loadCourses() {
        tableModel.setRowCount(0);

        LocalDate today = LocalDate.now();
        String currentSemester = getSemester(today.getMonthValue());

        String query =
                "SELECT c.code, c.title, c.credits, s.section_id, " +
                        "CONCAT(s.day, ' ', s.time) AS schedule, s.room, s.capacity, " +
                        "COUNT(e.enrollment_id) AS enrolled, " +
                        "u.username AS instructor_name, " +
                        "CASE " +
                        "   WHEN EXISTS (SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = s.section_id AND status='enrolled') THEN 'Already Enrolled' " +
                        "   WHEN COUNT(e.enrollment_id) >= s.capacity THEN 'Full' " +
                        "   ELSE 'Available' " +
                        "END AS status " +
                        "FROM courses c " +
                        "JOIN sections s ON c.course_id = s.course_id " +
                        "JOIN instructors i ON s.instructor_id = i.instructor_id " +
                        "JOIN auth_db.users u ON i.user_id = u.user_id " +
                        "LEFT JOIN enrollments e ON s.section_id = e.section_id AND e.status='enrolled' " +
                        "WHERE s.semester = ? " +
                        "GROUP BY s.section_id " +
                        "ORDER BY c.code, s.day";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, student_id);
            stmt.setString(2, currentSemester);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getInt("section_id"),
                        rs.getString("instructor_name"),
                        rs.getString("schedule"),
                        rs.getString("room"),
                        rs.getInt("enrolled") + "/" + rs.getInt("capacity"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterCourses() {
        String search = searchField.getText().trim();
        TableRowSorter<?> sorter = (TableRowSorter<?>) coursesTable.getRowSorter();

        if (search.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
        }
    }

    private void registerForCourse() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to register",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = coursesTable.convertRowIndexToModel(selectedRow);

        int sectionId = (int) tableModel.getValueAt(modelRow, 3);
        String courseCode = (String) tableModel.getValueAt(modelRow, 0);
        String courseTitle = (String) tableModel.getValueAt(modelRow, 1);
        String status = (String) tableModel.getValueAt(modelRow, 8);

        if (!status.equals("Available")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot register: " + status,
                    "Registration Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Register for " + courseCode + " - " + courseTitle + "?",
                "Confirm Registration",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String insertQuery =
                "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setInt(1, student_id);
            stmt.setInt(2, sectionId);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Successfully registered for " + courseCode,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadCourses();

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this,
                        "You are already registered for this course",
                        "Duplicate Registration",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dropCourse() {
        int selectedRow = coursesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to drop",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = coursesTable.convertRowIndexToModel(selectedRow);

        int sectionId = (int) tableModel.getValueAt(modelRow, 3);
        String courseCode = (String) tableModel.getValueAt(modelRow, 0);
        String courseTitle = (String) tableModel.getValueAt(modelRow, 1);
        String status = (String) tableModel.getValueAt(modelRow, 8);

        if (!status.equals("Already Enrolled")) {
            JOptionPane.showMessageDialog(this,
                    "You cannot drop this course because you are not enrolled.",
                    "Drop Failed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Drop " + courseCode + " - " + courseTitle + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String dropQuery =
                "UPDATE enrollments SET status='dropped' WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(dropQuery)) {

            stmt.setInt(1, student_id);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Successfully dropped " + courseCode,
                    "Course Dropped",
                    JOptionPane.INFORMATION_MESSAGE);

            loadCourses();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Drop failed: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showDialog(Component parent, int student_id, boolean maintenanceMode) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "Browse & Register Courses",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        StudentCourses panel = new StudentCourses(student_id, maintenanceMode);
        dialog.add(panel);

        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
