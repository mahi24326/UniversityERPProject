package edu.univ.erp.ui.student;

import edu.univ.erp.util.SectionsDAO;
import edu.univ.erp.domain.Section;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SectionsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtCourseId, txtInstructorId, txtDay, txtTime, txtRoom, txtCapacity, txtYear;
    private JComboBox<String> cmbSemester;

    private SectionsDAO dao = new SectionsDAO();

    public SectionsPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "ID","Course","Instructor","Day","Time","Room","Capacity","Semester","Year","Semester Start"
        }, 0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0,2,5,5));

        txtCourseId = new JTextField();
        txtInstructorId = new JTextField();
        txtDay = new JTextField();
        txtTime = new JTextField();
        txtRoom = new JTextField();
        txtCapacity = new JTextField();
        txtYear = new JTextField();
        cmbSemester = new JComboBox<>(new String[]{"Winter","Summer","Monsoon"});

        form.add(new JLabel("Course ID"));        form.add(txtCourseId);
        form.add(new JLabel("Instructor ID"));    form.add(txtInstructorId);
        form.add(new JLabel("Day"));              form.add(txtDay);
        form.add(new JLabel("Time"));             form.add(txtTime);
        form.add(new JLabel("Room"));             form.add(txtRoom);
        form.add(new JLabel("Capacity"));         form.add(txtCapacity);
        form.add(new JLabel("Semester"));         form.add(cmbSemester);
        form.add(new JLabel("Year"));             form.add(txtYear);

        add(form, BorderLayout.NORTH);

        JButton btnAdd = new JButton("Add Section");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        JPanel actions = new JPanel();
        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);

        add(actions, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addSection());
        btnUpdate.addActionListener(e -> updateSection());
        btnDelete.addActionListener(e -> deleteSection());

        loadSections();
    }

    private void loadSections() {
        model.setRowCount(0);
        List<Section> list = dao.getAllSections();

        for (Section s : list) {
            model.addRow(new Object[]{
                    s.getSectionId(),
                    s.getCourseId(),
                    s.getInstructorId(),
                    s.getDay(),
                    s.getTime(),
                    s.getRoom(),
                    s.getCapacity(),
                    s.getSemester(),
                    s.getYear(),
                    s.getSemesterStart()
            });
        }
    }

    private Section buildSectionFromForm(boolean includeId) {
        Section s = new Section();

        if (includeId) {
            int row = table.getSelectedRow();
            if (row == -1) return null;
            s.setSectionId((int) model.getValueAt(row, 0));
        }

        s.setCourseId(Integer.parseInt(txtCourseId.getText()));
        s.setInstructorId(Integer.parseInt(txtInstructorId.getText()));
        s.setDay(txtDay.getText());
        s.setTime(txtTime.getText());
        s.setRoom(txtRoom.getText());
        s.setCapacity(Integer.parseInt(txtCapacity.getText()));
        s.setSemester((String) cmbSemester.getSelectedItem());
        s.setYear(Integer.parseInt(txtYear.getText()));

        return s;
    }

    private void addSection() {
        Section s = buildSectionFromForm(false);
        if (s == null) return;

        if (dao.insertSection(s)) {
            loadSections();
        }
    }

    private void updateSection() {
        Section s = buildSectionFromForm(true);
        if (s == null) return;

        if (dao.updateSection(s)) {
            loadSections();
        }
    }

    private void deleteSection() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);
        if (dao.deleteSection(id)) {
            loadSections();
        }
    }
}