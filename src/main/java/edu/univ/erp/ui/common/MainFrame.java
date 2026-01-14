package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.InstructorTeachingStats;
import edu.univ.erp.domain.Student;
import edu.univ.erp.ui.admin.DashboardAdmin;
import edu.univ.erp.ui.instructor.DashboardInstructor;
import edu.univ.erp.ui.student.DashboardStudent;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.access.MaintenanceAccess;


public class MainFrame extends JFrame {

    private static MainFrame instance;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private String currentUser;
    private String currentRole;
    private boolean maintenanceMode = false;
    private Student student;

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    private MainFrame() {
        setTitle("University ERP System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize sidebar (250px wide, hidden by default)
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(new Color(30, 41, 59)); // Dark sidebar
        sidebarPanel.setVisible(false); // Hidden on login screen
        add(sidebarPanel, BorderLayout.WEST);

        // Initialize content panel
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }


    public void switchPanel(JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    public void setSidebarVisible(boolean visible) {
        sidebarPanel.setVisible(visible);
        revalidate();
        repaint();
    }


    private void updateSidebar(String role) {
        sidebarPanel.removeAll();

        // TODO: Create role-specific sidebar panels
        // For now, just show a placeholder
        JLabel roleLabel = new JLabel(role.toUpperCase());
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sidebarPanel.add(roleLabel);

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }


    public void setCurrentUser(String username, String role) {
        this.currentUser = username;
        this.currentRole = role;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }


    public void showLogin() {
        setSidebarVisible(false);
        switchPanel(new LoginWindow());
    }

    public void showAdminDashboard(String username, AdminSystemStats stats) {
        DashboardAdmin dashboard = new DashboardAdmin(username, stats);
        switchPanel(dashboard);
    }

    public void refreshAdminDashboard() {
        AdminService service = new AdminService();
        AdminSystemStats newStats = service.loadSystemStats();
        showAdminDashboard(currentUser, newStats);
    }

    public void showInstructorDashboard(Instructor instructor, InstructorTeachingStats stats) {
        setCurrentUser(instructor.getUsername(), "INSTRUCTOR");

        // 1. Force fresh read from DB
        boolean maintenance = MaintenanceAccess.getInstance().isMaintenanceMode();
        DashboardInstructor dashboard = new DashboardInstructor(instructor, stats);
        dashboard.updateMaintenanceBanner();
        switchPanel(dashboard);

    }

    public void showStudentDashboard(Student student) {
        setCurrentUser(student.getUsername(), "STUDENT");

        boolean maintenance = MaintenanceAccess.getInstance().isMaintenanceMode();
        DashboardStudent dashboard = new DashboardStudent(student);
        dashboard.updateMaintenanceBanner();
        switchPanel(dashboard);
    }



    public void logout() {
        currentUser = null;
        currentRole = null;
        showLogin();
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = MainFrame.getInstance();
            frame.setVisible(true);
            frame.showLogin();
        });
    }
}