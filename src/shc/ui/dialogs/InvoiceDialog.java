package shc.ui.dialogs;

import shc.db.DAO;
import shc.util.Theme;
import shc.ui.components.UI;

import javax.swing.*;
import javax.swing.border.Border;   // <-- YE LINE ADD KARO
import java.awt.*;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.*;



public class InvoiceDialog extends JDialog {

    private JPanel bodyPanel;

    public InvoiceDialog(Frame owner, Map<String,String> patient, int days, int ratePerDay, String roomLabel) {
        super(owner, "Invoice — Sufiyan Health Clinic", true);
        setSize(560, 760);
        setLocationRelativeTo(owner);
        setResizable(false);

        // Calculations
        long roomCharge  = (long) days * ratePerDay;
        long docFee      = (long) days * 500;
        long nursingFee  = (long) days * 300;
        long medCharge   = (long) days * 200;
        long subtotal    = roomCharge + docFee + nursingFee + medCharge;
        long tax         = (long)(subtotal * 0.05);
        long total       = subtotal + tax;

        String invNo  = "SHC-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + (System.currentTimeMillis() % 10000);
        String date   = new SimpleDateFormat("dd MMMM yyyy, hh:mm a").format(new Date());

        // Save to DB
        try {
            Map<String,String> inv = new LinkedHashMap<>();
            inv.put("invoice_no",      invNo);
            inv.put("patient_id",      patient.get("patient_id"));
            inv.put("patient_name",    patient.get("full_name"));
            inv.put("days",            String.valueOf(days));
            inv.put("room_type",       roomLabel);
            inv.put("room_charge",     String.valueOf(roomCharge));
            inv.put("doctor_fee",      String.valueOf(docFee));
            inv.put("nursing_fee",     String.valueOf(nursingFee));
            inv.put("medicine_charge", String.valueOf(medCharge));
            inv.put("subtotal",        String.valueOf(subtotal));
            inv.put("tax",             String.valueOf(tax));
            inv.put("total",           String.valueOf(total));
            inv.put("status",          "Unpaid");
            DAO.saveInvoice(inv);
        } catch (Exception e) { /* non-fatal */ }

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Invoice Header (print-friendly, no gradient) ──────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(14, 116, 144));
        header.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        header.setPreferredSize(new Dimension(0, 120));

        JPanel hLeft = new JPanel(new GridLayout(4,1,0,2));
        hLeft.setOpaque(false);
        JLabel hName = new JLabel("Sufiyan Health Clinic");
        hName.setFont(new Font("Segoe UI",Font.BOLD,20)); hName.setForeground(Color.WHITE);
        JLabel hSub  = new JLabel("Advanced Healthcare & Management");
        hSub.setFont(new Font("Segoe UI",Font.PLAIN,11)); hSub.setForeground(new Color(186,230,253));
        JLabel hInv  = new JLabel("Invoice #: " + invNo);
        hInv.setFont(new Font("Segoe UI",Font.BOLD,12)); hInv.setForeground(new Color(224,231,255));
        JLabel hDate = new JLabel(date);
        hDate.setFont(new Font("Segoe UI",Font.PLAIN,11)); hDate.setForeground(new Color(186,230,253));
        hLeft.add(hName); hLeft.add(hSub); hLeft.add(hInv); hLeft.add(hDate);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        badgePanel.setOpaque(false);
        JLabel badge = new JLabel("INVOICE",SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI",Font.BOLD,12));
        badge.setForeground(new Color(14,116,144));
        badge.setBackground(Color.WHITE); badge.setOpaque(true);
        badge.setPreferredSize(new Dimension(90,32));
        badge.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        badgePanel.add(badge);

        header.add(hLeft,     BorderLayout.WEST);
        header.add(badgePanel,BorderLayout.EAST);

        // ── Body ─────────────────────────────────────────────────────────
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Patient info
        bodyPanel.add(sectionLbl("PATIENT INFORMATION"));
        bodyPanel.add(Box.createVerticalStrut(8));
        JPanel patGrid = new JPanel(new GridLayout(2,4,8,6));
        patGrid.setBackground(new Color(241,245,249));
        patGrid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226,232,240)),
            BorderFactory.createEmptyBorder(12,14,12,14)));
        patGrid.add(infoCell("Patient Name", patient.get("full_name")));
        patGrid.add(infoCell("Patient ID",   patient.get("patient_id")));
        patGrid.add(infoCell("Age",          patient.getOrDefault("age","—") + " yrs"));
        patGrid.add(infoCell("Gender",       genderStr(patient.get("gender"))));
        patGrid.add(infoCell("Diagnosis",    patient.get("diagnosis")));
        patGrid.add(infoCell("Ward / Room",  roomLabel));
        patGrid.add(infoCell("Days Admitted",days + (days==1?" day":" days")));
        patGrid.add(infoCell("Status",       "Admitted → Discharge"));
        bodyPanel.add(patGrid);

        // Billing table
        bodyPanel.add(Box.createVerticalStrut(18));
        bodyPanel.add(sectionLbl("BILLING BREAKDOWN"));
        bodyPanel.add(Box.createVerticalStrut(8));
        bodyPanel.add(buildBillTable(days, ratePerDay, roomCharge, docFee, nursingFee, medCharge));

        // Totals
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(buildTotals(subtotal, tax, total));

        // Footer
        bodyPanel.add(Box.createVerticalStrut(20));
        JSeparator fSep = new JSeparator();
        fSep.setForeground(new Color(226,232,240));
        fSep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        bodyPanel.add(fSep);
        bodyPanel.add(Box.createVerticalStrut(12));
        JLabel fNote = new JLabel("<html><center><i>Thank you for choosing Sufiyan Health Clinic.<br>Billing inquiries: <b>billing@sufiyanhealth.com</b> &nbsp;|&nbsp; <b>021-111-0000</b></i></center></html>", SwingConstants.CENTER);
        fNote.setFont(new Font("Segoe UI",Font.PLAIN,10));
        fNote.setForeground(new Color(148,163,184));
        fNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        bodyPanel.add(fNote);

        // ── Action Bar ───────────────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        actions.setBackground(new Color(241,245,249));
        actions.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(226,232,240)));

        UI.PrimaryBtn btnPrint = new UI.PrimaryBtn("🖨  Print Invoice", Theme.PRIMARY);
        UI.OutlineBtn btnClose = new UI.OutlineBtn("✕  Close", Theme.TEXT_MUTED);
        btnPrint.setPreferredSize(new Dimension(150,36));
        btnClose.setPreferredSize(new Dimension(100,36));
        btnPrint.addActionListener(e -> print());
        btnClose.addActionListener(e -> dispose());
        actions.add(btnPrint); actions.add(btnClose);

        JScrollPane scroll = new JScrollPane(bodyPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        root.add(header,  BorderLayout.NORTH);
        root.add(scroll,  BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JLabel sectionLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,10));
        l.setForeground(new Color(14,116,144));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel infoCell(String label, String value) {
        JPanel p = new JPanel(new GridLayout(2,1,0,2)); p.setOpaque(false);
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Segoe UI",Font.PLAIN,9));  lbl.setForeground(new Color(148,163,184));
        JLabel val = new JLabel(value == null ? "—" : value); val.setFont(new Font("Segoe UI",Font.BOLD,11)); val.setForeground(new Color(15,23,42));
        p.add(lbl); p.add(val); return p;
    }

    private JPanel buildBillTable(int days, int rate, long room, long doc, long nur, long med) {
        JPanel t = new JPanel(new GridBagLayout());
        t.setBackground(Color.WHITE);
        t.setBorder(BorderFactory.createLineBorder(new Color(226,232,240)));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        t.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.gridy=0;
        String[] hdr = {"Description","Unit Rate","Quantity","Amount"};
        for (int i=0;i<4;i++){
            JLabel h=new JLabel(hdr[i]); h.setFont(new Font("Segoe UI",Font.BOLD,11));
            h.setForeground(new Color(14,116,144)); h.setBackground(new Color(241,245,249)); h.setOpaque(true);
            h.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
            h.setHorizontalAlignment(i>0?SwingConstants.RIGHT:SwingConstants.LEFT);
            gc.gridx=i; gc.weightx=(i==0)?1:0; gc.ipadx=(i==0)?100:60; t.add(h,gc);
        }
        addRow(t,1,"Room & Board",   String.format("Rs.%,d",rate), "× "+days+" days", String.format("Rs.%,d",room), false);
        addRow(t,2,"Doctor Fees",    "Rs.500",  "× "+days+" days", String.format("Rs.%,d",doc), false);
        addRow(t,3,"Nursing Care",   "Rs.300",  "× "+days+" days", String.format("Rs.%,d",nur), false);
        addRow(t,4,"Medication",     "Rs.200",  "× "+days+" days", String.format("Rs.%,d",med), true);
        return t;
    }

    private void addRow(JPanel p,int row,String d,String r,String q,String a,boolean last){
        GridBagConstraints gc=new GridBagConstraints(); gc.fill=GridBagConstraints.HORIZONTAL; gc.gridy=row;
        Color bg=row%2==0?Color.WHITE:new Color(248,250,252);
        String[] v={d,r,q,a};
        for (int i=0;i<4;i++){
            JLabel l=new JLabel(v[i]); l.setFont(new Font("Segoe UI",Font.PLAIN,12)); l.setForeground(new Color(51,65,85));
            l.setBackground(bg); l.setOpaque(true);
            Border bb=last?null:BorderFactory.createMatteBorder(0,0,1,0,new Color(241,245,249));
            l.setBorder(BorderFactory.createCompoundBorder(bb,BorderFactory.createEmptyBorder(9,12,9,12)));
            l.setHorizontalAlignment(i>0?SwingConstants.RIGHT:SwingConstants.LEFT);
            gc.gridx=i; gc.weightx=(i==0)?1:0; p.add(l,gc);
        }
    }

    private JPanel buildTotals(long sub, long tax, long total){
        JPanel p=new JPanel(new BorderLayout()); p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel inner=new JPanel(new GridLayout(3,2,0,4)); inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(0,260,0,0));
        addTotalRow(inner,"Subtotal",     String.format("Rs. %,d",sub),  false);
        addTotalRow(inner,"GST / Tax (5%)",String.format("Rs. %,d",tax), false);
        addTotalRow(inner,"TOTAL DUE",    String.format("Rs. %,d",total),true);
        p.add(inner,BorderLayout.EAST); return p;
    }

    private void addTotalRow(JPanel p,String l,String v,boolean bold){
        Font f=bold?new Font("Segoe UI",Font.BOLD,14):new Font("Segoe UI",Font.PLAIN,12);
        Color c=bold?new Color(14,116,144):new Color(51,65,85);
        JLabel ll=new JLabel(l),vl=new JLabel(v);
        ll.setFont(f); ll.setForeground(c); vl.setFont(f); vl.setForeground(c);
        vl.setHorizontalAlignment(SwingConstants.RIGHT);
        if (bold){
            ll.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(226,232,240)));
            vl.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(226,232,240)));
        }
        p.add(ll); p.add(vl);
    }

    private String genderStr(String g){
        if ("M".equals(g)) return "Male";
        if ("F".equals(g)) return "Female";
        return g == null ? "—" : g;
    }

    private void print() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((g, pf, pi) -> {
            if (pi > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            double sx = pf.getImageableWidth()  / bodyPanel.getWidth();
            double sy = pf.getImageableHeight() / bodyPanel.getHeight();
            g2.scale(Math.min(sx, sy), Math.min(sx, sy));
            bodyPanel.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try { job.print(); }
            catch (PrinterException ex) { JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage()); }
        }
    }
}
