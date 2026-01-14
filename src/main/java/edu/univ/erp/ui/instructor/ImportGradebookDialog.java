package edu.univ.erp.ui.instructor;

import edu.univ.erp.service.AssessmentService;
import edu.univ.erp.util.CSVImporter;
import edu.univ.erp.domain.SectionItem;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ImportGradebookDialog extends JDialog {

    private final int instructorId;
    private final AssessmentService service = new AssessmentService();

    private JComboBox<SectionItem> sectionDropdown;
    private JButton importBtn;

    public ImportGradebookDialog(Window parent, int instructorId) {
        super(parent, "Import Gradebook CSV", ModalityType.APPLICATION_MODAL);
        this.instructorId = instructorId;

        setLayout(new BorderLayout(15, 15));
        setSize(450, 220);
        setLocationRelativeTo(parent);

        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadSections();
    }


    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Select Section to Import Into:"));

        sectionDropdown = new JComboBox<>();
        panel.add(sectionDropdown);

        return panel;
    }


    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        importBtn = new JButton("Import CSV");
        importBtn.addActionListener(e -> importCSV());

        panel.add(importBtn);
        return panel;
    }


    /** Load instructor’s sections */
    private void loadSections() {
        try {
            List<SectionItem> list = service.getSectionsForInstructor(instructorId);
            for (SectionItem s : list)
                sectionDropdown.addItem(s);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage());
        }
    }


    /** Import CSV using new strict importer */
    private void importCSV() {

        SectionItem selected = (SectionItem) sectionDropdown.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No section selected.");
            return;
        }

        // COURSE ID IS NEEDED FOR VALIDATION
        int sectionId = selected.sectionId;
        int courseId = selected.courseId;   // NOTE: SectionItem MUST include this

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Gradebook CSV");
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File csvFile = chooser.getSelectedFile();

        try {
            // NEW IMPORT CALL
            CSVImporter.importCSV(csvFile, sectionId, courseId);

            JOptionPane.showMessageDialog(this,
                    "CSV imported successfully!");

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error importing CSV:\n" + e.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
