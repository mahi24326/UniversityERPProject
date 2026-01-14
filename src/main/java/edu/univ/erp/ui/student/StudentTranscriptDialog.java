package edu.univ.erp.ui.student;

import edu.univ.erp.util.StudentTranscriptExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StudentTranscriptDialog {

    public static void showDialog(Component parent, int studentId) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Transcript CSV");
        chooser.setSelectedFile(new File("transcript_student_" + studentId + ".csv"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try {
            StudentTranscriptExporter.exportCSV(file, studentId);
            JOptionPane.showMessageDialog(parent, "Transcript CSV exported successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Error exporting transcript: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
