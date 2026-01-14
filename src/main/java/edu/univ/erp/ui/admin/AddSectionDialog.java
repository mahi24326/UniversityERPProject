package edu.univ.erp.ui.admin;

import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.db.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class AddSectionDialog extends JDialog {

    private JTextField txtDay;
    private JTextField txtTime;
    private JTextField txtRoom;
    private JTextField txtCapacity;
    private JComboBox<String> cmbInstructor;
    private JComboBox<String> cmbSemester;
    private JTextField txtYear;

    private int courseId;

    private final SectionRepository repo = new SectionRepository();

    public AddSectionDialog(Window parent, int courseId) {
        super(parent, "Add Section", ModalityType.APPLICATION_MODAL);
        this.courseId = courseId;

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(createForm(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        loadInstructors();
    }

    private JPanel createForm() {
        JPanel p = new JPanel(new GridLayout(7, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtDay = new JTextField();
        txtTime = new JTextField();
        txtRoom = new JTextField();
        txtCapacity = new JTextField();
        txtYear = new JTextField();

        cmbSemester = new JComboBox<>(new String[]{"Summer", "Monsoon", "Winter"});
        cmbInstructor = new JComboBox<>();

        p.add(new JLabel("Day:"));
        p.add(txtDay);
        p.add(new JLabel("Time:"));
        p.add(txtTime);
        p.add(new JLabel("Room:"));
        p.add(txtRoom);
        p.add(new JLabel("Capacity:"));
        p.add(txtCapacity);
        p.add(new JLabel("Semester:"));
        p.add(cmbSemester);
        p.add(new JLabel("Year:"));
        p.add(txtYear);
        p.add(new JLabel("Instructor:"));
        p.add(cmbInstructor);

        return p;
    }

    private JPanel createButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnSave);
        p.add(btnCancel);
        return p;
    }

    private void loadInstructors() {
        try (Connection conn = DBUtil.getERPConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("""
                     SELECT i.instructor_id, u.username
                     FROM instructors i
                     JOIN auth_db.users u ON i.user_id = u.user_id
                     ORDER BY u.username
                     """)) {

            while (rs.next()) {
                cmbInstructor.addItem(rs.getInt("instructor_id") + " - " + rs.getString("username"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading instructors: " + e.getMessage());
        }
    }

    private void save() {
        try {
            int instructorId = Integer.parseInt(
                    cmbInstructor.getSelectedItem().toString().split(" - ")[0]
            );
            String day = txtDay.getText().trim();
            String time = txtTime.getText().trim();
            String room = txtRoom.getText().trim();
            int capacity = Integer.parseInt(txtCapacity.getText().trim());
            String semester = cmbSemester.getSelectedItem().toString();
            int year = Integer.parseInt(txtYear.getText().trim());

            String res = repo.addSection(courseId, instructorId, day, time, room, capacity, semester, year);

            if (res.equals("success")) {
                JOptionPane.showMessageDialog(this, "Section added successfully");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
        }
    }
}
