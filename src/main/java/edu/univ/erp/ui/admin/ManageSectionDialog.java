package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.SectionWithAvailability;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.db.DBUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class ManageSectionDialog extends JDialog {

    private JTable sectionTable;
    private DefaultTableModel tableModel;

    private int courseId;
    private String courseTitle;

    private final SectionService sectionService = new SectionService();

    public ManageSectionDialog(Window parent, int courseId, String courseTitle) {
        super(parent, "Manage Sections for: " + courseTitle, ModalityType.APPLICATION_MODAL);

        this.courseId = courseId;
        this.courseTitle = courseTitle;

        setSize(900, 550);
        setLocationRelativeTo(parent);

        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadSections();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lbl = new JLabel("Sections for: " + courseTitle);
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setForeground(new Color(0, 128, 128));

        panel.add(lbl, BorderLayout.WEST);
        return panel;
    }

    private JScrollPane createTablePanel() {

        tableModel = new DefaultTableModel(
                new String[]{
                        "Section ID", "Instructor", "Day", "Time",
                        "Room", "Capacity", "Enrolled", "Semester", "Year"
                }, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        sectionTable = new JTable(tableModel);
        sectionTable.setRowHeight(28);

        return new JScrollPane(sectionTable);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnAdd = new JButton("Add Section");
        JButton btnEdit = new JButton("Edit Section");
        JButton btnDelete = new JButton("Delete Section");
        JButton btnClose = new JButton("Close");

        btnAdd.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSection());
        btnClose.addActionListener(e -> dispose());

        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnClose);

        return panel;
    }

    private void loadSections() {
        tableModel.setRowCount(0);

        List<SectionWithAvailability> list =
                sectionService.getSectionsForAdmin(courseId);

        for (SectionWithAvailability s : list) {
            tableModel.addRow(new Object[]{
                    s.getsectionId(),
                    s.getinstructorName(),
                    s.getDay(),
                    s.getTime(),
                    s.getroom(),
                    s.getcapacity(),
                    s.getenrolled(),
                    s.getSemester(),
                    s.getYear()
            });
        }
    }


    private void openAddDialog() {
        AddSectionDialog dlg = new AddSectionDialog(this, courseId);
        dlg.setVisible(true);
        loadSections();
    }
    private void openEditDialog() {

        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a section to edit.");
            return;
        }

        int sectionId = (int) tableModel.getValueAt(row, 0);

        EditSectionDialog dlg = new EditSectionDialog(this, sectionId);
        dlg.setVisible(true);

        loadSections();
    }


    private void deleteSection() {

        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a section to delete.");
            return;
        }

        int sectionId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete section " + sectionId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBUtil.getERPConnection()) {
            // Check if enrolled students exist
            PreparedStatement check = conn.prepareStatement(
                    "SELECT COUNT(*) FROM enrollments WHERE section_id=? AND status='enrolled'"
            );
            check.setInt(1, sectionId);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete. Students currently enrolled.");
                return;
            }

            PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM sections WHERE section_id=?"
            );
            del.setInt(1, sectionId);
            del.executeUpdate();

            JOptionPane.showMessageDialog(this, "Section deleted.");
            loadSections();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

}
