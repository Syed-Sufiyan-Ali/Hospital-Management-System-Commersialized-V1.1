package shc.ui;

import shc.db.DAO;
import shc.util.Security;
import shc.util.Theme;
import shc.ui.components.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class LoginFrame extends JFrame {

    private UI.Field    fUser;
    private UI.PassField fPass;
    private JLabel       errLabel;
    private JCheckBox    showPwd;
    private int          failCount = 0;

    public LoginFrame() {
        setTitle("Sufiyan Health Clinic — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        // ── LEFT — Branding ──────────────────────────────────────────────────
        UI.GradientPanel left = new UI.GradientPanel(Theme.SIDEBAR_TOP, Theme.SIDEBAR_BOT, true);
        left.setPreferredSize(new Dimension(360, 0));
        left.setLayout(new BorderLayout());

        JPanel lContent = new JPanel();
        lContent.setLayout(new BoxLayout(lContent, BoxLayout.Y_AXIS));
        lContent.setOpaque(false);
        lContent.setBorder(BorderFactory.createEmptyBorder(60, 44, 60, 44));

        JLabel icon = new JLabel("⚕");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        icon.setForeground(new Color(103, 232, 249));
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brand = new JLabel("Sufiyan Health Clinic");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Comprehensive Hospital Management");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(147, 197, 253));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        lContent.add(icon);
        lContent.add(Box.createVerticalStrut(18));
        lContent.add(brand);
        lContent.add(Box.createVerticalStrut(4));
        lContent.add(tagline);
        lContent.add(Box.createVerticalStrut(44));

        String[] bullets = {
            "✓  Patient Registration & Profiles",
            "✓  Appointment Scheduling",
            "✓  Billing & Invoice Generation",
            "✓  Medicine Inventory Tracking",
            "✓  Live Dashboard & Analytics",
            "✓  Role-Based Access Control"
        };
        for (String b : bullets) {
            JLabel bl = new JLabel(b);
            bl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            bl.setForeground(new Color(186, 230, 253));
            bl.setAlignmentX(Component.LEFT_ALIGNMENT);
            lContent.add(bl);
            lContent.add(Box.createVerticalStrut(7));
        }

        lContent.add(Box.createVerticalGlue());
        JLabel version = new JLabel("v1.0 Production Release");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(71, 120, 148));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        lContent.add(version);

        left.add(lContent, BorderLayout.CENTER);

        // ── RIGHT — Login Form ───────────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Theme.BG);

        UI.Card card = new UI.Card(16, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(44, 44, 44, 44));
        card.setPreferredSize(new Dimension(360, 440));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Theme.TEXT_H);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Access your clinic dashboard");
        subtitle.setFont(Theme.FONT_BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        fUser = new UI.Field("Enter username");
        fUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        fPass = new UI.PassField();
        fPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        fPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        showPwd = new JCheckBox("Show password");
        showPwd.setFont(Theme.FONT_SMALL);
        showPwd.setForeground(Theme.TEXT_MUTED);
        showPwd.setOpaque(false);
        showPwd.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPwd.addActionListener(e -> fPass.setEchoChar(showPwd.isSelected() ? (char) 0 : '●'));

        UI.PrimaryBtn btnLogin = new UI.PrimaryBtn("Sign In →", Theme.PRIMARY);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        errLabel = new JLabel(" ");
        errLabel.setFont(Theme.FONT_SMALL);
        errLabel.setForeground(Theme.DANGER);
        errLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Default credentials: admin / admin123");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        hint.setForeground(Theme.TEXT_DISABLED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionListener doLogin = e -> attemptLogin();
        btnLogin.addActionListener(doLogin);
        fPass.addActionListener(doLogin);
        fUser.addActionListener(e -> fPass.requestFocus());

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(fUser);
        card.add(Box.createVerticalStrut(16));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(fPass);
        card.add(Box.createVerticalStrut(6));
        card.add(showPwd);
        card.add(Box.createVerticalStrut(6));
        card.add(errLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(14));
        card.add(hint);

        right.add(card);

        root.add(left,  BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);
        SwingUtilities.invokeLater(() -> fUser.requestFocus());
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_SUBHEAD);
        l.setForeground(Theme.TEXT_BODY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void attemptLogin() {
        if (failCount >= 5) {
            errLabel.setText("Account locked. Restart the application.");
            return;
        }
        String user = fUser.getText().trim();
        String pass = new String(fPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            errLabel.setText("Please enter your username and password.");
            return;
        }

        try {
            Map<String,String> u = DAO.loginUser(user, Security.sha256(pass));
            if (u == null) {
                failCount++;
                int left = 5 - failCount;
                errLabel.setText("Invalid credentials." + (left < 5 ? "  (" + left + " attempt" + (left==1?"":"s") + " left)" : ""));
                fPass.setText("");
                fPass.requestFocus();
            } else if ("0".equals(u.get("active"))) {
                errLabel.setText("This account has been disabled. Contact admin.");
            } else {
                Security.currentUser     = u.get("username");
                Security.currentFullName = u.get("full_name");
                Security.currentRole     = u.get("role");
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
            }
        } catch (Exception ex) {
            errLabel.setText("Login error: " + ex.getMessage());
        }
    }
}
