//package edu.univ.erp.ui.instructor;
//
//import edu.univ.erp.service.AssessmentService;
//import edu.univ.erp.domain.AssessmentComponent;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.table.TableColumn;
//import java.awt.*;
//import java.util.List;
//
//public class AssessmentRuleEditor extends JDialog {
//
//    private final int courseId;
//    private final AssessmentService service = new AssessmentService();
//    private DefaultTableModel model;
//    private JTable table;
//
//    public AssessmentRuleEditor(Window parent, int courseId) {
//        super(parent, "Assessment Weight Rules", ModalityType.APPLICATION_MODAL);
//        this.courseId = courseId;
//
//        setLayout(new BorderLayout(10,10));
//        add(createTablePanel(), BorderLayout.CENTER);
//        add(createBottomPanel(), BorderLayout.SOUTH);
//
//        loadComponents();
//
//        setSize(750,450);
//        setLocationRelativeTo(parent);
//    }
//
//    private JScrollPane createTablePanel() {
//
//        model = new DefaultTableModel(
//                new String[]{"ComponentId", "Component", "Weight %", "Max Marks", "Delete"}, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                // editable except id + delete button
//                return column != 0 && column != 4;
//            }
//        };
//
//        table = new JTable(model);
//        table.setRowHeight(32);
//
//        // Hide componentId column
//        table.getColumnModel().getColumn(0).setMinWidth(0);
//        table.getColumnModel().getColumn(0).setMaxWidth(0);
//
//        // Add delete button
//        TableColumn deleteCol = table.getColumnModel().getColumn(4);
//        deleteCol.setCellRenderer(new DeleteButtonRenderer());
//        deleteCol.setCellEditor(new DeleteButtonEditor(new JCheckBox()));
//
//        return new JScrollPane(table);
//    }
//
//    private JPanel createBottomPanel() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//
//        JButton addBtn = new JButton("Add Component");
//        JButton saveBtn = new JButton("Save Rules");
//
//        addBtn.addActionListener(e -> addNewComponent());
//        saveBtn.addActionListener(e -> saveRules());
//
//        panel.add(addBtn);
//        panel.add(saveBtn);
//        return panel;
//    }
//
//    private void loadComponents() {
//        model.setRowCount(0);
//
//        try {
//            List<AssessmentComponent> list = service.getComponentsForCourse(courseId);
//            for (AssessmentComponent c : list) {
//                model.addRow(new Object[]{
//                        c.getComponentId(),
//                        c.getName(),
//                        c.getWeight(),
//                        c.getMaxMarks(),
//                        "Delete"
//                });
//            }
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage());
//        }
//    }
//
//    private void addNewComponent() {
//        JTextField nameField = new JTextField(20);
//        JTextField weightField = new JTextField(5);
//        JTextField maxMarksField = new JTextField(5);
//
//        JPanel p = new JPanel(new GridLayout(3,2,10,10));
//        p.add(new JLabel("Name:"));
//        p.add(nameField);
//        p.add(new JLabel("Weight (%):"));
//        p.add(weightField);
//        p.add(new JLabel("Max Marks:"));
//        p.add(maxMarksField);
//
//        int ok = JOptionPane.showConfirmDialog(this, p, "Add Component",
//                JOptionPane.OK_CANCEL_OPTION);
//        if (ok != JOptionPane.OK_OPTION) return;
//
//        try {
//            String name = nameField.getText().trim();
//            int weight = Integer.parseInt(weightField.getText().trim());
//            int maxMarks = Integer.parseInt(maxMarksField.getText().trim());
//
//            service.addComponent(courseId, name, weight, maxMarks);
//            loadComponents();
//
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Weight and Max Marks must be numbers.");
//        }
//        catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error: "+e.getMessage());
//        }
//    }
//
//    private void saveRules() {
//
//        int total = 0;
//
//        for (int i = 0; i < model.getRowCount(); i++) {
//
//            String name = model.getValueAt(i, 1).toString().trim();
//            String wStr = model.getValueAt(i, 2).toString().trim();
//            String maxStr = model.getValueAt(i, 3).toString().trim();
//
//            if (name.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Component name cannot be empty.");
//                return;
//            }
//
//            try {
//                int w = Integer.parseInt(wStr);
//                int max = Integer.parseInt(maxStr);
//
//                if (w <= 0 || max <= 0) {
//                    JOptionPane.showMessageDialog(this,
//                            "Weight and Max Marks must be > 0.");
//                    return;
//                }
//
//                total += w;
//
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(this, "Invalid number at row " + (i+1));
//                return;
//            }
//        }
//
//        if (total != 100) {
//            JOptionPane.showMessageDialog(this,
//                    "Total weight must equal 100%. Current total = " + total,
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        JOptionPane.showMessageDialog(this, "Weight rules saved successfully!");
//        dispose();
//    }
//
//    // ===================================================================
//    // DELETE RENDERER
//    // ===================================================================
//    class DeleteButtonRenderer extends JButton implements TableCellRenderer {
//        public DeleteButtonRenderer() {
//            setOpaque(true);
//            setBackground(Color.RED);
//            setForeground(Color.WHITE);
//            setText("Delete");
//        }
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                                                       boolean isSelected, boolean hasFocus,
//                                                       int row, int col) {
//            return this;
//        }
//    }
//
//    // ===================================================================
//    // DELETE EDITOR — FIXED: uses stored row, NOT selectedRow()
//    // ===================================================================
//    class DeleteButtonEditor extends DefaultCellEditor {
//
//        private JButton btn;
//        private int rowIndex;
//        private boolean clicked = false;
//
//        public DeleteButtonEditor(JCheckBox checkBox) {
//            super(checkBox);
//
//            btn = new JButton("Delete");
//            btn.setBackground(Color.RED);
//            btn.setForeground(Color.WHITE);
//
//            btn.addActionListener(e -> fireEditingStopped());
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable t, Object val,
//                                                     boolean selected, int row, int col) {
//            this.rowIndex = row;
//            clicked = true;
//            return btn;
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//
//            if (clicked) {
//                int compId = Integer.parseInt(model.getValueAt(rowIndex, 0).toString());
//
//                int confirm = JOptionPane.showConfirmDialog(null,
//                        "Delete this component?", "Confirm",
//                        JOptionPane.YES_NO_OPTION);
//
//                if (confirm == JOptionPane.YES_OPTION) {
//                    try {
//                        service.deleteComponent(compId);
//                        model.removeRow(rowIndex);
//
//                    } catch (Exception e) {
//                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
//                    }
//                }
//            }
//
//            clicked = false;
//            return "Delete";
//        }
//    }
//
//}



package edu.univ.erp.ui.instructor;

import edu.univ.erp.service.AssessmentService;
import edu.univ.erp.domain.AssessmentComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AssessmentRuleEditor extends JDialog {

    private final int courseId;
    private final AssessmentService service = new AssessmentService();
    private DefaultTableModel model;
    private JTable table;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241); // Indigo
    private static final Color PRIMARY_HOVER = new Color(79, 82, 221);
    private static final Color DANGER_COLOR = new Color(239, 68, 68); // Red
    private static final Color DANGER_HOVER = new Color(220, 38, 38);
    private static final Color BACKGROUND = new Color(249, 250, 251); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TABLE_HEADER = new Color(243, 244, 246);
    private static final Color TABLE_HOVER = new Color(249, 250, 251);

    public AssessmentRuleEditor(Window parent, int courseId) {
        super(parent, "Assessment Weight Rules", ModalityType.APPLICATION_MODAL);
        this.courseId = courseId;

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadComponents();

        setSize(850, 550);
        setLocationRelativeTo(parent);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel titleLabel = new JLabel("Assessment Weight Rules");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Configure assessment components and their weights");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        model = new DefaultTableModel(
                new String[]{"ComponentId", "Component Name", "Weight %", "Max Marks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Only ComponentId is non-editable
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(CARD_BACKGROUND);
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Hide componentId column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Modern table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 44));

        // Cell renderer for alternating rows
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BACKGROUND : TABLE_HOVER);
                }
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return c;
            }
        };

        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CARD_BACKGROUND);
        outerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);

        JButton deleteBtn = createModernButton("Delete Selected", DANGER_COLOR, DANGER_HOVER);
        JButton addBtn = createModernButton("Add Component", PRIMARY_COLOR, PRIMARY_HOVER);
        JButton saveBtn = createModernButton("Save Rules", PRIMARY_COLOR, PRIMARY_HOVER);

        deleteBtn.addActionListener(e -> deleteSelectedComponent());
        addBtn.addActionListener(e -> addNewComponent());
        saveBtn.addActionListener(e -> saveRules());

        buttonPanel.add(deleteBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(saveBtn);

        outerPanel.add(buttonPanel, BorderLayout.EAST);

        return outerPanel;
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

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 40));

        return button;
    }

    private void loadComponents() {
        model.setRowCount(0);

        try {
            List<AssessmentComponent> list = service.getComponentsForCourse(courseId);
            for (AssessmentComponent c : list) {
                model.addRow(new Object[]{
                        c.getComponentId(),
                        c.getName(),
                        c.getWeight(),
                        c.getMaxMarks()
                });
            }
        } catch (Exception e) {
            showModernError("Error loading components: " + e.getMessage());
        }
    }

    private void deleteSelectedComponent() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            showModernWarning("Please select a component to delete.");
            return;
        }

        int compId = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String compName = model.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + compName + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteComponent(compId);
                model.removeRow(selectedRow);
                showModernSuccess("Component deleted successfully!");
            } catch (Exception e) {
                showModernError("Error deleting component: " + e.getMessage());
            }
        }
    }

    private void addNewComponent() {
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField nameField = createModernTextField(20);
        JTextField weightField = createModernTextField(5);
        JTextField maxMarksField = createModernTextField(5);

        gbc.gridx = 0; gbc.gridy = 0;
        dialogPanel.add(createModernLabel("Component Name:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialogPanel.add(createModernLabel("Weight (%):"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(weightField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialogPanel.add(createModernLabel("Max Marks:"), gbc);
        gbc.gridx = 1;
        dialogPanel.add(maxMarksField, gbc);

        int ok = JOptionPane.showConfirmDialog(this, dialogPanel, "Add New Component",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            int weight = Integer.parseInt(weightField.getText().trim());
            int maxMarks = Integer.parseInt(maxMarksField.getText().trim());

            service.addComponent(courseId, name, weight, maxMarks);
            loadComponents();
            showModernSuccess("Component added successfully!");

        } catch (NumberFormatException ex) {
            showModernError("Weight and Max Marks must be valid numbers.");
        } catch (Exception e) {
            showModernError("Error adding component: " + e.getMessage());
        }
    }

    private void saveRules() {
        int total = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = model.getValueAt(i, 1).toString().trim();
            String wStr = model.getValueAt(i, 2).toString().trim();
            String maxStr = model.getValueAt(i, 3).toString().trim();

            if (name.isEmpty()) {
                showModernError("Component name cannot be empty at row " + (i + 1) + ".");
                return;
            }

            try {
                int w = Integer.parseInt(wStr);
                int max = Integer.parseInt(maxStr);

                if (w <= 0 || max <= 0) {
                    showModernError("Weight and Max Marks must be greater than 0 at row " + (i + 1) + ".");
                    return;
                }

                total += w;

            } catch (Exception ex) {
                showModernError("Invalid number at row " + (i + 1) + ".");
                return;
            }
        }

        if (total != 100) {
            showModernError("Total weight must equal 100%.\nCurrent total: " + total + "%");
            return;
        }

        showModernSuccess("Weight rules saved successfully!");
        dispose();
    }

    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private void showModernError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showModernWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showModernSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}