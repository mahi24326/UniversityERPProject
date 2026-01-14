package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.domain.AdminSystemStats;
import edu.univ.erp.access.MaintenanceAccess;
import edu.univ.erp.service.AdminService;

public class DashboardAdmin extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(20, 184, 166);      // Teal
    private static final Color SECONDARY_COLOR = new Color(251, 146, 60);    // Orange
    private static final Color ACCENT_COLOR = new Color(168, 85, 247);       // Purple
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);       // Green
    private static final Color WARNING_COLOR = new Color(245, 158, 11);      // Amber
    private static final Color DANGER_COLOR = new Color(239, 68, 68);        // Red
    private static final Color BG_COLOR = new Color(249, 250, 251);          // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);         // Dark gray
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);    // Medium gray
    private static final Color BORDER_COLOR = new Color(229, 231, 235);      // Light border
    private static final Color SIDEBAR_BG = new Color(38, 38, 35);           // Dark sidebar
    private static final Color SIDEBAR_TEXT = new Color(229, 231, 235);      // Light text for sidebar
    private static final Color SIDEBAR_HOVER = new Color(55, 55, 52);        // Hover state

    private final AdminSystemStats stats;
    private boolean maintenanceMode;
    private String currentUsername;

    private JLabel maintenanceBanner;
    private JPanel statsSection;
    private JPanel quickActionsPanel;
    private boolean newMode = !MaintenanceAccess.getInstance().isMaintenanceMode();

    public DashboardAdmin(String adminUsername, AdminSystemStats stats) {
        this.currentUsername = adminUsername;
        this.stats = stats;
        this.maintenanceMode = newMode;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        initializeUI(adminUsername);
    }

    private void initializeUI(String username) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        maintenanceBanner = createMaintenanceBanner();
        maintenanceBanner.setVisible(newMode);
        mainPanel.add(maintenanceBanner, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        contentPanel.add(createHeaderPanel(username), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(createSidebar(), BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

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

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(SIDEBAR_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("System Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(156, 163, 175));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerSection.add(titleLabel);
        headerSection.add(Box.createVerticalStrut(4));
        headerSection.add(subtitleLabel);

        navPanel.add(headerSection);

        // Navigation buttons
        String[] options = {"Dashboard", "Users", "Courses","Settings"};
        for (String text : options) {
            navPanel.add(createSidebarButton(text, e -> handleSidebarAction(text)));
            navPanel.add(Box.createVerticalStrut(8));
        }

        navPanel.add(Box.createVerticalGlue());

        sidebar.add(navPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createSidebarButton(String text, ActionListener action) {
        JPanel buttonPanel = new JPanel(new BorderLayout(12, 0));
        buttonPanel.setBackground(SIDEBAR_BG);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Accent indicator (hidden by default, could be shown for active state)
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 30));
        indicator.setBackground(PRIMARY_COLOR);
        indicator.setVisible(false); // Can be toggled for active menu item

        // Text label
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(SIDEBAR_TEXT);

        buttonPanel.add(indicator, BorderLayout.WEST);
        buttonPanel.add(label, BorderLayout.CENTER);

        // Hover effect
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(SIDEBAR_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(buttonPanel, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return buttonPanel;
    }

    private void handleSidebarAction(String action) {
        switch (action) {
            case "Dashboard": refreshData(); break;
            case "Users": openUserManagement(); break;
            case "Courses": openCourseManagement(); break;
            case "Settings": openSystemAdmin(); break;
        }
    }

    private JLabel createMaintenanceBanner() {
        JLabel banner = new JLabel("MAINTENANCE MODE ACTIVE");
        banner.setOpaque(true);
        banner.setBackground(WARNING_COLOR);
        banner.setForeground(Color.WHITE);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        banner.setHorizontalAlignment(SwingConstants.CENTER);
        banner.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return banner;
    }

    private JPanel createHeaderPanel(String username) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        leftPanel.setBackground(BG_COLOR);

        JLabel welcomeLabel = new JLabel("Welcome back, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        JLabel deptLabel = new JLabel("System Administrator");
        deptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deptLabel.setForeground(TEXT_SECONDARY);

        JLabel semesterLabel = new JLabel("Manage your university system");
        semesterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        semesterLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(welcomeLabel);
        leftPanel.add(deptLabel);
        leftPanel.add(semesterLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(BG_COLOR);

        JButton profileBtn = createIconButton("Profile", PRIMARY_COLOR);
        profileBtn.addActionListener(e -> openProfile());

        JButton logoutBtn = createIconButton("Logout", DANGER_COLOR);
        logoutBtn.addActionListener(e -> handleLogout());

        rightPanel.add(profileBtn);
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

        panel.add(createStatsSection());
        panel.add(Box.createVerticalStrut(25));
        panel.add(createQuickActionsSection());
        panel.add(Box.createVerticalStrut(25));
        panel.add(createManagementSection());
        panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private JPanel createStatsSection() {
        statsSection = new JPanel(new GridLayout(1, 4, 20, 0));
        statsSection.setBackground(BG_COLOR);
        statsSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        statsSection.add(createStatCard("Total Students", String.valueOf(stats.getTotalStudents()), SUCCESS_COLOR, "Enrolled"));
        statsSection.add(createStatCard("Instructors", String.valueOf(stats.getTotalInstructors()), PRIMARY_COLOR, "Active"));
        statsSection.add(createStatCard("Courses", String.valueOf(stats.getTotalCourses()), ACCENT_COLOR, "Available"));
        statsSection.add(createStatCard("Sections", String.valueOf(stats.getTotalSections()), SECONDARY_COLOR, "This semester"));

        return statsSection;
    }

    private JPanel createStatCard(String title, String value, Color color, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel createQuickActionsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BG_COLOR);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        quickActionsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        quickActionsPanel.setBackground(BG_COLOR);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        updateQuickActionsPanel();

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(quickActionsPanel, BorderLayout.CENTER);

        return section;
    }

    private void updateQuickActionsPanel() {
        quickActionsPanel.removeAll();

        quickActionsPanel.add(createActionCard("Add Student", "Create new student", PRIMARY_COLOR, e -> openUserManagement()));
        quickActionsPanel.add(createActionCard("Add Instructor", "Create instructor", PRIMARY_COLOR, e -> openUserManagement()));
        quickActionsPanel.add(createActionCard("New Course", "Add course", SUCCESS_COLOR, e -> openCourseManagement()));
        quickActionsPanel.add(createActionCard("New Section", "Create section", SUCCESS_COLOR, e -> openCourseManagement()));
        quickActionsPanel.add(createActionCard("Settings", "System settings", TEXT_SECONDARY, e -> openSystemAdmin()));

        quickActionsPanel.add(createActionCard(
                maintenanceMode ? "Disable Maintenance" : "Maintenance Mode",
                maintenanceMode ? "Turn off" : "Turn on",
                maintenanceMode ? SUCCESS_COLOR : WARNING_COLOR,
                e -> toggleMaintenanceMode()
        ));

        quickActionsPanel.revalidate();
        quickActionsPanel.repaint();
    }

    private JPanel createActionCard(String title, String desc, Color color, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_COLOR);
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return card;
    }

    private JPanel createManagementSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BG_COLOR);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        JLabel titleLabel = new JLabel("Management Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(BG_COLOR);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        cardsPanel.add(createManagementCard(
                "User Management",
                "Manage all users",
                new String[]{"View users", "Add users", "Edit profiles", "Reset passwords"},
                PRIMARY_COLOR,
                e -> openUserManagement()
        ));

        cardsPanel.add(createManagementCard(
                "Course & Section Management",
                "Manage courses and sections",
                new String[]{"Create courses", "Manage sections", "Assign instructors", "Enrollments"},
                ACCENT_COLOR,
                e -> openCourseManagement()
        ));

        cardsPanel.add(createManagementCard(
                "System Administration",
                "System tools",
                new String[]{"Maintenance mode", "Backup DB", "System settings", "Logs"},
                TEXT_SECONDARY,
                e -> openSystemAdmin()
        ));

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(cardsPanel, BorderLayout.CENTER);

        return section;
    }

    private JPanel createManagementCard(String title, String desc, String[] items, Color color, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(15));

        for (String feature : items) {
            JLabel featureLabel = new JLabel("• " + feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            featureLabel.setForeground(TEXT_SECONDARY);
            featureLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(featureLabel);
            card.add(Box.createVerticalStrut(5));
        }

        JButton button = createModernButton("Open", color, color.darker());
        button.addActionListener(action);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createVerticalStrut(15));
        card.add(button);

        return card;
    }

    private JButton createIconButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

    private JButton createModernButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 36));

        return button;
    }

    private void openAddInstructor() { JOptionPane.showMessageDialog(this, "Add Instructor - Coming Soon"); }
    private void openAddSection() { JOptionPane.showMessageDialog(this, "Add Section - Coming Soon"); }
    private void openAddCourse() { JOptionPane.showMessageDialog(this, "Add Course - Coming Soon"); }
    private void openAddStudent() { JOptionPane.showMessageDialog(this, "Add Student - Coming Soon"); }

    private void openUserManagement() {
        UserManagement.showDialog(this);
    }

    private void openCourseManagement() {
        CourseManagement.showDialog(this);
    }

    private void openSettings() {
        JOptionPane.showMessageDialog(this, "Settings - Coming Soon");
    }

    private void openSystemAdmin() {
        showSystemAdminTools();
    }

    private void showSystemAdminTools() {
        Object[] options = {"Backup Database", "Restore Database", "Cancel"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose an action:",
                "System Administration",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            edu.univ.erp.service.DatabaseBackupService.backupDatabase();
        }
        else if (choice == 1) {
            edu.univ.erp.service.DatabaseRestoreService.restoreDatabase();
        }
    }

    private void toggleMaintenanceMode() {
        boolean current = MaintenanceAccess.getInstance().isMaintenanceMode();
        boolean newMode = !current;

        AdminService service = new AdminService();
        service.setMaintenanceMode(newMode);

        MaintenanceAccess.getInstance().setMaintenanceMode(newMode);

        this.maintenanceMode = newMode;
        maintenanceBanner.setVisible(newMode);

        MainFrame.getInstance().refreshAdminDashboard();
    }

    private void openProfile() {
        JOptionPane.showMessageDialog(this, "Profile - Coming Soon");
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            MainFrame.getInstance().logout();
        }
    }

    public void refreshData() {
        MainFrame.getInstance().refreshAdminDashboard();
    }
}