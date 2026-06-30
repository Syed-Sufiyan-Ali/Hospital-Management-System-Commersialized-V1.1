package shc.ui.dialogs;

import shc.db.DAO;
import shc.util.Theme;
import shc.util.Security;
import shc.ui.components.UI;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    public ChangePasswordDialog(Frame owner) {
        super(owner, "Change Password", true);
        setSize(420, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BG);

        UI.Card card = new UI.Card(16, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));
        card.setPreferredSize(new Dimension(360, 310));

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Theme.TEXT_H);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Changing password for: " + Security.currentFullName);
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        UI.PassField fCurrent = new UI.PassField();
        UI.PassField fNew     = new UI.PassField();
        UI.PassField fConfirm = new UI.PassField();
        fCurrent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fNew.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fCurrent.setAlignmentX(Component.LEFT_ALIGNMENT);
        fNew.setAlignmentX(Component.LEFT_ALIGNMENT);
        fConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel errLabel = UI.statusLabel();
        errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        UI.PrimaryBtn btnSave = new UI.PrimaryBtn("Update Password", Theme.PRIMARY);
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSave.addActionListener(e -> {
            String current = new String(fCurrent.getPassword());
            String newPw   = new String(fNew.getPassword());
            String confirm = new String(fConfirm.getPassword());

            if (current.isEmpty() || newPw.isEmpty() || confirm.isEmpty()) {
                UI.setStatus(errLabel, "All fields are required.", false); return;
            }
            if (!Security.sha256(current).equals(getStoredHash())) {
                UI.setStatus(errLabel, "Current password is incorrect.", false); return;
            }
            if (newPw.length() < 6) {
                UI.setStatus(errLabel, "New password must be at least 6 characters.", false); return;
            }
            if (!newPw.equals(confirm)) {
                UI.setStatus(errLabel, "New passwords do not match.", false); return;
            }
            try {
                DAO.changePassword(Security.currentUser, Security.sha256(newPw));
                JOptionPane.showMessageDialog(this, "Password updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                UI.setStatus(errLabel, "Error: " + ex.getMessage(), false);
            }
        });

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(22));
        card.add(fieldLabel("Current Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(fCurrent);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("New Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(fNew);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("Confirm New Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(fConfirm);
        card.add(Box.createVerticalStrut(8));
        card.add(errLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(btnSave);

        root.add(card);
        setContentPane(root);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SUBHEAD);
        l.setForeground(Theme.TEXT_BODY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private String getStoredHash() {
        try {
            java.util.List<java.util.Map<String,String>> users = DAO.getAllUsers();
            for (java.util.Map<String,String> u : users) {
                if (Security.currentUser.equals(u.get("username"))) {
                    // Re-query directly
                    java.sql.PreparedStatement ps = shc.db.DBConnection.getInstance().getConnection()
                        .prepareStatement("SELECT password FROM users WHERE username=?");
                    ps.setString(1, Security.currentUser);
                    java.sql.ResultSet rs = ps.executeQuery();
                    if (rs.next()) { String h = rs.getString(1); rs.close(); ps.close(); return h; }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }
}
