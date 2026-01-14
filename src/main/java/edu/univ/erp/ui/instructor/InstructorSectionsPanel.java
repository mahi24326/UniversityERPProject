//package edu.univ.erp.ui.instructor;
//
//import edu.univ.erp.domain.Instructor;
//import edu.univ.erp.domain.SectionWithAvailability;
//import edu.univ.erp.service.InstructorService;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.table.TableColumn;
//import javax.swing.table.JTableHeader;
//import java.awt.*;
//import java.util.List;
//
//public class InstructorSectionsPanel extends JPanel {
//
//    private static final Color BG_COLOR = new Color(249, 250, 251);
//    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
//    private static final Color BORDER_COLOR = new Color(229, 231, 235);
//    private static final Color PRIMARY_COLOR = new Color(20, 184, 166);
//
//    private final Instructor instructor;
//
//    public InstructorSectionsPanel(Instructor instructor) {
//        this.instructor = instructor;
//        initialize();
//    }
//
//    private void initialize() {
//        setLayout(new BorderLayout());
//        setBackground(BG_COLOR);
//
//        // HEADER
//        JPanel header = new JPanel(new BorderLayout());
//        header.setBackground(BG_COLOR);
//
//        JLabel title = new JLabel("My Teaching Sections");
//        title.setFont(new Font("Arial", Font.BOLD, 22));
//        title.setForeground(TEXT_PRIMARY);
//
//        header.add(title, BorderLayout.WEST);
//        add(header, BorderLayout.NORTH);
//
//        // FETCH SECTIONS
//        InstructorService instructorService = new InstructorService();
//        List<SectionWithAvailability> sections =
//                instructorService.getMySections(instructor.getInstructorId());
//
//        String[] columns = {
//                "Course Code", "Course Title", "Section ID",
//                "Schedule", "Room", "Enrolled", "Avg Grade", "Action"
//        };
//
//        DefaultTableModel model = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 7; // only "Action" column
//            }
//        };
//
//        for (SectionWithAvailability s : sections) {
//            model.addRow(new Object[] {
//                    s.getcourseCode(),
//                    s.getcourseTitle(),
//                    s.getsectionId(),
//                    s.getschedule(),
//                    s.getroom(),
//                    s.getenrolled() + "/" + s.getcapacity(),
//                    "—",
//                    "Manage"
//            });
//        }
//
//        JTable table = new JTable(model);
//        table.setRowHeight(35);
//        styleTable(table);
//
//        // SET CUSTOM RENDERER FOR MANAGE BUTTON
//        TableColumn actionColumn = table.getColumnModel().getColumn(7);
//        actionColumn.setCellRenderer(new ButtonRenderer());
//        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox(), table, sections));
//
//        JScrollPane scrollPane = new JScrollPane(table);
//        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
//
//        add(scrollPane, BorderLayout.CENTER);
//    }
//
//    private void styleTable(JTable table) {
//        table.setFont(new Font("Arial", Font.PLAIN, 12));
//        table.setGridColor(BORDER_COLOR);
//        table.setShowGrid(true);
//        table.setSelectionBackground(new Color(204, 251, 241));
//        table.setSelectionForeground(TEXT_PRIMARY);
//
//        JTableHeader header = table.getTableHeader();
//        header.setFont(new Font("Arial", Font.BOLD, 13));
//        header.setBackground(new Color(249, 250, 251));
//        header.setForeground(TEXT_PRIMARY);
//        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
//    }
//
//    // BUTTON RENDERER
//    class ButtonRenderer extends JButton implements TableCellRenderer {
//        public ButtonRenderer() {
//            setOpaque(true);
//        }
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                                                       boolean isSelected, boolean hasFocus,
//                                                       int row, int column) {
//            setText("Manage");
//            setBackground(PRIMARY_COLOR);
//            setForeground(Color.WHITE);
//            return this;
//        }
//    }
//
//    // BUTTON EDITOR
//    class ButtonEditor extends DefaultCellEditor {
//        private JButton button;
//        private boolean clicked;
//        private JTable table;
//        private List<SectionWithAvailability> sections;
//
//        public ButtonEditor(JCheckBox checkBox, JTable table, List<SectionWithAvailability> sections) {
//            super(checkBox);
//            this.table = table;
//            this.sections = sections;
//
//            button = new JButton("Manage");
//            button.setBackground(PRIMARY_COLOR);
//            button.setForeground(Color.WHITE);
//            button.addActionListener(e -> fireEditingStopped());
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable table, Object value,
//                                                     boolean isSelected, int row, int col) {
//            clicked = true;
//            return button;
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//            if (clicked) {
//                SectionWithAvailability s = sections.get(table.getSelectedRow());
//                openManageDialog(s.getsectionId(), s.getCourseId());
//            }
//            clicked = false;
//            return "Manage";
//        }
//    }
//
//    private void openManageDialog(int sectionId, int courseId) {
//        JPopupMenu menu = new JPopupMenu();
//
//        JMenuItem m1 = new JMenuItem("Assessment Rules");
//        JMenuItem m2 = new JMenuItem("Enter Grades");
//        JMenuItem m3 = new JMenuItem("Class Stats");
//
//        m1.addActionListener(e -> new AssessmentRuleEditor(
//                SwingUtilities.getWindowAncestor(this), courseId).setVisible(true));
//
//        m2.addActionListener(e -> new GradeEntryUI(
//                SwingUtilities.getWindowAncestor(this), sectionId, courseId).setVisible(true));
//
//        m3.addActionListener(e -> new ClassStatsUI(
//                SwingUtilities.getWindowAncestor(this), sectionId, courseId).setVisible(true));
//
//        menu.add(m1);
//        menu.add(m2);
//        menu.add(m3);
//
//        menu.show(this, 150, 75);
//    }
//}




package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.SectionWithAvailability;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class InstructorSectionsPanel extends JPanel {

    // Aesthetic color palette for a cleaner look
    private static final Color BG_COLOR = new Color(249, 250, 251); // Light off-white background
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39); // Dark text
    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Light grey border
    private static final Color PRIMARY_COLOR = new Color(20, 184, 166); // Teal/Cyan for primary action

    private final Instructor instructor;

    public InstructorSectionsPanel(Instructor instructor) {
        this.instructor = instructor;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout(0, 20)); // Added vertical gap between header and content
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Overall padding

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);

        JLabel title = new JLabel("My Teaching Sections");
        title.setFont(new Font("Arial", Font.BOLD, 26)); // Slightly larger title
        title.setForeground(TEXT_PRIMARY);

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // FETCH SECTIONS
        // NO FUNCTIONALITY CHANGE HERE
        InstructorService instructorService = new InstructorService();
        List<SectionWithAvailability> sections =
                instructorService.getMySections(instructor.getInstructorId());

        String[] columns = {
                "Course Code", "Course Title", "Section ID",
                "Schedule", "Room", "Enrolled", "Avg Grade", "Action"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // only "Action" column
            }
        };

        for (SectionWithAvailability s : sections) {
            model.addRow(new Object[] {
                    s.getcourseCode(),
                    s.getcourseTitle(),
                    s.getsectionId(),
                    s.getschedule(),
                    s.getroom(),
                    s.getenrolled() + "/" + s.getcapacity(),
                    "—",
                    "Manage"
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(40); // Increased row height for better visual spacing
        styleTable(table);

        // SET CUSTOM RENDERER FOR MANAGE BUTTON
        TableColumn actionColumn = table.getColumnModel().getColumn(7);
        actionColumn.setCellRenderer(new ButtonRenderer());
        // Pass the table and sections list to the editor
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox(), table, sections));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        // Ensure background shows through padding
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14)); // Slightly larger font
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false); // Removed vertical lines for modern look
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(204, 251, 241)); // Light selection highlight
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(249, 250, 251)); // Very light header background
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR.darker())); // Teal bottom border
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35)); // Header height
    }

    // BUTTON RENDERER
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(null); // Clean border
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText("Manage");
            setBackground(PRIMARY_COLOR);
            setForeground(Color.WHITE); // **FIXED: Ensures white font on teal background**
            return this;
        }
    }

    // BUTTON EDITOR
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private JTable table;
        private List<SectionWithAvailability> sections;

        public ButtonEditor(JCheckBox checkBox, JTable table, List<SectionWithAvailability> sections) {
            super(checkBox);
            this.table = table;
            this.sections = sections;

            button = new JButton("Manage");
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE); // **FIXED: Explicitly sets white font for the button in the editor**
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBorder(null); // Clean border
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int col) {
            clicked = true;
            return button;
        }

        // NO FUNCTIONALITY CHANGE HERE
        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                SectionWithAvailability s = sections.get(table.getSelectedRow());
                openManageDialog(s.getsectionId(), s.getCourseId());
            }
            clicked = false;
            return "Manage";
        }
    }

    // NO FUNCTIONALITY CHANGE HERE
    private void openManageDialog(int sectionId, int courseId) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem m1 = new JMenuItem("Assessment Rules");
        JMenuItem m2 = new JMenuItem("Enter Grades");
        JMenuItem m3 = new JMenuItem("Class Stats");

        m1.addActionListener(e -> new AssessmentRuleEditor(
                SwingUtilities.getWindowAncestor(this), courseId).setVisible(true));

        m2.addActionListener(e -> new GradeEntryUI(
                SwingUtilities.getWindowAncestor(this), sectionId, courseId).setVisible(true));

        m3.addActionListener(e -> new ClassStatsUI(
                SwingUtilities.getWindowAncestor(this), sectionId, courseId).setVisible(true));

        menu.add(m1);
        menu.add(m2);
        menu.add(m3);

        menu.show(this, 150, 75);
    }
}