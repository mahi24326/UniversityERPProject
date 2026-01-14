package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.InstructorTeachingStats;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import edu.univ.erp.access.MaintenanceAccess;


/**
 * Instructor Dashboard Panel - Can be embedded in any JFrame
 * Features: My sections, grade entry, class statistics, grade management
 */
public class DashboardInstructor extends JPanel {

    // Color scheme - Modern teal and orange palette
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

    // Sample instructor data (replace with real DB queries)
    private Instructor instructor;
    private InstructorTeachingStats stats;

    private JLabel maintenanceBanner;

    public DashboardInstructor(Instructor instructor, InstructorTeachingStats stats) {
        this.instructor = instructor;
        this.stats = stats;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setOpaque(true);

        // Top bar with maintenance banner (hidden by default)
        maintenanceBanner = createMaintenanceBanner();
        maintenanceBanner.setVisible(!MaintenanceAccess.getInstance().isMaintenanceMode());
        add(maintenanceBanner, BorderLayout.NORTH);

        // Sidebar with navigation
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content area with padding
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Header section
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Main content area - scrollable
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(SIDEBAR_TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage your courses");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(156, 163, 175));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerSection.add(titleLabel);
        headerSection.add(Box.createVerticalStrut(4));
        headerSection.add(subtitleLabel);

        navPanel.add(headerSection);

        // Navigation buttons
        navPanel.add(createSidebarButton("Enter Grades", "Add assessment scores", PRIMARY_COLOR, e -> enterGrades()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("Class Stats", "View analytics", ACCENT_COLOR, e -> viewStatistics()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("Import CSV", "Bulk grade upload", SUCCESS_COLOR, e -> importGrades()));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(createSidebarButton("Export Grades", "Download grade sheet", new Color(107, 114, 128), e -> exportGrades()));

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
        JLabel banner = new JLabel("MAINTENANCE MODE - You can view data but cannot enter or modify grades");
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

        // Left side - Welcome message
        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        leftPanel.setBackground(BG_COLOR);

        JLabel welcomeLabel = new JLabel("Welcome, " + instructor.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        JLabel deptLabel = new JLabel(instructor.getDepartment() + " Department");
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        deptLabel.setForeground(TEXT_SECONDARY);

        JLabel semesterLabel = new JLabel(stats.getTotalSections() + " Active Sections");
        semesterLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        semesterLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(welcomeLabel);
        leftPanel.add(deptLabel);
        leftPanel.add(semesterLabel);

        // Right side - Profile and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(BG_COLOR);

        JButton profileBtn = createIconButton("Profile", PRIMARY_COLOR);
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

        // Teaching overview cards
        panel.add(createTeachingOverview());
        panel.add(Box.createVerticalStrut(25));

        // My sections table
        panel.add(createMySectionsSection());
        panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private JPanel createTeachingOverview() {
        JPanel section = new JPanel(new GridLayout(1, 4, 20, 0));
        section.setBackground(BG_COLOR);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        section.add(createStatCard("My Sections", String.valueOf(stats.getTotalSections()), PRIMARY_COLOR, "This semester"));
        section.add(createStatCard("Total Students", String.valueOf(stats.getTotalStudents()), SECONDARY_COLOR, "Across all sections"));
        section.add(createStatCard("Avg. Class Size", String.format("%.0f", stats.getAvgClassSize()), ACCENT_COLOR, "Students per section"));
        section.add(createStatCard("Pending Grades", String.valueOf(stats.getPendingGrades()), WARNING_COLOR, "Need attention"));

        return section;
    }

    private JPanel createStatCard(String title, String value, Color accentColor, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
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

    private JPanel createMySectionsSection() {
        InstructorSectionsPanel panel = new InstructorSectionsPanel(instructor);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);
        wrapper.add(panel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createActivityCard(String title, String description, Color accentColor, String time) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Left indicator
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 40));
        indicator.setBackground(accentColor);

        // Center content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(descLabel);

        // Right time
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        timeLabel.setForeground(TEXT_SECONDARY);

        card.add(indicator, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(timeLabel, BorderLayout.EAST);

        return card;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(204, 251, 241));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(90);
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
        button.setPreferredSize(new Dimension(130, 36));

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

    private void exportGrades() {
        new ExportGradebookDialog(
                SwingUtilities.getWindowAncestor(this),
                instructor.getInstructorId()
        ).setVisible(true);
    }


    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            MainFrame mainFrame = MainFrame.getInstance();
            mainFrame.logout();  // This calls the logout() method in MainFrame
        }
    }

    // Action handlers (placeholders - implement later)
    private void enterGrades() {
        if (MaintenanceAccess.getInstance().canModifyData()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot enter grades during maintenance mode",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Grade Entry - Coming soon!");
    }



    private void viewStatistics() {
        JOptionPane.showMessageDialog(this, "Class Statistics - Coming soon!");
    }

    private void importGrades() {
        if (MaintenanceAccess.getInstance().canModifyData()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot import grades during maintenance mode",
                    "Maintenance Mode",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        new ImportGradebookDialog(
                SwingUtilities.getWindowAncestor(this),
                instructor.getInstructorId()
        ).setVisible(true);
    }


    private void manageSections() {
        JOptionPane.showMessageDialog(this, "Manage All Sections - Coming soon!");
    }

    public void updateMaintenanceBanner() {
        boolean active = !MaintenanceAccess.getInstance().isMaintenanceMode();
        maintenanceBanner.setVisible(active);
    }


//    // Test method - creates a frame to display the panel
//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Instructor Dashboard Panel Test");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1200, 800);
//            frame.setLocationRelativeTo(null);
//
//            DashboardInstructor panel = new DashboardInstructor("Dr. John Smith", "Computer Science");
//            frame.add(panel);
//
//            frame.setVisible(true);
//        });
//    }
}