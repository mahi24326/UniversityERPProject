package edu.univ.erp.service;

import edu.univ.erp.util.ConfigReader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class DatabaseBackupService {

    private static final String USER = ConfigReader.get("db.user");
    private static final String PASS = ConfigReader.get("db.password");

    public static void backupDatabase() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save ERP Backup As");
        fileChooser.setSelectedFile(new File("erp_backup.sql"));

        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

        File backupFile = fileChooser.getSelectedFile();

        JFileChooser binChooser = new JFileChooser();
        binChooser.setDialogTitle("Locate mysqldump.exe");
        binChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (binChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        File mysqldump = binChooser.getSelectedFile();

        if (!mysqldump.exists()) {
            JOptionPane.showMessageDialog(null, "mysqldump.exe not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    mysqldump.getAbsolutePath(),
                    "-u" + USER,
                    "-p" + PASS,
                    "--databases",
                    "erp_db",
                    "auth_db"
            );

            pb.redirectOutput(backupFile);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            int code = process.waitFor();

            if (code == 0) {
                JOptionPane.showMessageDialog(null,
                        "Backup successful:\n" + backupFile.getAbsolutePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Backup failed (exit code " + code + ")",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Backup error:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
