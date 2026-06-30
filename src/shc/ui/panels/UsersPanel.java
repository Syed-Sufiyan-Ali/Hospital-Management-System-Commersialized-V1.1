package shc.ui.panels;

import shc.db.DAO;
import shc.util.Theme;
import shc.util.Security;
import shc.ui.components.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class UsersPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;

    private UI.Field    fUsername, fFullName;
    private UI.PassField fPassword;
    private JComboBox<String> cbRole;
    private JCheckBox   chkActive;

    public UsersPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        refresh();
    }

    private void buildUI() {
        add(UI.pageTitle("User Management", "Create accounts, assign roles, enable or disable access"), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(500);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(8);

        // ── Left: Table ─────────────────────────────────────────────────────
        UI.Card leftCard = new UI.Card(14, Color.WHITE);
        leftCard.setLayout(new BorderLayout(0, 10));
        leftCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel lTop = new JPanel(new BorderLayout());
        lTop.setOpaque(false);
        JLabel t = new JLabel("System Users");
        t.setFont(Theme.FONT_HEADING); t.setForeground(Theme.TEXT_H);
        UI.PrimaryBtn btnR = new UI.PrimaryBtn("↻", Theme.PRIMARY);
        btnR.setPreferredSize(new Dimension(36, 32));
        btnR.addActionListener(e -> refresh());
        lTop.add(t, BorderLayout.WEST); lTop.add(btnR, BorderLayout.EAST);

        String[] cols = {"Username","Full Name","Role","Active","Created"};
        tableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        UI.styleTable(table);

        JPanel lBot = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        lBot.setOpaque(false);
        UI.OutlineBtn btnToggle = new UI.OutlineBtn("⏻ Toggle Active", Theme.WARNING);
        UI.OutlineBtn btnDelete = new UI.OutlineBtn("🗑 Delete",        Theme.DANGER);
        btnToggle.setPreferredSize(new Dimension(140, 32));
        btnDelete.setPreferredSize(new Dimension(90,  32));
        btnToggle.addActionListener(e -> toggleActive());
        btnDelete.addActionListener(e -> deleteUser());
        lBot.add(btnToggle); lBot.add(btnDelete);

        leftCard.add(lTop,                 BorderLayout.NORTH);
        leftCard.add(UI.scrollPane(table), BorderLayout.CENTER);
        leftCard.add(lBot,                 BorderLayout.SOUTH);

        // ── Right: Form ─────────────────────────────────────────────────────
        UI.Card rightCard = new UI.Card(14, Color.WHITE);
        rightCard.setLayout(new BorderLayout(0, 14));
        rightCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel fTitle = new JLabel("Add / Update User");
        fTitle.setFont(Theme.FONT_HEADING); fTitle.setForeground(Theme.TEXT_H);

        JPanel form = new JPanel(new GridLayout(5, 1, 0, 12));
        form.setOpaque(false);

        fUsername = new UI.Field("Username (login name)");
        fFullName = new UI.Field("Display name");
        fPassword = new UI.PassField();
        cbRole    = new JComboBox<>(new String[]{"Admin","Doctor","Receptionist","Staff"});
        cbRole.setFont(Theme.FONT_BODY);

        form.add(UI.formRow("Username *",     fUsername));
        form.add(UI.formRow("Full Name *",    fFullName));
        form.add(UI.formRow("Password *",     fPassword));
        form.add(UI.formRow("Role *",         cbRole));

        // Info box
        JPanel infoBox = new JPanel(new BorderLayout());
        infoBox.setBackground(new Color(240, 249, 255));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(186, 230, 253)),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel infoTxt = new JLabel("<html>"
            + "<b>Role Permissions:</b><br>"
            + "• <b>Admin</b> — Full access (users, delete records)<br>"
            + "• <b>Doctor</b> — Patients, Appointments, Billing<br>"
            + "• <b>Receptionist</b> — Appointments, Billing<br>"
            + "• <b>Staff</b> — View only"
            + "</html>");
        infoTxt.setFont(Theme.FONT_SMALL);
        infoTxt.setForeground(new Color(8, 70, 90));
        infoBox.add(infoTxt);

        statusLabel = UI.statusLabel();

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        UI.PrimaryBtn btnSave   = new UI.PrimaryBtn("＋ Save User",  Theme.SUCCESS);
        UI.OutlineBtn btnClear  = new UI.OutlineBtn("↺ Clear",       Theme.TEXT_MUTED);
        btnSave.addActionListener(e -> saveUser());
        btnClear.addActionListener(e -> clearForm());
        btnRow.add(btnSave); btnRow.add(btnClear);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(btnRow,      BorderLayout.NORTH);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        rightCard.add(fTitle,  BorderLayout.NORTH);
        rightCard.add(form,    BorderLayout.CENTER);

        JPanel rBot = new JPanel(new BorderLayout(0, 10));
        rBot.setOpaque(false);
        rBot.add(infoBox, BorderLayout.NORTH);
        rBot.add(bottom,  BorderLayout.SOUTH);
        rightCard.add(rBot, BorderLayout.SOUTH);

        split.setLeftComponent(leftCard);
        split.setRightComponent(rightCard);
        add(split, BorderLayout.CENTER);
    }

    public void refresh() {
        SwingWorker<List<Map<String,String>>, Void> w = new SwingWorker<>() {
            protected List<Map<String,String>> doInBackground() throws Exception { return DAO.getAllUsers(); }
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Map<String,String> u : get()) {
                        tableModel.addRow(new Object[]{
                            u.get("username"), u.get("full_name"), u.get("role"),
                            "1".equals(u.get("active")) ? "✓ Active" : "✗ Disabled",
                            u.getOrDefault("created_at","")
                        });
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void saveUser() {
        String user = fUsername.getText().trim();
        String name = fFullName.getText().trim();
        String pass = new String(fPassword.getPassword());
        if (user.isEmpty() || name.isEmpty() || pass.isEmpty()) {
            UI.setStatus(statusLabel, "Username, Full Name, and Password are required.", false); return;
        }
        if (pass.length() < 6) { UI.setStatus(statusLabel, "Password must be at least 6 characters.", false); return; }
        try {
            DAO.saveUser(user, Security.sha256(pass), name, (String) cbRole.getSelectedItem());
            UI.setStatus(statusLabel, "✓ User '" + user + "' saved.", true);
            clearForm(); refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }

    private void toggleActive() {
        int row = table.getSelectedRow();
        if (row < 0) { UI.setStatusWarn(statusLabel, "Select a user first."); return; }
        String username = (String) tableModel.getValueAt(row, 0);
        if ("admin".equalsIgnoreCase(username)) { UI.setStatus(statusLabel, "Cannot disable the default admin account.", false); return; }
        String current  = (String) tableModel.getValueAt(row, 3);
        boolean active  = current.startsWith("✓");
        try {
            DAO.setUserActive(username, !active);
            UI.setStatus(statusLabel, "✓ User '" + username + "' " + (!active ? "enabled." : "disabled."), true);
            refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) { UI.setStatusWarn(statusLabel, "Select a user first."); return; }
        String username = (String) tableModel.getValueAt(row, 0);
        if (username.equalsIgnoreCase(Security.currentUser)) {
            UI.setStatus(statusLabel, "You cannot delete your own account.", false); return;
        }
        if ("admin".equalsIgnoreCase(username)) {
            UI.setStatus(statusLabel, "The default admin account cannot be deleted.", false); return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Delete user '" + username + "'? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            DAO.deleteUser(username);
            UI.setStatus(statusLabel, "✓ User '" + username + "' deleted.", true);
            refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }

    private void clearForm() {
        fUsername.setText(""); fFullName.setText(""); fPassword.setText("");
        cbRole.setSelectedIndex(0); table.clearSelection();
    }
}
