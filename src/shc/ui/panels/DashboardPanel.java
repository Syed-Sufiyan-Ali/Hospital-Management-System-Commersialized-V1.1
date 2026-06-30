package shc.ui.panels;

import shc.db.DAO;
import shc.util.Theme;
import shc.ui.components.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private UI.StatCard scPatients, scAdmitted, scAppointments, scLowStock;
    private JLabel      revenueLabel, pendingLabel, dateLabel;
    private DefaultTableModel apptModel;

    public DashboardPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ── Top bar ────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(Theme.TEXT_H);

        dateLabel = new JLabel();
        dateLabel.setFont(Theme.FONT_BODY);
        dateLabel.setForeground(Theme.TEXT_MUTED);
        updateDate();

        topBar.add(heading,   BorderLayout.WEST);
        topBar.add(dateLabel, BorderLayout.EAST);

        // ── Stat Cards ─────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        scPatients    = new UI.StatCard("Total Patients",    "—", "All time",    Theme.PRIMARY);
        scAdmitted    = new UI.StatCard("Currently Admitted","—", "In hospital", Theme.WARNING);
        scAppointments= new UI.StatCard("Today's Appointments","—","Scheduled today",Theme.SUCCESS);
        scLowStock    = new UI.StatCard("Low Stock Alerts",  "—", "Needs reorder",Theme.DANGER);

        statsRow.add(scPatients);
        statsRow.add(scAdmitted);
        statsRow.add(scAppointments);
        statsRow.add(scLowStock);

        // ── Revenue Row ────────────────────────────────────────────────────
        JPanel revenueRow = new JPanel(new GridLayout(1, 2, 14, 0));
        revenueRow.setOpaque(false);
        revenueRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        UI.Card revCard = new UI.Card(14, Color.WHITE);
        revCard.setLayout(new BorderLayout(0, 8));
        revCard.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JLabel revTitle = new JLabel("Total Revenue Collected");
        revTitle.setFont(Theme.FONT_SUBHEAD); revTitle.setForeground(Theme.TEXT_MUTED);
        revenueLabel = new JLabel("Rs. —");
        revenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        revenueLabel.setForeground(Theme.SUCCESS);
        revCard.add(revTitle,    BorderLayout.NORTH);
        revCard.add(revenueLabel,BorderLayout.CENTER);

        UI.Card pendCard = new UI.Card(14, Color.WHITE);
        pendCard.setLayout(new BorderLayout(0, 8));
        pendCard.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        JLabel pendTitle = new JLabel("Pending / Unpaid Invoices");
        pendTitle.setFont(Theme.FONT_SUBHEAD); pendTitle.setForeground(Theme.TEXT_MUTED);
        pendingLabel = new JLabel("Rs. —");
        pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pendingLabel.setForeground(Theme.DANGER);
        pendCard.add(pendTitle,   BorderLayout.NORTH);
        pendCard.add(pendingLabel,BorderLayout.CENTER);

        revenueRow.add(revCard);
        revenueRow.add(pendCard);

        // ── Today's Appointments Table ─────────────────────────────────────
        UI.Card apptCard = new UI.Card(14, Color.WHITE);
        apptCard.setLayout(new BorderLayout(0, 10));
        apptCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel apptTop = new JPanel(new BorderLayout());
        apptTop.setOpaque(false);
        JLabel apptTitle = new JLabel("Today's Appointments");
        apptTitle.setFont(Theme.FONT_HEADING); apptTitle.setForeground(Theme.TEXT_H);
        UI.PrimaryBtn btnRefresh = new UI.PrimaryBtn("↻ Refresh", Theme.PRIMARY);
        btnRefresh.setPreferredSize(new Dimension(110, 32));
        btnRefresh.addActionListener(e -> refresh());
        apptTop.add(apptTitle,  BorderLayout.WEST);
        apptTop.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"Patient ID", "Patient Name", "Doctor", "Time", "Purpose", "Status"};
        apptModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable apptTable = new JTable(apptModel);
        UI.styleTable(apptTable);
        JScrollPane sp = UI.scrollPane(apptTable);

        apptCard.add(apptTop, BorderLayout.NORTH);
        apptCard.add(sp,      BorderLayout.CENTER);

        // ── Layout ─────────────────────────────────────────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(statsRow);
        center.add(revenueRow);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(topBar,  BorderLayout.NORTH);
        topSection.add(center,  BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(apptCard,   BorderLayout.CENTER);
    }

    public void refresh() {
        updateDate();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int patients=0, admitted=0, todayAppts=0, lowStock=0;
            double revenue=0, pending=0;
            List<Map<String,String>> todayList = new ArrayList<>();

            protected Void doInBackground() {
                try {
                    patients   = DAO.countPatients();
                    admitted   = DAO.countAdmitted();
                    todayAppts = DAO.countTodayAppointments();
                    lowStock   = DAO.countLowStockMedicines();
                    revenue    = DAO.totalRevenue();
                    pending    = DAO.pendingRevenue();
                    todayList  = DAO.getTodayAppointments();
                } catch (Exception e) { e.printStackTrace(); }
                return null;
            }
            protected void done() {
                scPatients.setValue(String.valueOf(patients));
                scAdmitted.setValue(String.valueOf(admitted));
                scAppointments.setValue(String.valueOf(todayAppts));
                scLowStock.setValue(String.valueOf(lowStock));
                if (lowStock > 0) scLowStock.setChange("⚠ Reorder needed");

                revenueLabel.setText(String.format("Rs. %,.0f", revenue));
                pendingLabel.setText(String.format("Rs. %,.0f", pending));

                apptModel.setRowCount(0);
                for (Map<String,String> a : todayList) {
                    apptModel.addRow(new Object[]{
                        a.get("patient_id"), a.get("patient_name"), a.get("doctor"),
                        a.get("appt_time"),  a.get("purpose"),      a.get("status")
                    });
                }
            }
        };
        worker.execute();
    }

    private void updateDate() {
        String d = new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date());
        if (dateLabel != null) dateLabel.setText(d);
    }
}
