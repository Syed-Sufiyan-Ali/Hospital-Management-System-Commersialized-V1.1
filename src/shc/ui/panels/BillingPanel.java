package shc.ui.panels;

import shc.db.DAO;
import shc.util.Theme;
import shc.ui.components.UI;
import shc.ui.dialogs.InvoiceDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BillingPanel extends JPanel {

    private DefaultTableModel historyModel;
    private JTable historyTable;
    private JLabel statusLabel;

    // Invoice form
    private UI.Field fPatientId, fDays;
    private JComboBox<String> cbRoom;
    private JLabel previewLabel;

    private static final int[] RATES = {1000, 2000, 3500, 6000};
    private static final String[] ROOM_LABELS = {
        "General Ward    — Rs. 1,000 / day",
        "Semi-Private     — Rs. 2,000 / day",
        "Private Room    — Rs. 3,500 / day",
        "ICU / Critical  — Rs. 6,000 / day"
    };

    public BillingPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        refreshHistory();
    }

    private void buildUI() {
        add(UI.pageTitle("Billing & Invoices","Generate invoices and manage payment status"), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(540);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(8);

        // ── Left: Invoice History ───────────────────────────────────────────
        UI.Card leftCard = new UI.Card(14, Color.WHITE);
        leftCard.setLayout(new BorderLayout(0, 10));
        leftCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel leftTop = new JPanel(new BorderLayout());
        leftTop.setOpaque(false);
        JLabel histTitle = new JLabel("Invoice History");
        histTitle.setFont(Theme.FONT_HEADING); histTitle.setForeground(Theme.TEXT_H);
        UI.PrimaryBtn btnRefresh = new UI.PrimaryBtn("↻", Theme.PRIMARY);
        btnRefresh.setPreferredSize(new Dimension(36,32));
        btnRefresh.addActionListener(e -> refreshHistory());
        leftTop.add(histTitle, BorderLayout.WEST);
        leftTop.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"Invoice #","Patient","Days","Room","Total (Rs)","Status","Date"};
        historyModel = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){ return false; } };
        historyTable = new JTable(historyModel);
        UI.styleTable(historyTable);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actionRow.setOpaque(false);
        UI.OutlineBtn btnMarkPaid    = new UI.OutlineBtn("✓ Mark Paid",   Theme.SUCCESS);
        UI.OutlineBtn btnMarkUnpaid  = new UI.OutlineBtn("✗ Mark Unpaid", Theme.DANGER);
        UI.OutlineBtn btnViewInvoice = new UI.OutlineBtn("👁 View",        Theme.PRIMARY);
        btnMarkPaid.setPreferredSize(new Dimension(110,32));
        btnMarkUnpaid.setPreferredSize(new Dimension(120,32));
        btnViewInvoice.setPreferredSize(new Dimension(80,32));
        btnMarkPaid.addActionListener(e   -> updateStatus("Paid"));
        btnMarkUnpaid.addActionListener(e -> updateStatus("Unpaid"));
        btnViewInvoice.addActionListener(e -> viewSelectedInvoice());
        actionRow.add(btnViewInvoice);
        actionRow.add(btnMarkPaid);
        actionRow.add(btnMarkUnpaid);

        leftCard.add(leftTop,   BorderLayout.NORTH);
        leftCard.add(UI.scrollPane(historyTable), BorderLayout.CENTER);
        leftCard.add(actionRow, BorderLayout.SOUTH);

        // ── Right: Generate Invoice ─────────────────────────────────────────
        UI.Card rightCard = new UI.Card(14, Color.WHITE);
        rightCard.setLayout(new BorderLayout(0, 14));
        rightCard.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel formTitle = new JLabel("Generate New Invoice");
        formTitle.setFont(Theme.FONT_HEADING); formTitle.setForeground(Theme.TEXT_H);

        JPanel form = new JPanel(new GridLayout(3, 1, 0, 12));
        form.setOpaque(false);
        fPatientId = new UI.Field("Enter Patient ID");
        fDays      = new UI.Field("Number of days admitted");
        cbRoom     = new JComboBox<>(ROOM_LABELS);
        cbRoom.setFont(Theme.FONT_BODY);
        form.add(UI.formRow("Patient ID *", fPatientId));
        form.add(UI.formRow("Days Admitted *", fDays));
        form.add(UI.formRow("Room Type *", cbRoom));

        // Rate Cards preview
        JPanel rateCards = new JPanel(new GridLayout(2, 2, 10, 10));
        rateCards.setOpaque(false);
        Color[] rateColors = {Theme.PRIMARY, Theme.SUCCESS, Theme.WARNING, Theme.DANGER};
        String[] rateLabels = {"General Ward","Semi-Private","Private Room","ICU / Critical"};
        String[] rates = {"Rs. 1,000","Rs. 2,000","Rs. 3,500","Rs. 6,000"};
        for (int i=0;i<4;i++) {
            UI.Card rc = new UI.Card(10, rateColors[i]);
            rc.setLayout(new BorderLayout(0,2));
            rc.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
            JLabel ra = new JLabel(rates[i]); ra.setFont(new Font("Segoe UI",Font.BOLD,15)); ra.setForeground(Color.WHITE);
            JLabel rl = new JLabel(rateLabels[i]+"/day"); rl.setFont(Theme.FONT_SMALL); rl.setForeground(new Color(255,255,255,200));
            rc.add(ra, BorderLayout.NORTH); rc.add(rl, BorderLayout.CENTER);
            rateCards.add(rc);
        }

        // Live preview
        previewLabel = new JLabel("<html><div style='color:#64748B;font-style:italic'>Fill in Patient ID and Days to preview total</div></html>");
        previewLabel.setFont(Theme.FONT_BODY);
        fDays.addActionListener(e -> updatePreview());
        cbRoom.addActionListener(e -> updatePreview());
        fDays.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e){ updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e){ updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ updatePreview(); }
        });

        statusLabel = UI.statusLabel();

        UI.PrimaryBtn btnGenerate = new UI.PrimaryBtn("📄  Generate & View Invoice", Theme.PRIMARY);
        btnGenerate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnGenerate.addActionListener(e -> generateInvoice());

        JPanel bottomArea = new JPanel();
        bottomArea.setLayout(new BoxLayout(bottomArea, BoxLayout.Y_AXIS));
        bottomArea.setOpaque(false);
        bottomArea.add(previewLabel);
        bottomArea.add(Box.createVerticalStrut(10));
        btnGenerate.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomArea.add(btnGenerate);
        bottomArea.add(Box.createVerticalStrut(6));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomArea.add(statusLabel);

        rightCard.add(formTitle,   BorderLayout.NORTH);
        rightCard.add(form,        BorderLayout.CENTER);

        JPanel rightBottom = new JPanel(new BorderLayout(0,12));
        rightBottom.setOpaque(false);
        rightBottom.add(rateCards, BorderLayout.NORTH);
        rightBottom.add(bottomArea,BorderLayout.SOUTH);
        rightCard.add(rightBottom, BorderLayout.SOUTH);

        split.setLeftComponent(leftCard);
        split.setRightComponent(rightCard);
        add(split, BorderLayout.CENTER);
    }

    private void updatePreview() {
        try {
            int days = Integer.parseInt(fDays.getText().trim());
            int rate = RATES[cbRoom.getSelectedIndex()];
            int room = days * rate;
            int doc  = days * 500;
            int nur  = days * 300;
            int med  = days * 200;
            int sub  = room + doc + nur + med;
            int tax  = (int)(sub * 0.05);
            int tot  = sub + tax;
            previewLabel.setText("<html><b style='color:#0E7490'>Estimated Total: Rs. " + String.format("%,d", tot) +
                "</b> &nbsp;|&nbsp; <span style='color:#64748B'>Sub: "+String.format("%,d",sub)+" + Tax(5%): "+String.format("%,d",tax)+"</span></html>");
        } catch (NumberFormatException e) {
            previewLabel.setText("<html><div style='color:#64748B;font-style:italic'>Enter valid days to see estimate</div></html>");
        }
    }

    public void refreshHistory() {
        SwingWorker<List<Map<String,String>>,Void> w = new SwingWorker<>(){
            protected List<Map<String,String>> doInBackground() throws Exception { return DAO.getAllInvoices(); }
            protected void done(){
                try {
                    historyModel.setRowCount(0);
                    for (Map<String,String> inv : get()) {
                        historyModel.addRow(new Object[]{
                            inv.get("invoice_no"), inv.get("patient_name"),
                            inv.get("days"), inv.get("room_type"),
                            String.format("%,.0f", Double.parseDouble(inv.get("total"))),
                            inv.get("status"), inv.get("created_at").substring(0,10)
                        });
                    }
                } catch (Exception e){ e.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void generateInvoice() {
        String pid  = fPatientId.getText().trim();
        String dStr = fDays.getText().trim();
        if (pid.isEmpty() || dStr.isEmpty()){ UI.setStatus(statusLabel,"Patient ID and Days are required.",false); return; }
        int days;
        try { days = Integer.parseInt(dStr); if (days<=0) throw new NumberFormatException(); }
        catch (NumberFormatException e){ UI.setStatus(statusLabel,"Enter a valid positive number for days.",false); return; }

        SwingWorker<Map<String,String>,Void> w = new SwingWorker<>(){
            protected Map<String,String> doInBackground() throws Exception { return DAO.getPatientById(pid); }
            protected void done(){
                try {
                    Map<String,String> patient = get();
                    if (patient == null){ UI.setStatus(statusLabel,"Patient ID not found: "+pid,false); return; }
                    int rate = RATES[cbRoom.getSelectedIndex()];
                    String roomLabel = ROOM_LABELS[cbRoom.getSelectedIndex()].split("—")[0].trim();
                    InvoiceDialog dlg = new InvoiceDialog(
                        (Frame)SwingUtilities.getWindowAncestor(BillingPanel.this),
                        patient, days, rate, roomLabel);
                    dlg.setVisible(true);
                    UI.setStatus(statusLabel,"✓ Invoice generated for "+patient.get("full_name"),true);
                    refreshHistory();
                } catch (Exception e){ UI.setStatus(statusLabel,"Error: "+e.getMessage(),false); }
            }
        };
        w.execute();
    }

    private void updateStatus(String status) {
        int row = historyTable.getSelectedRow();
        if (row<0){ UI.setStatusWarn(statusLabel,"Select an invoice first."); return; }
        String invNo = (String)historyModel.getValueAt(row,0);
        try { DAO.updateInvoiceStatus(invNo,status); UI.setStatus(statusLabel,"✓ Status updated to "+status,true); refreshHistory(); }
        catch (Exception e){ UI.setStatus(statusLabel,"Error: "+e.getMessage(),false); }
    }

    private void viewSelectedInvoice() {
        int row = historyTable.getSelectedRow();
        if (row<0){ UI.setStatusWarn(statusLabel,"Select an invoice first."); return; }
        JOptionPane.showMessageDialog(this,
            "Invoice: "   + historyModel.getValueAt(row,0) +
            "\nPatient: "  + historyModel.getValueAt(row,1) +
            "\nTotal: Rs. "+ historyModel.getValueAt(row,4) +
            "\nStatus: "   + historyModel.getValueAt(row,5),
            "Invoice Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
