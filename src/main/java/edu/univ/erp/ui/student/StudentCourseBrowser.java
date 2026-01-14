//package edu.univ.erp.ui.student;
//
//import edu.univ.erp.domain.Course;
//import edu.univ.erp.domain.SectionWithAvailability;
//import edu.univ.erp.service.CourseService;
//import edu.univ.erp.service.SectionService;
//import edu.univ.erp.service.StudentService;
//import edu.univ.erp.access.MaintenanceAccess;
//
//import javax.swing.*;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.List;
//
//public class StudentCourseBrowser extends JPanel {
//
//    // --- Aesthetic Color Palette ---
//    private static final Color BG_COLOR = new Color(249, 250, 251);     // Light off-white background
//    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);    // Dark text
//    private static final Color PRIMARY_COLOR = new Color(20, 184, 166); // Teal/Cyan for primary action
//    private static final Color DANGER_COLOR = new Color(239, 68, 68);   // Red for warnings
//    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Light grey border
//    private static final Color HEADER_BG = new Color(243, 244, 246);    // Table header background
//    private static final Color SELECTION_COLOR = new Color(204, 251, 241); // Light teal selection
//
//    private JTable coursesTable;
//    private JTable sectionsTable;
//
//    private DefaultTableModel coursesModel;
//    private DefaultTableModel sectionsModel;
//
//    private final CourseService courseService = new CourseService();
//    private final SectionService sectionService = new SectionService();
//    private final StudentService studentService = new StudentService();
//
//    private final int studentId;
//    private boolean maintenanceMode;
//
//    public StudentCourseBrowser(int studentId) {
//        this.studentId = studentId;
//
//        this.maintenanceMode = !MaintenanceAccess.getInstance().isMaintenanceMode();
//
//        setLayout(new BorderLayout(15, 15)); // Increased gap for spacing
//        setBackground(BG_COLOR);
//        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//
//        add(createSortBar(), BorderLayout.NORTH);
//        add(createMainSplitPanel(), BorderLayout.CENTER);
//        add(createBottomPanel(), BorderLayout.SOUTH);
//
//        loadCourses();
//    }
//
//    private JPanel createSortBar() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
//
//        JLabel label = new JLabel("Sort Courses By:");
//        label.setFont(new Font("Arial", Font.BOLD, 12));
//        label.setForeground(TEXT_PRIMARY);
//
//        JComboBox<String> sortBox = new JComboBox<>(new String[]{
//                "Course Code (Prefix + Number)",
//                "Alphabetical (Title)"
//        });
//
//        sortBox.setFont(new Font("Arial", Font.PLAIN, 12));
//        sortBox.setBackground(Color.WHITE);
//        sortBox.setForeground(TEXT_PRIMARY);
//
//        // Styling the JComboBox to look cleaner
//        sortBox.setRenderer(new DefaultListCellRenderer() {
//            @Override
//            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                comp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//                return comp;
//            }
//        });
//
//        sortBox.addActionListener(e -> applySort(sortBox.getSelectedIndex()));
//
//        panel.add(label);
//        panel.add(sortBox);
//
//        return panel;
//    }
//
//    // NO FUNCTIONALITY CHANGE HERE
//    private void applySort(int option) {
//
//        TableRowSorter<DefaultTableModel> sorter =
//                (TableRowSorter<DefaultTableModel>) coursesTable.getRowSorter();
//
//        if (sorter == null) return;
//
//        if (option == 0) {
//            // === Sort by COURSE CODE ===
//            sorter.setComparator(0, (a, b) -> {
//                String s1 = a.toString();
//                String s2 = b.toString();
//
//                // prefix
//                String p1 = s1.replaceAll("[0-9]", "");
//                String p2 = s2.replaceAll("[0-9]", "");
//                int pc = p1.compareToIgnoreCase(p2);
//                if (pc != 0) return pc;
//
//                // numeric part
//                int n1 = Integer.parseInt(s1.replaceAll("\\D", "0"));
//                int n2 = Integer.parseInt(s2.replaceAll("\\D", "0"));
//                return Integer.compare(n1, n2);
//            });
//
//            sorter.setSortKeys(
//                    java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING))
//            );
//        }
//
//        else if (option == 1) {
//            // === Sort ALPHABETICALLY BY TITLE ===
//            sorter.setComparator(1, (a, b) ->
//                    a.toString().compareToIgnoreCase(b.toString())
//            );
//
//            sorter.setSortKeys(
//                    java.util.List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING))
//            );
//        }
//
//        sorter.sort();
//    }
//
//
//    private JSplitPane createMainSplitPanel() {
//
//        coursesModel = new DefaultTableModel(
//                new String[]{"Code","Title","Credits","CourseId"}, 0)
//        {
//            public boolean isCellEditable(int r,int c){ return false; }
//            @Override
//            public Class<?> getColumnClass(int columnIndex) {
//                if (columnIndex == 3) return Integer.class; // Ensure CourseId is treated as Integer
//                return super.getColumnClass(columnIndex);
//            }
//        };
//
//        sectionsModel = new DefaultTableModel(
//                new String[]{"Section","Instructor","Schedule","Room","Enroll","Status"}, 0)
//        {
//            public boolean isCellEditable(int r,int c){ return false; }
//            @Override
//            public Class<?> getColumnClass(int columnIndex) {
//                if (columnIndex == 0) return Integer.class; // Section ID
//                return super.getColumnClass(columnIndex);
//            }
//        };
//
//        coursesTable = new JTable(coursesModel);
//        sectionsTable = new JTable(sectionsModel);
//
//        styleTable(coursesTable);
//        styleTable(sectionsTable);
//
//        /* === ENABLE SORTING FOR BOTH TABLES (Functionality preserved) === */
//        TableRowSorter<DefaultTableModel> courseSorter =
//                new TableRowSorter<>(coursesModel);
//
//        /* ----- CUSTOM SORTING LOGIC FOR COURSES (Functionality preserved) ----- */
//        courseSorter.setComparator(0, (a, b) -> {
//            String s1 = a.toString();
//            String s2 = b.toString();
//            String p1 = s1.replaceAll("[0-9]", "");
//            String p2 = s2.replaceAll("[0-9]", "");
//
//            int prefixCompare = p1.compareToIgnoreCase(p2);
//            if (prefixCompare != 0) return prefixCompare;
//
//            int n1 = Integer.parseInt(s1.replaceAll("\\D","0"));
//            int n2 = Integer.parseInt(s2.replaceAll("\\D","0"));
//            return Integer.compare(n1, n2);
//        });
//
//        courseSorter.setComparator(1, (a, b) ->
//                a.toString().compareToIgnoreCase(b.toString())
//        );
//
//        coursesTable.setRowSorter(courseSorter);
//
//
//        // Section table sorter (Functionality preserved)
//        TableRowSorter<DefaultTableModel> sectionSorter =
//                new TableRowSorter<>(sectionsModel);
//        sectionsTable.setRowSorter(sectionSorter);
//
//        coursesTable.getTableHeader().putClientProperty("TableHeader.enableSortingIcons", true);
//        sectionsTable.getTableHeader().putClientProperty("TableHeader.enableSortingIcons", true);
//
//        /* ------------------------------------------------ */
//
//        // NO FUNCTIONALITY CHANGE HERE
//        coursesTable.getSelectionModel().addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) loadSectionsForSelectedCourse();
//        });
//
//        // Remove CourseId column from display
//        TableColumn courseIdColumn = coursesTable.getColumnModel().getColumn(3);
//        coursesTable.removeColumn(courseIdColumn);
//
//        // Style Status column (visual enhancement)
//        sectionsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
//
//
//        JScrollPane leftPane = new JScrollPane(coursesTable);
//        JScrollPane rightPane = new JScrollPane(sectionsTable);
//
//        leftPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
//        rightPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
//
//        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
//        split.setDividerLocation(400); // FUNCTIONALITY PRESERVED
//        split.setBorder(null);
//
//        return split;
//    }
//
//    private void styleTable(JTable table) {
//        table.setFont(new Font("Arial", Font.PLAIN, 13));
//        table.setRowHeight(35);
//        table.setGridColor(BORDER_COLOR);
//        table.setShowVerticalLines(false);
//        table.setShowHorizontalLines(true);
//        table.setSelectionBackground(SELECTION_COLOR);
//        table.setSelectionForeground(TEXT_PRIMARY);
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Arial", Font.BOLD, 13));
//        header.setBackground(HEADER_BG);
//        header.setForeground(TEXT_PRIMARY);
//        // Clean header bottom border
//        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
//    }
//
//    private JPanel createBottomPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//        panel.setBackground(BG_COLOR);
//
//        JButton registerBtn = createStyledButton("Register for Selected Section", PRIMARY_COLOR);
//
//        if (maintenanceMode) {
//            registerBtn.setEnabled(false);
//            registerBtn.setBackground(Color.GRAY);
//        }
//
//        registerBtn.addActionListener(e -> {
//            if (maintenanceMode) {
//                // NO FUNCTIONALITY CHANGE HERE
//                JOptionPane.showMessageDialog(this,
//                        "Registering for courses not allowed, maintenance mode is ON",
//                        "Maintenance Mode",
//                        JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            // NO FUNCTIONALITY CHANGE HERE
//            registerForSelectedSection();
//        });
//
//        panel.add(registerBtn);
//
//        return panel;
//    }
//
//    private JButton createStyledButton(String text, Color bgColor) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("Arial", Font.BOLD, 14));
//        button.setForeground(Color.WHITE);
//        button.setBackground(bgColor);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//
//        button.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                if(button.isEnabled()) button.setBackground(bgColor.darker());
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                if(button.isEnabled()) button.setBackground(bgColor);
//            }
//        });
//
//        return button;
//    }
//
//    // NO FUNCTIONALITY CHANGE HERE
//    private void loadCourses() {
//        coursesModel.setRowCount(0);
//        try {
//            List<Course> list = courseService.getAllCourses();
//            for (Course c : list) {
//                coursesModel.addRow(new Object[]{
//                        c.getCode(), c.getTitle(), c.getCredits(), c.getCourseId()
//                });
//            }
//            // Apply sort after loading data
//            applySort(0);
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
//        }
//    }
//
//    // NO FUNCTIONALITY CHANGE HERE
//    private void loadSectionsForSelectedCourse() {
//        sectionsModel.setRowCount(0);
//
//        int viewRow = coursesTable.getSelectedRow();
//        if (viewRow == -1) return;
//
//        // convert view row to model row for sorter
//        int modelRow = coursesTable.convertRowIndexToModel(viewRow);
//
//        // CourseId is now at index 3 in the model
//        int courseId = (int) coursesModel.getValueAt(modelRow, 3);
//
//        try {
//            List<SectionWithAvailability> sections = sectionService.getSections(courseId, studentId);
//
//            for (SectionWithAvailability s : sections) {
//                sectionsModel.addRow(new Object[]{
//                        s.getsectionId(),
//                        s.getinstructorName(),
//                        s.getschedule(),
//                        s.getroom(),
//                        s.getenrolled() + "/" + s.getcapacity(),
//                        s.getstatus()
//                });
//            }
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage());
//        }
//    }
//
//    // NO FUNCTIONALITY CHANGE HERE
//    private void registerForSelectedSection() {
//
//        int viewRow = sectionsTable.getSelectedRow();
//        if (viewRow == -1) {
//            JOptionPane.showMessageDialog(this, "Select a section");
//            return;
//        }
//
//        int modelRow = sectionsTable.convertRowIndexToModel(viewRow);
//
//        int sectionId = (int) sectionsModel.getValueAt(modelRow, 0);
//        String status = (String) sectionsModel.getValueAt(modelRow, 5);
//
//        if (!status.equals("Available")) {
//            JOptionPane.showMessageDialog(this, "Cannot register: " + status);
//            return;
//        }
//
//        int ok = JOptionPane.showConfirmDialog(this,
//                "Register for section " + sectionId + "?",
//                "Confirm", JOptionPane.YES_NO_OPTION);
//
//        if (ok != JOptionPane.YES_OPTION) return;
//
//        String result = studentService.registerForCourse(studentId, sectionId);
//
//        if (result.equals("success")) {
//            JOptionPane.showMessageDialog(this, "Registration successful.");
//            loadSectionsForSelectedCourse();
//        } else if (result.equals("duplicate")) {
//            JOptionPane.showMessageDialog(this, "Already enrolled.");
//        } else {
//            JOptionPane.showMessageDialog(this, "Error: " + result);
//        }
//    }
//
//    // --- Status Cell Renderer for visual aesthetic ---
//    class StatusRenderer extends DefaultTableCellRenderer {
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//            String status = (String) value;
//            label.setHorizontalAlignment(CENTER);
//            label.setFont(new Font("Arial", Font.BOLD, 11));
//            label.setOpaque(true);
//
//            Color statusBG;
//            Color statusFG = Color.WHITE;
//
//            // Set colors based on status
//            if (status.equals("Available")) {
//                statusBG = PRIMARY_COLOR.darker();
//            } else if (status.equals("Full")) {
//                statusBG = DANGER_COLOR;
//            } else if (status.equals("Enrolled")) {
//                statusBG = new Color(34, 197, 94); // Success Green
//            } else if (status.equals("Conflict")) {
//                statusBG = new Color(245, 158, 11); // Warning Orange
//            } else {
//                statusBG = Color.GRAY;
//            }
//
//            // Adjust label appearance
//            if (isSelected) {
//                // If selected, keep the selection background but show the status text cleanly
//                label.setBackground(SELECTION_COLOR);
//                label.setForeground(statusBG); // Use the status color for text when selected
//                label.setBorder(BorderFactory.createLineBorder(statusBG.darker(), 1));
//            } else {
//                // Normal appearance: text inside a colored pill/badge
//                label.setBackground(statusBG);
//                label.setForeground(statusFG);
//                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
//
//                // Overlay text with a semi-transparent panel to give a pill effect
//                JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
//                container.setBackground(table.getBackground()); // Match table background
//                if(isSelected) container.setBackground(SELECTION_COLOR);
//
//                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//                container.add(label);
//                return container;
//            }
//
//            return label;
//        }
//    }
//
//
//    // NO FUNCTIONALITY CHANGE HERE
//    public static void showDialog(Component parent, int studentId) {
//        JDialog dialog = new JDialog(
//                SwingUtilities.getWindowAncestor(parent),
//                "Browse Courses",
//                Dialog.ModalityType.APPLICATION_MODAL
//        );
//
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//
//        StudentCourseBrowser browser = new StudentCourseBrowser(studentId);
//        dialog.add(browser);
//
//        dialog.setSize(1000, 600);
//        dialog.setLocationRelativeTo(parent);
//        dialog.setVisible(true);
//    }
//}






package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.SectionWithAvailability;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.access.MaintenanceAccess;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentCourseBrowser extends JPanel {

    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color PRIMARY_COLOR = new Color(20, 184, 166);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color HEADER_BG = new Color(243, 244, 246);
    private static final Color SELECTION_COLOR = new Color(204, 251, 241);

    private JTable coursesTable;
    private JTable sectionsTable;

    private DefaultTableModel coursesModel;
    private DefaultTableModel sectionsModel;

    private final CourseService courseService = new CourseService();
    private final SectionService sectionService = new SectionService();
    private final StudentService studentService = new StudentService();

    private final int studentId;
    private boolean maintenanceMode;

    public StudentCourseBrowser(int studentId) {
        this.studentId = studentId;

        this.maintenanceMode = !MaintenanceAccess.getInstance().isMaintenanceMode();

        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createSortBar(), BorderLayout.NORTH);
        add(createMainSplitPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadCourses();
    }

    private JPanel createSortBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JLabel label = new JLabel("Sort Courses By:");
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);

        JComboBox<String> sortBox = new JComboBox<>(new String[]{
                "Course Code (Prefix + Number)",
                "Alphabetical (Title)"
        });

        sortBox.setFont(new Font("Arial", Font.PLAIN, 12));
        sortBox.setBackground(Color.WHITE);
        sortBox.setForeground(TEXT_PRIMARY);

        sortBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                comp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return comp;
            }
        });

        sortBox.addActionListener(e -> applySort(sortBox.getSelectedIndex()));

        panel.add(label);
        panel.add(sortBox);

        return panel;
    }

    private void applySort(int option) {

        TableRowSorter<DefaultTableModel> sorter =
                (TableRowSorter<DefaultTableModel>) coursesTable.getRowSorter();

        if (sorter == null) return;

        if (option == 0) {
            sorter.setComparator(0, (a, b) -> {
                String s1 = a.toString();
                String s2 = b.toString();

                String p1 = s1.replaceAll("[0-9]", "");
                String p2 = s2.replaceAll("[0-9]", "");
                int pc = p1.compareToIgnoreCase(p2);
                if (pc != 0) return pc;

                int n1 = Integer.parseInt(s1.replaceAll("\\D", "0"));
                int n2 = Integer.parseInt(s2.replaceAll("\\D", "0"));
                return Integer.compare(n1, n2);
            });

            sorter.setSortKeys(
                    java.util.List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING))
            );
        }

        else if (option == 1) {

            sorter.setComparator(1, (a, b) ->
                    a.toString().compareToIgnoreCase(b.toString())
            );

            sorter.setSortKeys(
                    java.util.List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING))
            );
        }

        sorter.sort();
    }

    private JSplitPane createMainSplitPanel() {

        coursesModel = new DefaultTableModel(
                new String[]{"Code","Title","Credits","CourseId"}, 0)
        {
            public boolean isCellEditable(int r,int c){ return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };

        sectionsModel = new DefaultTableModel(
                new String[]{"Section","Instructor","Schedule","Room","Enroll","Status"}, 0)
        {
            public boolean isCellEditable(int r,int c){ return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };

        coursesTable = new JTable(coursesModel);
        sectionsTable = new JTable(sectionsModel);

        styleTable(coursesTable);
        styleTable(sectionsTable);

        TableRowSorter<DefaultTableModel> courseSorter =
                new TableRowSorter<>(coursesModel);

        courseSorter.setComparator(0, (a, b) -> {
            String s1 = a.toString();
            String s2 = b.toString();
            String p1 = s1.replaceAll("[0-9]", "");
            String p2 = s2.replaceAll("[0-9]", "");

            int prefixCompare = p1.compareToIgnoreCase(p2);
            if (prefixCompare != 0) return prefixCompare;

            int n1 = Integer.parseInt(s1.replaceAll("\\D","0"));
            int n2 = Integer.parseInt(s2.replaceAll("\\D","0"));
            return Integer.compare(n1, n2);
        });

        courseSorter.setComparator(1, (a, b) ->
                a.toString().compareToIgnoreCase(b.toString())
        );

        coursesTable.setRowSorter(courseSorter);

        TableRowSorter<DefaultTableModel> sectionSorter =
                new TableRowSorter<>(sectionsModel);
        sectionsTable.setRowSorter(sectionSorter);

        coursesTable.getTableHeader().putClientProperty("TableHeader.enableSortingIcons", true);
        sectionsTable.getTableHeader().putClientProperty("TableHeader.enableSortingIcons", true);

        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSectionsForSelectedCourse();
        });

        TableColumn courseIdColumn = coursesTable.getColumnModel().getColumn(3);
        coursesTable.removeColumn(courseIdColumn);

        sectionsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane leftPane = new JScrollPane(coursesTable);
        JScrollPane rightPane = new JScrollPane(sectionsTable);

        leftPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        rightPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
        split.setDividerLocation(400);
        split.setBorder(null);

        return split;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(BG_COLOR);

        JButton registerBtn = createStyledButton("Register for Selected Section", PRIMARY_COLOR);

        if (maintenanceMode) {
            registerBtn.setEnabled(false);
            registerBtn.setBackground(Color.GRAY);
        }

        registerBtn.addActionListener(e -> {
            if (maintenanceMode) {
                JOptionPane.showMessageDialog(this,
                        "Registering for courses not allowed, maintenance mode is ON",
                        "Maintenance Mode",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            registerForSelectedSection();
        });

        // -------------------------
        // NEW DROP BUTTON (ADDED)
        // -------------------------
        JButton dropBtn = createStyledButton("Drop Selected Section", DANGER_COLOR);
        dropBtn.addActionListener(e -> dropSelectedSection());
        panel.add(dropBtn);

        panel.add(registerBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(button.isEnabled()) button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if(button.isEnabled()) button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadCourses() {
        coursesModel.setRowCount(0);
        try {
            List<Course> list = courseService.getAllCourses();
            for (Course c : list) {
                coursesModel.addRow(new Object[]{
                        c.getCode(), c.getTitle(), c.getCredits(), c.getCourseId()
                });
            }
            applySort(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
        }
    }

    private void loadSectionsForSelectedCourse() {
        sectionsModel.setRowCount(0);

        int viewRow = coursesTable.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = coursesTable.convertRowIndexToModel(viewRow);
        int courseId = (int) coursesModel.getValueAt(modelRow, 3);

        try {
            List<SectionWithAvailability> sections = sectionService.getSections(courseId, studentId);

            for (SectionWithAvailability s : sections) {
                sectionsModel.addRow(new Object[]{
                        s.getsectionId(),
                        s.getinstructorName(),
                        s.getschedule(),
                        s.getroom(),
                        s.getenrolled() + "/" + s.getcapacity(),
                        s.getstatus()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage());
        }
    }

    private void registerForSelectedSection() {

        int viewRow = sectionsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a section");
            return;
        }

        int modelRow = sectionsTable.convertRowIndexToModel(viewRow);

        int sectionId = (int) sectionsModel.getValueAt(modelRow, 0);
        String status = (String) sectionsModel.getValueAt(modelRow, 5);

        if (!status.equals("Available")) {
            JOptionPane.showMessageDialog(this, "Cannot register: " + status);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Register for section " + sectionId + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        String result = studentService.registerForCourse(studentId, sectionId);

        if (result.equals("success")) {
            JOptionPane.showMessageDialog(this, "Registration successful.");
            loadSectionsForSelectedCourse();
        } else if (result.equals("duplicate")) {
            JOptionPane.showMessageDialog(this, "Already enrolled.");
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + result);
        }
    }

    // -----------------------------------------------------
    // NEW FUNCTIONALITY: DROP COURSE (ONLY NEW FEATURE)
    // -----------------------------------------------------
    private void dropSelectedSection() {

        int viewRow = sectionsTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a section to drop.");
            return;
        }

        int modelRow = sectionsTable.convertRowIndexToModel(viewRow);

        int sectionId = (int) sectionsModel.getValueAt(modelRow, 0);
        String status = (String) sectionsModel.getValueAt(modelRow, 5);

        if (!status.equals("Enrolled")) {
            JOptionPane.showMessageDialog(this, "Cannot drop because you are not enrolled.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Drop section " + sectionId + "?",
                "Confirm Drop", JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        String result = studentService.dropCourse(studentId, sectionId);

        if (result.equals("success")) {
            JOptionPane.showMessageDialog(this, "Course dropped.");
            loadSectionsForSelectedCourse();
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + result);
        }
    }
    // -----------------------------------------------------

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = (String) value;
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setOpaque(true);

            Color statusBG;
            Color statusFG = Color.WHITE;

            if (status.equals("Available")) {
                statusBG = PRIMARY_COLOR.darker();
            } else if (status.equals("Full")) {
                statusBG = DANGER_COLOR;
            } else if (status.equals("Enrolled")) {
                statusBG = new Color(34, 197, 94);
            } else if (status.equals("Conflict")) {
                statusBG = new Color(245, 158, 11);
            } else {
                statusBG = Color.GRAY;
            }

            if (isSelected) {
                label.setBackground(SELECTION_COLOR);
                label.setForeground(statusBG);
                label.setBorder(BorderFactory.createLineBorder(statusBG.darker(), 1));
            } else {
                label.setBackground(statusBG);
                label.setForeground(statusFG);
                label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

                JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                container.setBackground(table.getBackground());
                if(isSelected) container.setBackground(SELECTION_COLOR);

                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                container.add(label);
                return container;
            }

            return label;
        }
    }

    public static void showDialog(Component parent, int studentId) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "Browse Courses",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        StudentCourseBrowser browser = new StudentCourseBrowser(studentId);
        dialog.add(browser);

        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
