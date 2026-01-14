package edu.univ.erp.ui.instructor;

import edu.univ.erp.service.AssessmentService;
import edu.univ.erp.util.CSVHelper;
import edu.univ.erp.domain.SectionItem;
import edu.univ.erp.domain.AssessmentComponent;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;


public class ExportGradebookDialog extends JDialog {

    private final int instructorId;
    private final AssessmentService service = new AssessmentService();

    private JComboBox<SectionItem> sectionDropdown;
    private JButton exportBtn;

    public ExportGradebookDialog(Window parent, int instructorId) {
        super(parent, "Export Gradebook", ModalityType.APPLICATION_MODAL);
        this.instructorId = instructorId;

        setLayout(new BorderLayout(15, 15));
        setSize(420, 200);
        setLocationRelativeTo(parent);

        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadSections();
    }


    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Select Section to Export:"));

        sectionDropdown = new JComboBox<>();
        panel.add(sectionDropdown);

        return panel;
    }


    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        exportBtn = new JButton("Export CSV");
        exportBtn.addActionListener(e -> exportCSV());

        panel.add(exportBtn);
        return panel;
    }


    private void loadSections() {
        try {
            // You can replace with your real query if needed
            List<SectionItem> list = service.getSectionsForInstructor(instructorId);

            for (SectionItem s : list)
                sectionDropdown.addItem(s);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage());
        }
    }


    private void exportCSV() {
        SectionItem selected = (SectionItem) sectionDropdown.getSelectedItem();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No section selected.");
            return;
        }

        try {
            List<String[]> rows = service.getGradebookRows(selected.sectionId);

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Gradebook CSV");
            chooser.setSelectedFile(new File("gradebook_section_" + selected.sectionId + ".csv"));

            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();

            // ==== dynamic header with components ====
            int courseId = selected.courseId;
            List<AssessmentComponent> comps = service.getComponentsForCourse(courseId);

            String[] header = new String[comps.size() + 2];
            int h = 0;
            header[h++] = "Student";

            for (AssessmentComponent c : comps)
                header[h++] = c.getName();

            header[h] = "Final Score";

            CSVHelper.writeCSV(file, header, rows);

            JOptionPane.showMessageDialog(this, "CSV exported successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage());
        }
    }

}
