package edu.univ.erp.ui.student;

import edu.univ.erp.service.TimetableService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;


public class StudentTimetable extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(38, 38, 35);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private int studentId;

    public StudentTimetable(int studentId) {
        this.studentId = studentId;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel("📅 My Weekly Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Timetable grid
        add(createTimetableGrid(), BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_COLOR);
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 13));
        closeBtn.setBackground(Color.LIGHT_GRAY);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(100, 40));
        closeBtn.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JScrollPane createTimetableGrid() {

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] columns = new String[days.length + 1];
        columns[0] = "Time";
        System.arraycopy(days, 0, columns, 1, days.length);

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Define standard time slots
        String[] timeSlots = {
                "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00",
                "11:00 - 12:00", "12:00 - 13:00", "13:00 - 14:00",
                "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"
        };

        // Fetch timetable from service
        Map<String, List<TimetableService.CourseSlot>> timetable = TimetableService.getStudentTimetable(studentId);

        // Populate model
        for (String timeSlot : timeSlots) {
            Object[] row = new Object[days.length + 1];
            row[0] = timeSlot;

            for (int i = 0; i < days.length; i++) {
                List<TimetableService.CourseSlot> slots = timetable.get(days[i]);
                row[i + 1] = "";
                if (slots != null) {
                    for (TimetableService.CourseSlot slot : slots) {
                        // If section overlaps this time slot
                        if (slot.time.startsWith(timeSlot.substring(0, 5))) {
                            row[i + 1] = "<html><b>" + slot.code + "</b><br/>"
                                    + slot.title + "<br/>"
                                    + "<small>" + slot.room + " • " + slot.instructor + "</small></html>";
                            break;
                        }
                    }
                }
            }

            model.addRow(row);
        }

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return scrollPane;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(60);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
    }


    public static void showDialog(Component parent, int studentId) {
        Window parentWindow = (parent != null) ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(parentWindow, "My Timetable", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.add(new StudentTimetable(studentId));
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
