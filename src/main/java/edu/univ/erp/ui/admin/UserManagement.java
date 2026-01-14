package edu.univ.erp.ui.admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.AdminSystemStats;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.domain.NewUserRequest;
import edu.univ.erp.service.AdminService;

public class UserManagement extends JPanel {

    // Color scheme
    private static final Color mainTeal = new Color(0, 128, 128);
    private static final Color lightGrey = new Color(249, 250, 251);
    private static final Color darkBlue = new Color(17, 24, 39);
    private static final Color greenBright = new Color(16, 185, 129);
    private static final Color orangeBright = new Color(245, 158, 11);

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnDelete, btnBack;
    private AdminSystemStats stats;

    public UserManagement() {
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(lightGrey);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(mainTeal);

        JLabel subtitleLabel = new JLabel("View and manage all users");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(lightGrey);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        tablePanel.setBackground(Color.WHITE);

        String[] columnNames = {"Username", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(mainTeal);
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(lightGrey);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnAdd = createButton("Add User", greenBright);
        btnDelete = createButton("Delete User", orangeBright);
        btnBack = createButton("Back to Dashboard", Color.GRAY);

        btnAdd.addActionListener(e -> addUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnBack.addActionListener(e -> backToDashboard());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 36));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void backToDashboard() {
        String username = MainFrame.getInstance().getCurrentUser();
        MainFrame.getInstance().switchPanel(new DashboardAdmin(username, stats));
    }

    public void loadUsers() {
        tableModel.setRowCount(0); // Clear existing rows
        String sql = "SELECT username, role FROM users";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // Make sure we're using auth_db since users table is there
            stmt.execute("USE auth_db");

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getString("role")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"student","instructor","admin"});

        JPanel panel = new JPanel(new GridLayout(3,2,10,10));
        panel.add(new JLabel("Username:")); panel.add(usernameField);
        panel.add(new JLabel("Password:")); panel.add(passwordField);
        panel.add(new JLabel("Role:")); panel.add(roleCombo);

        int option = JOptionPane.showConfirmDialog(
                this, panel, "Add User", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) return;

        NewUserRequest req = new NewUserRequest(
                usernameField.getText().trim(),
                new String(passwordField.getPassword()),
                (String) roleCombo.getSelectedItem()
        );

        // role-specific details
        if (req.getRole().equals("student")) {
            req.setRollNumber(JOptionPane.showInputDialog("Roll Number:"));
            req.setProgram(JOptionPane.showInputDialog("Program:"));
            req.setYear(Integer.parseInt(JOptionPane.showInputDialog("Year:")));
        }

        if (req.getRole().equals("instructor")) {
            req.setDepartment(JOptionPane.showInputDialog("Department:"));
        }

        AdminService service = new AdminService();
        String result = service.addUser(req);

        switch (result) {
            case "success":
                JOptionPane.showMessageDialog(this, "User added.");
                loadUsers();
                break;
            case "duplicate":
                JOptionPane.showMessageDialog(this, "Username already exists.");
                break;
            default:
                JOptionPane.showMessageDialog(this, "Error: " + result);
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the user: " + username + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION)
            return;

        String deleteAuthUser = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteAuthUser)) {

            pstmt.setString(1, username);
            int rows = pstmt.executeUpdate();

            if (rows == 0) {
                JOptionPane.showMessageDialog(this,
                        "User not found in auth_db!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "User deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadUsers();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting user: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    public static void showDialog(Component parent) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "User Management",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        UserManagement panel = new UserManagement();

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

}