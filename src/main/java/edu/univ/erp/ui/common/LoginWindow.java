package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.*;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.InstructorTeachingStats;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.AdminService;

public class LoginWindow extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    // === ADDED FOR LOCKOUT ===
    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 6;
    private JButton loginButton;
    // =========================

    public LoginWindow() {

        setLayout(new BorderLayout());
        setOpaque(true);

        ImageIcon bgImage = new ImageIcon("src/main/java/edu/univ/erp/ui/common/background3.jpg");

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = bgImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setPreferredSize(new Dimension(400, 600));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Login", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(new Color(73, 129, 145));
        gbc.insets = new Insets(10, 0, 5, 0);
        loginPanel.add(title, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 35, 0);

        JLabel subtitl = new JLabel("Welcome to University ERP", SwingConstants.LEFT);
        subtitl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        subtitl.setForeground(new Color(59,69,70,180));
        loginPanel.add(subtitl, gbc);

        gbc.insets = new Insets(5, 0, 15, 0);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground((new Color(59,69,70)));
        loginPanel.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        styleInput(usernameField);
        loginPanel.add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passLabel.setForeground((new Color(59,69,70)));
        loginPanel.add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        styleInput(passwordField);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 0, 10, 0);

        // === loginButton made accessible ===
        loginButton = new JButton("Login");
        // Removed conflicting FlatLaf styling that might force it to white
        styleButton(loginButton);
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> authenticateUser());
        passwordField.addActionListener(e -> authenticateUser());

        gbc.gridy++;
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(200, 40, 40));
        loginPanel.add(messageLabel, gbc);

        GridBagConstraints rightAlign = new GridBagConstraints();
        rightAlign.gridx = 1;
        rightAlign.gridy = 0;
        rightAlign.weightx = 1;
        rightAlign.weighty = 1;
        rightAlign.anchor = GridBagConstraints.EAST;
        rightAlign.insets = new Insets(0, 0, 0, 50);

        backgroundPanel.add(loginPanel, rightAlign);
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    // Styles the login button (Fixed for visibility)
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);

        // White Background with Teal Text (Inverted to ensure readability)
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(73, 129, 145));

        // Thick colored border to define the button shape
        btn.setBorder(BorderFactory.createCompoundBorder(
                // 1. OUTER BORDER (The Visible Teal Line)
                BorderFactory.createLineBorder(new Color(73, 129, 145), 2),

                // 2. INNER BORDER (The Invisible Padding)
                // This adds 10px of empty space on Top and Bottom to make the button taller
                BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));


        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
    }

    private void authenticateUser() {

        String username = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());

        if (username.isEmpty() || enteredPassword.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }

        // Already locked out
        if (failedAttempts >= MAX_ATTEMPTS) {
            messageLabel.setText("Too many failed attempts. Login disabled.");
            loginButton.setEnabled(false);
            return;
        }

        AuthService auth = new AuthService();
        AuthService.AuthResult result = auth.authenticate(username, enteredPassword);


        if ("User not found".equalsIgnoreCase(result.errorMessage)) {
            messageLabel.setText("Username not found");
            return;
        }

        if (!result.success) {

            failedAttempts++;

            if (failedAttempts >= MAX_ATTEMPTS) {
                messageLabel.setText("Too many failed attempts. Login disabled.");
                loginButton.setEnabled(false);
            } else {
                messageLabel.setText(
                        "Incorrect password. Attempts left: " + (MAX_ATTEMPTS - failedAttempts)
                );
            }

            return;
        }

        failedAttempts = 0;

        MainFrame main = MainFrame.getInstance();
        main.setCurrentUser(username, result.role);

        try {
            switch (result.role.toLowerCase()) {

                case "admin":
                    AdminService a_service = new AdminService();
                    AdminSystemStats a_stats = a_service.loadSystemStats();
                    main.showAdminDashboard(username, a_stats);
                    break;

                case "instructor":
                    InstructorRepository instructorRepo = new InstructorRepository();
                    Instructor instructor = instructorRepo.findByUserId(result.userId);

                    if (instructor != null) {
                        InstructorService i_service = new InstructorService();
                        InstructorTeachingStats i_stats =
                                i_service.getTeachingStats(instructor.getInstructorId());
                        main.showInstructorDashboard(instructor, i_stats);
                    } else {
                        messageLabel.setText("Error loading instructor profile");
                    }
                    break;

                case "student":
                    StudentRepository studentRepo = new StudentRepository();
                    Student student = studentRepo.findByUserId(result.userId);

                    if (student != null) {
                        main.showStudentDashboard(student);
                    } else {
                        messageLabel.setText("Error loading student profile");
                    }
                    break;

                default:
                    messageLabel.setText("Unknown role: " + result.role);
            }

        } catch (Exception e) {
            messageLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
