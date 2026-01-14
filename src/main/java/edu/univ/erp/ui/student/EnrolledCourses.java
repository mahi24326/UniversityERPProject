//package edu.univ.erp.ui.student;
//
//import javax.swing.*;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.sql.*;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import edu.univ.erp.db.DBUtil;
//import edu.univ.erp.util.DateTimeUtil;
//import edu.univ.erp.service.EnrollmentService;
//
///**
// * Panel for viewing all enrolled courses with detailed information
// * Uses global drop period: deadline = sections.semester_start + drop_period_days (from settings)
// *
// * Requirements:
// * - DBUtil.getERPConnection() : Connection
// * - DBUtil.getSetting(String) : String  (returns null if not present)
// * - DateTimeUtil.calculateDropDeadline(LocalDate, int)
// * - DateTimeUtil.isBeforeDeadline(LocalDateTime)
// * - DateTimeUtil.format(LocalDateTime)
// * - EnrollmentService.dropCourse(int studentId, String courseCode)
// */
//public class EnrolledCourses extends JPanel {
//
//    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
//    private static final Color DANGER_COLOR = new Color(239, 68, 68);
//    private static final Color BG_COLOR = Color.WHITE;
//    private static final Color TEXT_PRIMARY = new Color(38, 38, 35);
//    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
//    private static final Color BORDER_COLOR = new Color(229, 231, 235);
//
//    private JTable coursesTable;
//    private DefaultTableModel tableModel;
//    private int studentId;
//    private boolean maintenanceMode;
//
//    public EnrolledCourses(int studentId, boolean maintenanceMode) {
//        this.studentId = studentId;
//        this.maintenanceMode = maintenanceMode;
//        initializeUI();
//        loadEnrolledCourses();
//    }
//
//    /* ---------------------- UI Initialization ---------------------- */
//
//    private void initializeUI() {
//        setLayout(new BorderLayout(10, 10));
//        setBackground(BG_COLOR);
//        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        // Header
//        JPanel headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(BG_COLOR);
//
//        JLabel titleLabel = new JLabel("📚 My Enrolled Courses");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        titleLabel.setForeground(TEXT_PRIMARY);
//
//        headerPanel.add(titleLabel, BorderLayout.WEST);
//        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
//
//        add(headerPanel, BorderLayout.NORTH);
//
//        // Table
//        JPanel tablePanel = createTablePanel();
//        add(tablePanel, BorderLayout.CENTER);
//
//        // Button panel
//        JPanel buttonPanel = createButtonPanel();
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    private JPanel createTablePanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(BG_COLOR);
//
//        String[] columns = {"Course Code", "Course Title", "Credits", "Section", "Instructor",
//                "Day", "Time", "Room", "Status", "Drop", "Drop Deadline"};
//        tableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 9; // Only Drop button column editable
//            }
//
//            @Override
//            public Class<?> getColumnClass(int column) {
//                return column == 9 ? JButton.class : String.class;
//            }
//        };
//
//        coursesTable = new JTable(tableModel);
//        styleTable(coursesTable);
//
//        // Add button renderer and editor for Drop column
//        coursesTable.getColumnModel().getColumn(9).setCellRenderer(new ButtonRenderer());
//        coursesTable.getColumnModel().getColumn(9).setCellEditor(new ButtonEditor(new JCheckBox()));
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
//        JButton refreshBtn = new JButton("Refresh");
//        refreshBtn.setFont(new Font("Arial", Font.BOLD, 13));
//        refreshBtn.setBackground(PRIMARY_COLOR);
//        refreshBtn.setForeground(Color.WHITE);
//        refreshBtn.setFocusPainted(false);
//        refreshBtn.setBorderPainted(false);
//        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        refreshBtn.setPreferredSize(new Dimension(100, 40));
//        refreshBtn.addActionListener(e -> loadEnrolledCourses());
//
//        JButton closeBtn = new JButton("Close");
//        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
//        closeBtn.setBackground(Color.LIGHT_GRAY);
//        closeBtn.setFocusPainted(false);
//        closeBtn.setBorderPainted(false);
//        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        closeBtn.setPreferredSize(new Dimension(100, 40));
//        closeBtn.addActionListener(e -> {
//            Window window = SwingUtilities.getWindowAncestor(this);
//            if (window != null) window.dispose();
//        });
//
//        panel.add(refreshBtn);
//        panel.add(closeBtn);
//
//        return panel;
//    }
//
//    private void styleTable(JTable table) {
//        table.setFont(new Font("Arial", Font.PLAIN, 12));
//        table.setRowHeight(40);
//        table.setShowGrid(true);
//        table.setGridColor(BORDER_COLOR);
//        table.setSelectionBackground(new Color(239, 246, 255));
//        table.setSelectionForeground(TEXT_PRIMARY);
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Arial", Font.BOLD, 12));
//        header.setBackground(new Color(249, 250, 251));
//        header.setForeground(TEXT_PRIMARY);
//        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
//
//        // Column widths (sensible defaults)
//        TableColumnModel cm = table.getColumnModel();
//        cm.getColumn(0).setPreferredWidth(100);
//        cm.getColumn(1).setPreferredWidth(220);
//        cm.getColumn(2).setPreferredWidth(60);
//        cm.getColumn(3).setPreferredWidth(60);
//        cm.getColumn(4).setPreferredWidth(150);
//        cm.getColumn(5).setPreferredWidth(80);
//        cm.getColumn(6).setPreferredWidth(100);
//        cm.getColumn(7).setPreferredWidth(80);
//        cm.getColumn(8).setPreferredWidth(80);
//        cm.getColumn(9).setPreferredWidth(80);
//        cm.getColumn(10).setPreferredWidth(140);
//    }
//
//    /* ---------------------- Data Loading ---------------------- */
//
//    /**
//     * Load enrolled courses, compute drop deadline per-section using semester_start + drop_period_days.
//     */
//    private void loadEnrolledCourses() {
//        tableModel.setRowCount(0);
//
//        // Query pulls semester_start; deadline computed client-side using global drop period
//        String query = "SELECT e.enrollment_id, c.code, c.title, c.credits, " +
//                "s.section_id, s.semester AS section_semester, s.year AS section_year, s.semester_start, s.day, s.time, s.room, e.status, " +
//                "u.username as instructor " +
//                "FROM enrollments e " +
//                "JOIN sections s ON e.section_id = s.section_id " +
//                "JOIN courses c ON s.course_id = c.course_id " +
//                "JOIN instructors i ON s.instructor_id = i.instructor_id " +
//                "JOIN auth_db.users u ON i.user_id = u.user_id " +
//                "WHERE e.student_id = ? " +
//                "ORDER BY c.code";
//
//        int dropPeriodDays = readIntSettingOrDefault("drop_period_days", 14);
//        boolean dropEnabled = Boolean.parseBoolean(readSettingOrDefault("drop_enabled", "true"));
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, studentId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Date semStartDate = rs.getDate("semester_start");
//                LocalDate semesterStart = (semStartDate != null) ? semStartDate.toLocalDate() : null;
//                LocalDateTime deadline = (semesterStart != null)
//                        ? DateTimeUtil.calculateDropDeadline(semesterStart, dropPeriodDays)
//                        : null;
//
//                String status = rs.getString("status");
//                boolean canDrop = dropEnabled
//                        && "enrolled".equalsIgnoreCase(status)
//                        && deadline != null
//                        && DateTimeUtil.isBeforeDeadline(deadline)
//                        && !maintenanceMode;
//
//                String sectionLabel = rs.getString("section_semester") + " " + rs.getInt("section_year");
//
//                Object[] row = {
//                        rs.getString("code"),
//                        rs.getString("title"),
//                        String.valueOf(rs.getInt("credits")),
//                        sectionLabel,
//                        rs.getString("instructor"),
//                        rs.getString("day"),
//                        rs.getString("time"),
//                        rs.getString("room"),
//                        status,
//                        canDrop ? "Drop" : "N/A",
//                        deadline != null ? DateTimeUtil.format(deadline) : "N/A"
//                };
//                tableModel.addRow(row);
//            }
//
//            if (tableModel.getRowCount() == 0) {
//                JOptionPane.showMessageDialog(this,
//                        "No enrolled courses found",
//                        "Information",
//                        JOptionPane.INFORMATION_MESSAGE);
//            }
//
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this,
//                    "Error loading courses: " + e.getMessage(),
//                    "Database Error",
//                    JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    /* ---------------------- Drop action ---------------------- */
//
//    private void dropCourse(int row) {
//        if (maintenanceMode) {
//            JOptionPane.showMessageDialog(this,
//                    "Cannot drop courses during maintenance mode",
//                    "Maintenance Mode",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        String courseCode = (String) tableModel.getValueAt(row, 0);
//        String courseTitle = (String) tableModel.getValueAt(row, 1);
//        String status = (String) tableModel.getValueAt(row, 8);
//
//        if (!"enrolled".equalsIgnoreCase(status)) {
//            JOptionPane.showMessageDialog(this,
//                    "Only enrolled courses can be dropped",
//                    "Cannot Drop",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                "Are you sure you want to drop " + courseCode + " - " + courseTitle + "?",
//                "Confirm Drop",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE);
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        try {
//            boolean success = EnrollmentService.dropCourse(studentId, courseCode);
//            if (success) {
//                JOptionPane.showMessageDialog(this,
//                        "Successfully dropped " + courseCode,
//                        "Success",
//                        JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "Cannot drop course. Deadline may have passed or course not found.",
//                        "Drop Failed",
//                        JOptionPane.WARNING_MESSAGE);
//            }
//            loadEnrolledCourses(); // Refresh table
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this,
//                    "Database error: " + e.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
//        }
//    }
//
//    /* ---------------------- Renderers / Editors ---------------------- */
//
//    // Button renderer for table
//    class ButtonRenderer extends JButton implements TableCellRenderer {
//        public ButtonRenderer() { setOpaque(true); }
//
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                                                       boolean isSelected, boolean hasFocus, int row, int column) {
//            String label = (value == null) ? "Drop" : value.toString();
//            setText(label);
//
//            if ("N/A".equals(label)) {
//                setBackground(Color.LIGHT_GRAY);
//                setForeground(Color.DARK_GRAY);
//                setEnabled(false);
//            } else {
//                setBackground(DANGER_COLOR);
//                setForeground(Color.WHITE);
//                setEnabled(true);
//            }
//
//            setFont(new Font("Arial", Font.BOLD, 11));
//            setFocusPainted(false);
//            setBorderPainted(false);
//            return this;
//        }
//    }
//
//    // Button editor for table
//    class ButtonEditor extends DefaultCellEditor {
//        private JButton button;
//        private String label;
//        private boolean isPushed;
//        private int editingRow;
//
//        public ButtonEditor(JCheckBox checkBox) {
//            super(checkBox);
//            button = new JButton();
//            button.setOpaque(true);
//            button.setFont(new Font("Arial", Font.BOLD, 11));
//            button.setFocusPainted(false);
//            button.setBorderPainted(false);
//
//            button.addActionListener(e -> fireEditingStopped());
//        }
//
//        public Component getTableCellEditorComponent(JTable table, Object value,
//                                                     boolean isSelected, int row, int column) {
//            label = (value == null) ? "Drop" : value.toString();
//            button.setText(label);
//            button.setEnabled(!"N/A".equals(label));
//            button.setBackground(!"N/A".equals(label) ? DANGER_COLOR : Color.LIGHT_GRAY);
//            button.setForeground(!"N/A".equals(label) ? Color.WHITE : Color.DARK_GRAY);
//            isPushed = true;
//            editingRow = row;
//            return button;
//        }
//
//        public Object getCellEditorValue() {
//            if (isPushed && !"N/A".equals(label)) dropCourse(editingRow);
//            isPushed = false;
//            return label;
//        }
//
//        public boolean stopCellEditing() {
//            isPushed = false;
//            return super.stopCellEditing();
//        }
//    }
//
//    /* ---------------------- Small dashboard preview section ---------------------- */
//
//    /**
//     * Create a compact "My Courses" section that shows up to 5 enrolled courses.
//     * This static helper is intended to be used by DashboardStudent.
//     */
//    public static JPanel createMiniPanel(int studentId, boolean allowDrop) {
//        return createCompactTable(studentId, allowDrop);
//    }
//
//    private static JPanel createCompactTable(int studentId, boolean allowDrop) {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBackground(BG_COLOR);
//
//        String[] columns = {"Code", "Title", "Section", "Instructor", "Status", "Action"};
//        DefaultTableModel model = new DefaultTableModel(columns, 0) {
//            @Override public boolean isCellEditable(int r, int c) { return c == 5; }
//        };
//
//        JTable table = new JTable(model);
//        styleTableCompact(table);
//
//        // load data
//        String sql =
//                "SELECT c.code, c.title, c.credits, s.section_id, s.semester AS section_semester, s.year AS section_year, " +
//                        "u.username AS instructor, e.status, s.semester_start " +
//                        "FROM enrollments e " +
//                        "JOIN sections s ON e.section_id = s.section_id " +
//                        "JOIN courses c ON s.course_id = c.course_id " +
//                        "JOIN instructors i ON s.instructor_id = i.instructor_id " +
//                        "JOIN auth_db.users u ON i.user_id = u.user_id " +
//                        "WHERE e.student_id = ? " +
//                        "ORDER BY c.code " +
//                        "LIMIT 5";
//
//        int dropPeriodDays = readIntSettingOrDefaultStatic("drop_period_days", 14);
//        boolean dropEnabled = Boolean.parseBoolean(readSettingOrDefaultStatic("drop_enabled", "true"));
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Date semStartDate = rs.getDate("semester_start");
//                LocalDate semesterStart = (semStartDate != null) ? semStartDate.toLocalDate() : null;
//                LocalDateTime deadline = (semesterStart != null)
//                        ? DateTimeUtil.calculateDropDeadline(semesterStart, dropPeriodDays)
//                        : null;
//
//                String status = rs.getString("status");
//                boolean canDrop = dropEnabled && allowDrop && "enrolled".equalsIgnoreCase(status)
//                        && deadline != null && DateTimeUtil.isBeforeDeadline(deadline);
//
//                String sectionLabel = rs.getString("section_semester") + " " + rs.getInt("section_year");
//
//                model.addRow(new Object[]{
//                        rs.getString("code"),
//                        rs.getString("title"),
//                        sectionLabel,
//                        rs.getString("instructor"),
//                        status,
//                        canDrop ? "Drop" : "N/A"
//                });
//            }
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null,
//                    "Error loading course preview: " + ex.getMessage(),
//                    "Database Error",
//                    JOptionPane.ERROR_MESSAGE);
//            ex.printStackTrace();
//        }
//
//        // Wire renderer/editor for action column that calls EnrollmentService directly
//        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
//            private final JButton btn = new JButton();
//
//            @Override
//            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                String label = String.valueOf(value);
//                btn.setText(label);
//                if ("N/A".equals(label)) {
//                    btn.setBackground(Color.LIGHT_GRAY);
//                    btn.setForeground(Color.DARK_GRAY);
//                    btn.setEnabled(false);
//                } else {
//                    btn.setBackground(DANGER_COLOR);
//                    btn.setForeground(Color.WHITE);
//                    btn.setEnabled(true);
//                }
//                btn.setFont(new Font("Arial", Font.BOLD, 11));
//                return btn;
//            }
//        });
//
//        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
//            private final JButton btn = new JButton();
//            private String label;
//            private int editingRow;
//
//            {
//                btn.setOpaque(true);
//                btn.addActionListener(e -> fireEditingStopped());
//            }
//
//            @Override
//            public Component getTableCellEditorComponent(JTable t, Object value, boolean isSelected, int row, int column) {
//                this.label = String.valueOf(value);
//                editingRow = row;
//                btn.setText(label);
//                btn.setEnabled(!"N/A".equals(label));
//                btn.setBackground(!"N/A".equals(label) ? DANGER_COLOR : Color.LIGHT_GRAY);
//                btn.setForeground(!"N/A".equals(label) ? Color.WHITE : Color.DARK_GRAY);
//                return btn;
//            }
//
//            @Override
//            public Object getCellEditorValue() {
//                if (!"N/A".equals(label)) {
//                    String code = (String) model.getValueAt(editingRow, 0);
//                    // Attempt drop via service directly for preview
//                    int confirm = JOptionPane.showConfirmDialog(null,
//                            "Drop " + code + "?",
//                            "Confirm Drop",
//                            JOptionPane.YES_NO_OPTION,
//                            JOptionPane.WARNING_MESSAGE);
//                    if (confirm == JOptionPane.YES_OPTION) {
//                        try {
//                            boolean ok = EnrollmentService.dropCourse(studentId, code);
//                            if (ok) {
//                                JOptionPane.showMessageDialog(null, "Dropped " + code, "Success", JOptionPane.INFORMATION_MESSAGE);
//                                // refresh preview table contents
//                                // rebuild model rows: easiest is to remove row now
//                                model.removeRow(editingRow);
//                            } else {
//                                JOptionPane.showMessageDialog(null, "Cannot drop course (deadline passed or not found).", "Drop Failed", JOptionPane.WARNING_MESSAGE);
//                            }
//                        } catch (SQLException ex) {
//                            JOptionPane.showMessageDialog(null, "Drop failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                        }
//                    }
//                }
//                return label;
//            }
//        });
//
//        JScrollPane scroll = new JScrollPane(table);
//        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
//        panel.add(scroll, BorderLayout.CENTER);
//        return panel;
//    }
//
//    /* ---------------------- Compact table styling ---------------------- */
//
//    private static void styleTableCompact(JTable table) {
//        table.setFont(new Font("Arial", Font.PLAIN, 12));
//        table.setRowHeight(28);
//        table.setShowGrid(true);
//        table.setGridColor(BORDER_COLOR);
//        table.setSelectionBackground(new Color(239, 246, 255));
//        table.setSelectionForeground(TEXT_PRIMARY);
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Arial", Font.BOLD, 12));
//        header.setBackground(new Color(249, 250, 251));
//        header.setForeground(TEXT_PRIMARY);
//    }
//
//    /* ---------------------- Helpers for settings ---------------------- */
//
//    private String readSettingOrDefault(String key, String def) {
//        try {
//            String v = DBUtil.getSetting(key);
//            return (v == null) ? def : v;
//        } catch (Exception ex) {
//            return def;
//        }
//    }
//
//    private int readIntSettingOrDefault(String key, int def) {
//        try {
//            String v = DBUtil.getSetting(key);
//            if (v == null) return def;
//            return Integer.parseInt(v);
//        } catch (Exception ex) {
//            return def;
//        }
//    }
//
//    // static variants used by static mini-panel builder
//    private static String readSettingOrDefaultStatic(String key, String def) {
//        try {
//            String v = DBUtil.getSetting(key);
//            return (v == null) ? def : v;
//        } catch (Exception ex) {
//            return def;
//        }
//    }
//
//    private static int readIntSettingOrDefaultStatic(String key, int def) {
//        try {
//            String v = DBUtil.getSetting(key);
//            if (v == null) return def;
//            return Integer.parseInt(v);
//        } catch (Exception ex) {
//            return def;
//        }
//    }
//
//    /* ---------------------- Utilities ---------------------- */
//
//    private JButton createStyledButton(String text, Color bg) {
//        JButton btn = new JButton(text);
//        btn.setFont(new Font("Arial", Font.BOLD, 13));
//        btn.setBackground(bg);
//        btn.setForeground(Color.WHITE);
//        btn.setFocusPainted(false);
//        btn.setBorderPainted(false);
//        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        btn.setPreferredSize(new Dimension(110, 36));
//        return btn;
//    }
//
//    /**
//     * Show this panel in a dialog window
//     */
//    public static void showDialog(Component parent, int studentId, boolean maintenanceMode) {
//        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "My Enrolled Courses", Dialog.ModalityType.APPLICATION_MODAL);
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//
//        EnrolledCourses panel = new EnrolledCourses(studentId, maintenanceMode);
//        dialog.add(panel);
//
//        dialog.setSize(1100, 600);
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.util.DateTimeUtil;
import edu.univ.erp.service.EnrollmentService;

/**
 * Panel for viewing all enrolled courses with detailed information
 * Uses global drop period: deadline = sections.semester_start + drop_period_days (from settings)
 *
 * Requirements:
 * - DBUtil.getERPConnection() : Connection
 * - DBUtil.getSetting(String) : String  (returns null if not present)
 * - DateTimeUtil.calculateDropDeadline(LocalDate, int)
 * - DateTimeUtil.isBeforeDeadline(LocalDateTime)
 * - DateTimeUtil.format(LocalDateTime)
 * - EnrollmentService.dropCourse(int studentId, String courseCode)
 */
public class EnrolledCourses extends JPanel {

    // --- Aesthetic Color Palette ---
    private static final Color PRIMARY_COLOR = new Color(20, 184, 166);      // Teal (for positive actions/highlights)
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);        // Blue (original primary color)
    private static final Color DANGER_COLOR = new Color(239, 68, 68);        // Red
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Green
    private static final Color WARNING_COLOR = new Color(245, 158, 11);      // Amber
    private static final Color BG_COLOR = new Color(249, 250, 251);          // Light gray background
    private static final Color CARD_COLOR = Color.WHITE;                     // Card/Panel background
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);         // Dark text
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);    // Medium gray text
    private static final Color BORDER_COLOR = new Color(229, 231, 235);      // Light border
    private static final Color HEADER_BG = new Color(243, 244, 246);         // Table header background
    private static final Color SELECTION_COLOR = new Color(204, 251, 241);   // Light teal selection


    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private int studentId;
    private boolean maintenanceMode;

    public EnrolledCourses(int studentId, boolean maintenanceMode) {
        this.studentId = studentId;
        this.maintenanceMode = maintenanceMode;
        initializeUI();
        loadEnrolledCourses();
    }

    /* ---------------------- UI Initialization ---------------------- */

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);

        // --- CHANGE: Remove emoji ---
        JLabel titleLabel = new JLabel("My Enrolled Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        add(headerPanel, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        String[] columns = {"Course Code", "Course Title", "Credits", "Section", "Instructor",
                "Day", "Time", "Room", "Status", "Drop", "Drop Deadline"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only Drop button column editable - FUNCTIONALITY PRESERVED
            }

            @Override
            public Class<?> getColumnClass(int column) {
                // Status column (index 8) is String, Drop (index 9) is Button, others String
                if (column == 9) return JButton.class;
                return String.class;
            }
        };

        coursesTable = new JTable(tableModel);
        styleTable(coursesTable);

        // Add custom renderers/editors
        coursesTable.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer()); // NEW: Status column renderer
        coursesTable.getColumnModel().getColumn(9).setCellRenderer(new DropButtonRenderer()); // FUNCTIONALITY PRESERVED
        coursesTable.getColumnModel().getColumn(9).setCellEditor(new DropButtonEditor(new JCheckBox())); // FUNCTIONALITY PRESERVED

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setBackground(BG_COLOR);


        JButton closeBtn = createStyledButton("Close", TEXT_SECONDARY.darker()); // Keep background light gray, change text to dark
        closeBtn.setForeground(TEXT_PRIMARY); // Set text color to a dark gray for contrast with light background

        closeBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        });


        panel.add(closeBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        // --- FIX: Conditional foreground color for better contrast with light backgrounds ---
        // If the background is light, use dark text. Otherwise, use white text.
        // This makes 'Close' button text dark, while 'Refresh' button text stays white (as PRIMARY_COLOR is dark enough).
        if (bgColor.getRed() > 200 && bgColor.getGreen() > 200 && bgColor.getBlue() > 200) { // Check if bgColor is generally light
            button.setForeground(TEXT_PRIMARY);
        } else {
            button.setForeground(Color.WHITE);
        }

        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 45));

        Color hoverColor = bgColor.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(45); // Increased height for aesthetic padding
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        // Clean header border
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Column widths (FUNCTIONALITY PRESERVED from original defaults)
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(100);
        cm.getColumn(1).setPreferredWidth(220);
        cm.getColumn(2).setPreferredWidth(60);
        cm.getColumn(3).setPreferredWidth(60);
        cm.getColumn(4).setPreferredWidth(150);
        cm.getColumn(5).setPreferredWidth(80);
        cm.getColumn(6).setPreferredWidth(100);
        cm.getColumn(7).setPreferredWidth(80);
        cm.getColumn(8).setPreferredWidth(80); // Status
        cm.getColumn(9).setPreferredWidth(80); // Drop Button
        cm.getColumn(10).setPreferredWidth(140);
    }

    /* ---------------------- Data Loading (FUNCTIONALITY PRESERVED) ---------------------- */

    /**
     * Load enrolled courses, compute drop deadline per-section using semester_start + drop_period_days.
     */
    private void loadEnrolledCourses() {
        tableModel.setRowCount(0);

        // Query pulls semester_start; deadline computed client-side using global drop period
        String query = "SELECT e.enrollment_id, c.code, c.title, c.credits, " +
                "s.section_id, s.semester AS section_semester, s.year AS section_year, s.semester_start, s.day, s.time, s.room, e.status, " +
                "u.username as instructor " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN instructors i ON s.instructor_id = i.instructor_id " +
                "JOIN auth_db.users u ON i.user_id = u.user_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY c.code";

        int dropPeriodDays = readIntSettingOrDefault("drop_period_days", 14);
        boolean dropEnabled = Boolean.parseBoolean(readSettingOrDefault("drop_enabled", "true"));

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date semStartDate = rs.getDate("semester_start");
                LocalDate semesterStart = (semStartDate != null) ? semStartDate.toLocalDate() : null;
                LocalDateTime deadline = (semesterStart != null)
                        ? DateTimeUtil.calculateDropDeadline(semesterStart, dropPeriodDays)
                        : null;

                String status = rs.getString("status");
                boolean canDrop = dropEnabled
                        && "enrolled".equalsIgnoreCase(status)
                        && deadline != null
                        && DateTimeUtil.isBeforeDeadline(deadline)
                        && !maintenanceMode;

                String sectionLabel = rs.getString("section_semester") + " " + rs.getInt("section_year");

                Object[] row = {
                        rs.getString("code"),
                        rs.getString("title"),
                        String.valueOf(rs.getInt("credits")),
                        sectionLabel,
                        rs.getString("instructor"),
                        rs.getString("day"),
                        rs.getString("time"),
                        rs.getString("room"),
                        status, // Index 8: Status
                        canDrop ? "Drop" : "N/A", // Index 9: Drop Button label
                        deadline != null ? DateTimeUtil.format(deadline) : "N/A"
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No enrolled courses found",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ---------------------- Drop action (FUNCTIONALITY PRESERVED) ---------------------- */

    private void dropCourse(int row) {
        if (maintenanceMode) {
            JOptionPane.showMessageDialog(this,
                    "Cannot drop courses during maintenance mode",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCode = (String) tableModel.getValueAt(row, 0);
        String courseTitle = (String) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 8);

        if (!"enrolled".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "Only enrolled courses can be dropped",
                    "Cannot Drop",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + courseCode + " - " + courseTitle + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean success = EnrollmentService.dropCourse(studentId, courseCode);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Successfully dropped " + courseCode,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot drop course. Deadline may have passed or course not found.",
                        "Drop Failed",
                        JOptionPane.WARNING_MESSAGE);
            }
            loadEnrolledCourses(); // Refresh table
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ---------------------- Renderers / Editors (AESTHETICS MODIFIED, FUNCTIONALITY PRESERVED) ---------------------- */

    // NEW: Status Renderer for aesthetic text badges
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(String.valueOf(value));
            label.setOpaque(true);
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            String status = String.valueOf(value).toLowerCase();
            Color statusBG;
            Color statusFG = Color.WHITE;

            switch (status) {
                case "enrolled":
                    statusBG = SUCCESS_COLOR;
                    break;
                case "waitlisted":
                    statusBG = WARNING_COLOR;
                    break;
                case "dropped":
                    statusBG = TEXT_SECONDARY;
                    break;
                default:
                    statusBG = Color.GRAY;
            }

            if (isSelected) {
                label.setBackground(SELECTION_COLOR);
                label.setForeground(statusBG.darker());
            } else {
                // Apply 'pill' style using a containing panel to keep background white/light
                label.setBackground(statusBG);
                label.setForeground(statusFG);

                JPanel container = new JPanel(new GridBagLayout()); // Center alignment
                container.add(label);

                if (isSelected) {
                    container.setBackground(SELECTION_COLOR);
                } else {
                    container.setBackground(CARD_COLOR);
                }
                return container;
            }
            return label; // Fallback if not using pill container logic
        }
    }

    // Drop Button renderer for table (AESTHETICALLY MODIFIED)
    class DropButtonRenderer extends JButton implements TableCellRenderer {
        public DropButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Added padding
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String label = (value == null) ? "Drop" : value.toString();
            setText(label);

            if ("N/A".equals(label)) {
                setBackground(HEADER_BG); // Lighter gray for unavailable
                setForeground(TEXT_SECONDARY);
                setEnabled(false);
            } else {
                setBackground(DANGER_COLOR);
                setForeground(Color.WHITE);
                setEnabled(true);
            }

            if (isSelected) {
                setBackground(getBackground().darker()); // Darker shade when row selected
            }

            return this;
        }
    }

    // Drop Button editor for table (FUNCTIONALITY PRESERVED)
    class DropButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int editingRow;

        public DropButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            button.addActionListener(e -> fireEditingStopped()); // FUNCTIONALITY PRESERVED
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Drop" : value.toString();
            button.setText(label);

            // FUNCTIONALITY PRESERVED
            button.setEnabled(!"N/A".equals(label));
            button.setBackground(!"N/A".equals(label) ? DANGER_COLOR : HEADER_BG);
            button.setForeground(!"N/A".equals(label) ? Color.WHITE : TEXT_SECONDARY);

            isPushed = true;
            editingRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed && !"N/A".equals(label)) dropCourse(editingRow); // FUNCTIONALITY PRESERVED
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing(); // FUNCTIONALITY PRESERVED
        }
    }

    /* ---------------------- Small dashboard preview section (FUNCTIONALITY PRESERVED) ---------------------- */

    /**
     * Create a compact "My Courses" section that shows up to 5 enrolled courses.
     * This static helper is intended to be used by DashboardStudent.
     */
    public static JPanel createMiniPanel(int studentId, boolean allowDrop) {
        return createCompactTable(studentId, allowDrop);
    }

    private static JPanel createCompactTable(int studentId, boolean allowDrop) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        String[] columns = {"Code", "Title", "Section", "Instructor", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 5; } // FUNCTIONALITY PRESERVED
        };

        JTable table = new JTable(model);
        styleTableCompact(table);

        // load data (FUNCTIONALITY PRESERVED)
        String sql =
                "SELECT c.code, c.title, c.credits, s.section_id, s.semester AS section_semester, s.year AS section_year, " +
                        "u.username AS instructor, e.status, s.semester_start " +
                        "FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.section_id " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "JOIN instructors i ON s.instructor_id = i.instructor_id " +
                        "JOIN auth_db.users u ON i.user_id = u.user_id " +
                        "WHERE e.student_id = ? " +
                        "ORDER BY c.code " +
                        "LIMIT 5";

        int dropPeriodDays = readIntSettingOrDefaultStatic("drop_period_days", 14);
        boolean dropEnabled = Boolean.parseBoolean(readSettingOrDefaultStatic("drop_enabled", "true"));

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date semStartDate = rs.getDate("semester_start");
                LocalDate semesterStart = (semStartDate != null) ? semStartDate.toLocalDate() : null;
                LocalDateTime deadline = (semesterStart != null)
                        ? DateTimeUtil.calculateDropDeadline(semesterStart, dropPeriodDays)
                        : null;

                String status = rs.getString("status");
                boolean canDrop = dropEnabled && allowDrop && "enrolled".equalsIgnoreCase(status)
                        && deadline != null && DateTimeUtil.isBeforeDeadline(deadline);

                String sectionLabel = rs.getString("section_semester") + " " + rs.getInt("section_year");

                model.addRow(new Object[]{
                        rs.getString("code"),
                        rs.getString("title"),
                        sectionLabel,
                        rs.getString("instructor"),
                        status,
                        canDrop ? "Drop" : "N/A"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error loading course preview: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Wire renderer/editor for action column (AESTHETICS MODIFIED, FUNCTIONALITY PRESERVED)
        table.getColumnModel().getColumn(4).setCellRenderer(new CompactStatusRenderer()); // NEW: Compact Status Renderer

        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            private final JButton btn = new JButton();
            { // AESTHETICS MODIFIED
                btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String label = String.valueOf(value);
                btn.setText(label);

                // FUNCTIONALITY PRESERVED
                if ("N/A".equals(label)) {
                    btn.setBackground(HEADER_BG);
                    btn.setForeground(TEXT_SECONDARY);
                    btn.setEnabled(false);
                } else {
                    btn.setBackground(DANGER_COLOR);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(true);
                }
                if (isSelected) btn.setBackground(btn.getBackground().darker()); // Aesthetic hover
                return btn;
            }
        });

        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JButton btn = new JButton();
            private String label;
            private int editingRow;

            {
                btn.setOpaque(true);
                btn.addActionListener(e -> fireEditingStopped());
                // AESTHETICS MODIFIED
                btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            }

            @Override
            public Component getTableCellEditorComponent(JTable t, Object value, boolean isSelected, int row, int column) {
                this.label = String.valueOf(value);
                editingRow = row;
                btn.setText(label);
                // FUNCTIONALITY PRESERVED
                btn.setEnabled(!"N/A".equals(label));
                btn.setBackground(!"N/A".equals(label) ? DANGER_COLOR : HEADER_BG);
                btn.setForeground(!"N/A".equals(label) ? Color.WHITE : TEXT_SECONDARY);
                return btn;
            }

            @Override
            public Object getCellEditorValue() {
                if (!"N/A".equals(label)) { // FUNCTIONALITY PRESERVED
                    String code = (String) model.getValueAt(editingRow, 0);
                    // Attempt drop via service directly for preview
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Drop " + code + "?",
                            "Confirm Drop",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            boolean ok = EnrollmentService.dropCourse(studentId, code); // FUNCTIONALITY PRESERVED
                            if (ok) {
                                JOptionPane.showMessageDialog(null, "Dropped " + code, "Success", JOptionPane.INFORMATION_MESSAGE);
                                // refresh preview table contents
                                model.removeRow(editingRow); // FUNCTIONALITY PRESERVED
                            } else {
                                JOptionPane.showMessageDialog(null, "Cannot drop course (deadline passed or not found).", "Drop Failed", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Drop failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // NEW: Status Renderer for Compact Table
    static class CompactStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(String.valueOf(value));
            label.setOpaque(true);
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));

            String status = String.valueOf(value).toLowerCase();
            Color statusBG;

            switch (status) {
                case "enrolled": statusBG = SUCCESS_COLOR.brighter(); break;
                case "waitlisted": statusBG = WARNING_COLOR.brighter(); break;
                case "dropped": statusBG = TEXT_SECONDARY.brighter(); break;
                default: statusBG = Color.LIGHT_GRAY;
            }

            // Apply pill style for better visibility in a compact table
            label.setBackground(statusBG);
            label.setForeground(TEXT_PRIMARY.darker());

            JPanel container = new JPanel(new GridBagLayout());
            container.add(label);
            container.setBackground(isSelected ? new Color(239, 246, 255) : CARD_COLOR);
            return container;
        }
    }



    private static void styleTableCompact(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
    }


    private String readSettingOrDefault(String key, String def) {
        try {
            String v = DBUtil.getSetting(key);
            return (v == null) ? def : v;
        } catch (Exception ex) {
            return def;
        }
    }

    private int readIntSettingOrDefault(String key, int def) {
        try {
            String v = DBUtil.getSetting(key);
            if (v == null) return def;
            return Integer.parseInt(v);
        } catch (Exception ex) {
            return def;
        }
    }

    // static variants used by static mini-panel builder
    private static String readSettingOrDefaultStatic(String key, String def) {
        try {
            String v = DBUtil.getSetting(key);
            return (v == null) ? def : v;
        } catch (Exception ex) {
            return def;
        }
    }

    private static int readIntSettingOrDefaultStatic(String key, int def) {
        try {
            String v = DBUtil.getSetting(key);
            if (v == null) return def;
            return Integer.parseInt(v);
        } catch (Exception ex) {
            return def;
        }
    }


    public static void showDialog(Component parent, int studentId, boolean maintenanceMode) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "My Enrolled Courses", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        EnrolledCourses panel = new EnrolledCourses(studentId, maintenanceMode);
        dialog.add(panel);

        dialog.setSize(1100, 650); // Slightly adjusted size for new padding/fonts
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}