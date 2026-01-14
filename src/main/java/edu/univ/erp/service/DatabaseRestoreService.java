package edu.univ.erp.service;

import edu.univ.erp.util.ConfigReader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class DatabaseRestoreService {

    private static final String USER = ConfigReader.get("db.user");
    private static final String PASS = ConfigReader.get("db.password");

    public static void restoreDatabase() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select ERP Backup File");

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        File backupFile = fileChooser.getSelectedFile();

        JFileChooser binChooser = new JFileChooser();
        binChooser.setDialogTitle("Locate mysql.exe");
        binChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (binChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        File mysql = binChooser.getSelectedFile();

        if (!mysql.exists()) {
            JOptionPane.showMessageDialog(null, "mysql.exe not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    mysql.getAbsolutePath(),
                    "-u" + USER,
                    "-p" + PASS
            );

            pb.redirectInput(backupFile);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            int code = process.waitFor();

            if (code == 0) {
                JOptionPane.showMessageDialog(null,
                        "Restore completed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Restore failed (exit code " + code + ")",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Restore error:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
