package shc.ui;

import shc.util.Theme;
import shc.util.Security;
import shc.ui.components.UI;
import shc.ui.panels.*;
import shc.ui.dialogs.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private CardLayout    cards;
    private JPanel        cardPanel;
    private DashboardPanel    dashPanel;
    private PatientsPanel     patPanel;
    private AppointmentsPanel apptPanel;
    private BillingPanel      billPanel;
    private MedicinePanel     medPanel;
    private UsersPanel        usersPanel;

    private List<JToggleButton> navBtns = new ArrayList<>();
    private ButtonGroup         navGroup = new ButtonGroup();

    public MainFrame() {
        setTitle("Sufiyan Health Clinic  —  " + Security.currentFullName + "  [" + Security.currentRole + "]");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { confirmExit(); }
        });

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        // ── SIDEBAR ────────────────────────────────────────────────────────
        UI.GradientPanel sidebar = new UI.GradientPanel(Theme.SIDEBAR_TOP, Theme.SIDEBAR_BOT, true);
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));

        JPanel sideContent = new JPanel();
        sideContent.setLayout(new BoxLayout(sideContent, BoxLayout.Y_AXIS));
        sideContent.setOpaque(false);
        sideContent.setBorder(BorderFactory.createEmptyBorder(24, 14, 20, 14));

        // ── Logo ──────────────────────────────────────────────────────────
        JPanel logoArea = new JPanel(new BorderLayout(0, 2));
        logoArea.setOpaque(false);
        logoArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel icon = new JLabel("⚕", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        icon.setForeground(new Color(103, 232, 249));

        JLabel appName = new JLabel("Sufiyan Health Clinic", SwingConstants.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        appName.setForeground(Color.WHITE);

        JLabel appSub = new JLabel("Hospital Management", SwingConstants.CENTER);
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        appSub.setForeground(new Color(147, 197, 253));

        logoArea.add(icon,   BorderLayout.NORTH);
        logoArea.add(appName,BorderLayout.CENTER);
        logoArea.add(appSub, BorderLayout.SOUTH);
        sideContent.add(logoArea);
        sideContent.add(Box.createVerticalStrut(16));

        // ── User Badge ────────────────────────────────────────────────────
        JPanel badge = new JPanel(new BorderLayout(10, 0));
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        badge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel avatarLbl = new JLabel("👤");
        avatarLbl.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JPanel nameBlock = new JPanel(new GridLayout(2, 1, 0, 1));
        nameBlock.setOpaque(false);
        JLabel nameL = new JLabel(Security.currentFullName);
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameL.setForeground(Color.WHITE);
        JLabel roleL = new JLabel(Security.currentRole);
        roleL.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleL.setForeground(new Color(147, 197, 253));
        nameBlock.add(nameL); nameBlock.add(roleL);

        badge.add(avatarLbl, BorderLayout.WEST);
        badge.add(nameBlock, BorderLayout.CENTER);

        // Wrap badge in rounded panel
        UI.Card badgeCard = new UI.Card(10, new Color(255, 255, 255, 20));
        badgeCard.setShadow(false);
        badgeCard.setLayout(new BorderLayout());
        badgeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        badgeCard.add(badge);
        sideContent.add(badgeCard);
        sideContent.add(Box.createVerticalStrut(18));

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(255, 255, 255, 25));
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sideContent.add(sep1);
        sideContent.add(Box.createVerticalStrut(12));

        JLabel navLbl = new JLabel("  MENU");
        navLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLbl.setForeground(new Color(100, 160, 180));
        navLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideContent.add(navLbl);
        sideContent.add(Box.createVerticalStrut(6));

        // ── Card Panel ────────────────────────────────────────────────────
        cards     = new CardLayout();
        cardPanel = new JPanel(cards);
        cardPanel.setBackground(Theme.BG);

        dashPanel  = new DashboardPanel();
        patPanel   = new PatientsPanel();
        apptPanel  = new AppointmentsPanel();
        billPanel  = new BillingPanel();
        medPanel   = new MedicinePanel();
        usersPanel = new UsersPanel();

        cardPanel.add(dashPanel,  "dashboard");
        cardPanel.add(patPanel,   "patients");
        cardPanel.add(apptPanel,  "appointments");
        cardPanel.add(billPanel,  "billing");
        cardPanel.add(medPanel,   "medicines");
        cardPanel.add(usersPanel, "users");

        // ── Nav Items ────────────────────────────────────────────────────
        Object[][] navItems = {
            {"🏠",  "Dashboard",     "dashboard",    true },
            {"👥",  "Patients",      "patients",     true },
            {"📅",  "Appointments",  "appointments", true },
            {"📄",  "Billing",       "billing",      true },
            {"💊",  "Medicines",     "medicines",    true },
            {"👤",  "Users",         "users",        Security.isAdmin() },
        };

        for (Object[] item : navItems) {
            if (!(boolean) item[3]) continue;
            String icon2 = (String) item[0];
            String label = (String) item[1];
            String key   = (String) item[2];

            JToggleButton btn = createNavBtn(icon2 + "  " + label, key);
            navGroup.add(btn);
            navBtns.add(btn);
            sideContent.add(btn);
            sideContent.add(Box.createVerticalStrut(3));
        }

        // Select first
        if (!navBtns.isEmpty()) {
            navBtns.get(0).setSelected(true);
            navBtns.get(0).setForeground(Color.WHITE);
        }

        // ── Bottom actions ────────────────────────────────────────────────
        sideContent.add(Box.createVerticalGlue());
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 25));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sideContent.add(sep2);
        sideContent.add(Box.createVerticalStrut(10));

        sideContent.add(sideAction("🔑  Change Password", new Color(186, 230, 253), e ->
            new ChangePasswordDialog(this).setVisible(true)));
        sideContent.add(Box.createVerticalStrut(3));
        sideContent.add(sideAction("↩  Logout", new Color(252, 165, 165), e -> logout()));
        sideContent.add(Box.createVerticalStrut(3));
        sideContent.add(sideAction("⏻  Exit", new Color(252, 165, 165), e -> confirmExit()));

        sidebar.add(sideContent, BorderLayout.CENTER);

        // ── Top Bar ──────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel topTitle = new JLabel("Sufiyan Health Clinic");
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topTitle.setForeground(Theme.TEXT_H);

        JLabel topRight = new JLabel("Welcome, " + Security.currentFullName + "  |  " + Security.currentRole);
        topRight.setFont(Theme.FONT_SMALL);
        topRight.setForeground(Theme.TEXT_MUTED);

        topBar.add(topTitle, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.add(topBar,   BorderLayout.NORTH);
        mainArea.add(cardPanel,BorderLayout.CENTER);

        root.add(sidebar,  BorderLayout.WEST);
        root.add(mainArea, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JToggleButton createNavBtn(String text, String key) {
        JToggleButton btn = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(new Color(103, 232, 249));
                    g2.fillRoundRect(0, (getHeight() - 22) / 2, 3, 22, 3, 3);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(147, 197, 253));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        btn.addActionListener(e -> {
            cards.show(cardPanel, key);
            for (JToggleButton b : navBtns)
                b.setForeground(b.isSelected() ? Color.WHITE : new Color(147, 197, 253));
            // Refresh active panel
            switch (key) {
                case "dashboard":   dashPanel.refresh();  break;
                case "patients":    patPanel.refresh();   break;
                case "appointments":apptPanel.refresh();  break;
                case "billing":     billPanel.refreshHistory(); break;
                case "medicines":   medPanel.refresh();   break;
                case "users":       usersPanel.refresh(); break;
            }
        });
        return btn;
    }

    private JButton sideAction(String text, Color fg, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(fg);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setContentAreaFilled(false); b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.addActionListener(al);
        return b;
    }

    private void logout() {
        int c = JOptionPane.showConfirmDialog(this, "Logout and return to the login screen?", "Logout", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            Security.currentUser = "";
            Security.currentFullName = "";
            Security.currentRole = "";
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    private void confirmExit() {
        int c = JOptionPane.showConfirmDialog(this, "Exit Sufiyan Health Clinic?", "Exit", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            shc.db.DBConnection.getInstance();
            System.exit(0);
        }
    }
}
