package edu.univ.erp.ui.student;

import edu.univ.erp.access.MaintenanceAccess;
import edu.univ.erp.domain.Student;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardStudent extends JPanel {

    // --- COLOR SCHEME (Adopted instructor's aesthetic for consistency) ---
    private static final Color PRIMARY_COLOR = new Color(20, 184, 166);      // Teal
    private static final Color SECONDARY_COLOR = new Color(251, 146, 60);    // Orange
    private static final Color ACCENT_COLOR = new Color(168, 85, 247);       // Purple
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Green
    private static final Color WARNING_COLOR = new Color(245, 158, 11);      // Amber
    private static final Color DANGER_COLOR = new Color(239, 68, 68);        // Red
    private static final Color BG_COLOR = new Color(249, 250, 251);          // Light gray
    private static final Color CARD_COLOR = Color.WHITE;                     // Generic card background
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);         // Dark gray
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);    // Medium gray
    private static final Color BORDER_COLOR = new Color(229, 231, 235);      // Light border
    private static final Color SIDEBAR_BG = new Color(38, 38, 35);           // Dark sidebar
    private static final Color SIDEBAR_TEXT = new Color(229, 231, 235);      // Light text for sidebar
    private static final Color SIDEBAR_HOVER = new Color(55, 55, 52);        // Hover state

    // Existing Student card colors (kept for main content cards)
    private static final Color CARD_COLOR1 = new Color(73, 129, 145);
    private static final Color CARD_COLOR2 = new Color(105, 137, 146);
    private static final Color CARD_COLOR3 = new Color(137, 159, 166);
    private static final Color CARD_COLOR4 = new Color(177, 193, 198);
    private static final Color CARD_COLOR5 = new Color(207, 222, 226);
    private static final Color CARD_COLOR6 = new Color(228, 247, 253);


    private Student student;
    private JLabel maintenanceBanner;

    public DashboardStudent(Student student) {
        this.student = student;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR); // Use new BG_COLOR
        setOpaque(true);

        maintenanceBanner = createMaintenanceBanner();
        maintenanceBanner.setVisible(!MaintenanceAccess.getInstance().isMaintenanceMode());
        add(maintenanceBanner, BorderLayout.NORTH);

        // Sidebar with navigation - NEW
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- SIDEBAR IMPLEMENTATION (Functionality preserved) ---
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SIDEBAR_BG);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Sidebar header
        JPanel headerSection = new JPanel();
        headerSection.setLayout(new BoxLayout(headerSection, BoxLayout.Y_AXIS));
        headerSection.setBackground(SIDEBAR_BG);
        headerSection.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));
        headerSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(SIDEBAR_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Access core functions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(156, 163, 175));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerSection.add(titleLabel);
        headerSection.add(Box.createVerticalStrut(4));
        headerSection.add(subtitleLabel);

        navPanel.add(headerSection);

        // Quick Actions
        navPanel.add(createSidebarButton("Browse Courses", "View course catalog", PRIMARY_COLOR, e -> browseCourses()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("Register/Drop", "Add or remove courses", SECONDARY_COLOR, e -> registerCourses()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("My Timetable", "View class schedule", ACCENT_COLOR, e -> viewTimetable()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("My Grades", "Check performance", WARNING_COLOR, e -> viewGrades()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("Transcript", "Download academic record", new Color(107, 114, 128), e -> downloadTranscript()));

        navPanel.add(Box.createVerticalGlue());

        sidebar.add(navPanel, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel createSidebarButton(String title, String description, Color accentColor, ActionListener action) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout(12, 0));
        buttonPanel.setBackground(SIDEBAR_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Accent color indicator
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 40));
        indicator.setBackground(accentColor);

        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(SIDEBAR_BG);
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(SIDEBAR_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(156, 163, 175));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(descLabel);

        buttonPanel.add(indicator, BorderLayout.WEST);
        buttonPanel.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(SIDEBAR_HOVER);
                textPanel.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(SIDEBAR_BG);
                textPanel.setBackground(SIDEBAR_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(buttonPanel, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return buttonPanel;
    }

    private JLabel createMaintenanceBanner() {
        JLabel banner = new JLabel("⚠ MAINTENANCE MODE - You can view data but cannot register or drop courses");
        banner.setOpaque(true);
        banner.setBackground(WARNING_COLOR);
        banner.setForeground(Color.WHITE);
        banner.setFont(new Font("Arial", Font.BOLD, 14));
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return banner;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        leftPanel.setBackground(BG_COLOR);

        JLabel welcomeLabel = new JLabel("Welcome back, " + student.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        JLabel rollLabel = new JLabel("Roll Number: " + student.getRollNumber() + " • " + student.getProgram());
        rollLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rollLabel.setForeground(TEXT_SECONDARY);

        JLabel yearLabel = new JLabel("Year " + student.getYear());
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        yearLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(welcomeLabel);
        leftPanel.add(rollLabel);
        leftPanel.add(yearLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(BG_COLOR);

        // --- CHANGE 3: Profile button removed ---
        // JButton profileBtn = createIconButton("👤 Profile", PRIMARY_COLOR);

        JButton logoutBtn = createIconButton("🚪 Logout", DANGER_COLOR);

        logoutBtn.addActionListener(e -> handleLogout());

        // rightPanel.add(profileBtn);
        rightPanel.add(logoutBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);

        panel.add(createAcademicOverview());
        panel.add(Box.createVerticalStrut(25));

        panel.add(createMyCoursesSection());
        panel.add(Box.createVerticalStrut(25));

        // The Upcoming Classes section was implicitly removed in the previous prompt.
        // I will re-add the vertical strut if the intent was to keep it, but based
        // on the provided template, I'll stick to the current layout which ends here.

        // If createUpcomingClassesSection() existed in the original code but was
        // omitted from the provided template, it would be added here:
        // panel.add(createUpcomingClassesSection());
        // panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private JPanel createAcademicOverview() {
        // --- CHANGE 1: Current GPA card removed ---
        // Changed GridLayout from 1, 4 to 1, 3
        JPanel section = new JPanel(new GridLayout(1, 3, 20, 0));
        section.setBackground(BG_COLOR);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Card 1: Enrolled Courses
        section.add(createStatCard("📚 Enrolled Courses",
                String.valueOf(student.getEnrolledCourses()),
                new Color(59, 130, 246), CARD_COLOR1, "This semester"));

        // Card 2: Total Credits (Original Card 3)
        section.add(createStatCard("📊 Total Credits",
                String.valueOf(student.getTotalCredits()),
                new Color(139, 92, 246), CARD_COLOR3, "This semester"));

        // Card 3: Progress (Original Card 4)
        section.add(createStatCard("📈 Progress",
                "40%", new Color(245, 158, 11), CARD_COLOR4, "Semester completion"));

        // Removed Card 2: Current GPA

        return section;
    }

    private JPanel createStatCard(String title, String value, Color accentColor, Color CardColor, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);

        return card;
    }

    // ... (rest of the class code above)

    private JPanel createMyCoursesSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BG_COLOR);
        // Set maximum height to accommodate only the large button
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // --- CHANGE 2: Replace big box/mini-panel with a single large button ---

        JButton bigCourseButton = new JButton("View All Enrolled Courses");
        bigCourseButton.setFont(new Font("Arial", Font.BOLD, 18));

        // --- UI CHANGE: Text color to PRIMARY_COLOR (Teal) ---
        bigCourseButton.setForeground(PRIMARY_COLOR);

        // --- UI CHANGE: Background color to CARD_COLOR6 (Light Blue/White) ---
        bigCourseButton.setBackground(CARD_COLOR6);

        bigCourseButton.setFocusPainted(false);
        bigCourseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bigCourseButton.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // Use the same action listener as the original "View All" button
        bigCourseButton.addActionListener(e -> viewAllCourses());

        // Add hover effect for style
        // Changed hover color to a light border color for subtle effect
        Color hoverColor = BORDER_COLOR;
        bigCourseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bigCourseButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bigCourseButton.setBackground(CARD_COLOR6); // Return to light background
            }
        });

        // Add padding around the button
        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setBackground(BG_COLOR);
        buttonWrapper.add(bigCourseButton, BorderLayout.CENTER);

        section.add(buttonWrapper, BorderLayout.CENTER);

        return section;
    }

// ... (rest of the class code below)

    // The createUpcomingClassesSection() method was not included in the provided
    // template but would be here if needed to complete the UI.
    // private JPanel createUpcomingClassesSection() { ... }
    // private JPanel createClassCard() { ... }

    // ... remaining helper methods (createIconButton, createStyledButton, etc.) ...

    private void styleTable(JTable table) {
        // Unused in current dashboard layout, but kept for completeness
    }

    private JButton createIconButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 38));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 36));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public void updateMaintenanceBanner() {
        boolean active = !MaintenanceAccess.getInstance().isMaintenanceMode();
        maintenanceBanner.setVisible(active);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            MainFrame mainFrame = MainFrame.getInstance();
            mainFrame.logout();
        }
    }

    private void browseCourses() {
//        StudentCourses.showDialog(this, student.getStudentId(), !MaintenanceAccess.getInstance().isMaintenanceMode());
        StudentCourseBrowser.showDialog(this, student.getStudentId());
    }

    private void registerCourses() {
        // NOTE: Functionality remains the same, only the button location changed
        if (!MaintenanceAccess.getInstance().isMaintenanceMode()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot register/drop during maintenance mode",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        StudentCourses.showDialog(this, student.getStudentId(), MaintenanceAccess.getInstance().isMaintenanceMode());
    }

    private void viewTimetable() {
        StudentTimetable.showDialog(this, student.getStudentId());
    }

    private void viewGrades() {
        StudentGrades.showDialog(this, student.getStudentId());
    }

    // FUNCTIONALITY PRESERVED
    private void viewAllCourses() {
        EnrolledCourses.showDialog(this, student.getStudentId(), !MaintenanceAccess.getInstance().isMaintenanceMode());
    }

    private void downloadTranscript() {
        // Assuming this method exists elsewhere as per the original code's intent
        // JOptionPane.showMessageDialog(this, "Download Transcript - Coming soon!");
        StudentTranscriptDialog.showDialog(this, student.getStudentId());
    }

}