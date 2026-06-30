package shc.ui.dialogs;

import shc.db.DAO;
import shc.util.Theme;
import shc.ui.components.UI;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PatientDialog extends JDialog {

    private boolean saved = false;
    private Map<String,String> existing;

    private UI.Field fId, fName, fAge, fPhone, fAddress, fBlood, fDiagnosis, fDoctor, fAdmitDate;
    private JComboBox<String> cbGender, cbStatus;
    private JLabel statusLabel;

    public PatientDialog(Frame owner, Map<String,String> existing) {
        super(owner, existing == null ? "Register New Patient" : "Edit Patient", true);
        this.existing = existing;
        setSize(620, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        buildUI();
        if (existing != null) populate();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);

        // ── Header ──────────────────────────────────────────────────────────
        UI.GradientPanel header = new UI.GradientPanel(Theme.PRIMARY, Theme.PRIMARY_DARK, false);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));
        header.setPreferredSize(new Dimension(0, 64));
        JLabel title = new JLabel(existing == null ? "New Patient Registration" : "Edit Patient Record");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // ── Form ────────────────────────────────────────────────────────────
        UI.Card card = new UI.Card(0, Color.WHITE);
        card.setShadow(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 6, 6, 6);

        fId        = new UI.Field("e.g. P-001");
        fName      = new UI.Field("Full name");
        fAge       = new UI.Field("e.g. 35");
        fPhone     = new UI.Field("e.g. 0300-1234567");
        fAddress   = new UI.Field("Street, City");
        fBlood     = new UI.Field("A+, B-, O+...");
        fDiagnosis = new UI.Field("Primary diagnosis");
        fDoctor    = new UI.Field("Attending doctor name");
        fAdmitDate = new UI.Field("YYYY-MM-DD");

        cbGender = new JComboBox<>(new String[]{"M","F","Other"});
        cbGender.setFont(Theme.FONT_BODY);

        cbStatus = new JComboBox<>(new String[]{"Admitted","Outpatient","Discharged"});
        cbStatus.setFont(Theme.FONT_BODY);

        if (existing != null) fId.setEditable(false);

        // Row 0
        gc.gridy=0; gc.gridx=0; gc.weightx=0.3; card.add(formRow("Patient ID *", fId), gc);
        gc.gridx=1; gc.weightx=0.7; card.add(formRow("Full Name *", fName), gc);

        // Row 1
        gc.gridy=1; gc.gridx=0; gc.weightx=0.2; card.add(formRow("Age *", fAge), gc);
        gc.gridx=1; gc.weightx=0.3; card.add(formRow("Gender *", cbGender), gc);

        // Row 2
        gc.gridy=2; gc.gridx=0; gc.weightx=0.4; card.add(formRow("Phone", fPhone), gc);
        gc.gridx=1; gc.weightx=0.4; card.add(formRow("Blood Group", fBlood), gc);

        // Row 3
        gc.gridy=3; gc.gridx=0; gc.gridwidth=2; gc.weightx=1.0; card.add(formRow("Address", fAddress), gc);
        gc.gridwidth=1;

        // Row 4
        gc.gridy=4; gc.gridx=0; gc.weightx=0.4; card.add(formRow("Diagnosis *", fDiagnosis), gc);
        gc.gridx=1; gc.weightx=0.6; card.add(formRow("Attending Doctor", fDoctor), gc);

        // Row 5
        gc.gridy=5; gc.gridx=0; gc.weightx=0.4; card.add(formRow("Admission Date", fAdmitDate), gc);
        gc.gridx=1; gc.weightx=0.4; card.add(formRow("Status", cbStatus), gc);

        // Filler
        gc.gridy=6; gc.gridx=0; gc.gridwidth=2; gc.weighty=1.0;
        card.add(Box.createVerticalGlue(), gc);

        // ── Footer ──────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)));

        statusLabel = UI.statusLabel();
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        UI.OutlineBtn btnCancel = new UI.OutlineBtn("Cancel", Theme.TEXT_MUTED);
        UI.PrimaryBtn btnSave   = new UI.PrimaryBtn(existing == null ? "Register Patient" : "Save Changes", Theme.SUCCESS);
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnSave.setPreferredSize(new Dimension(160, 36));

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());

        btnRow.add(btnCancel);
        btnRow.add(btnSave);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(btnRow,      BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(card) {{ setBorder(BorderFactory.createEmptyBorder()); }}, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel formRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SUBHEAD);
        lbl.setForeground(Theme.TEXT_BODY);
        p.add(lbl,   BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void populate() {
        fId.setText(existing.getOrDefault("patient_id",""));
        fName.setText(existing.getOrDefault("full_name",""));
        fAge.setText(existing.getOrDefault("age",""));
        fPhone.setText(existing.getOrDefault("phone",""));
        fAddress.setText(existing.getOrDefault("address",""));
        fBlood.setText(existing.getOrDefault("blood_group",""));
        fDiagnosis.setText(existing.getOrDefault("diagnosis",""));
        fDoctor.setText(existing.getOrDefault("doctor",""));
        fAdmitDate.setText(existing.getOrDefault("admitted_on",""));
        cbGender.setSelectedItem(existing.getOrDefault("gender","M"));
        cbStatus.setSelectedItem(existing.getOrDefault("status","Admitted"));
    }

    private void save() {
        String id   = fId.getText().trim();
        String name = fName.getText().trim();
        String age  = fAge.getText().trim();
        String diag = fDiagnosis.getText().trim();

        if (id.isEmpty() || name.isEmpty() || age.isEmpty() || diag.isEmpty()) {
            UI.setStatus(statusLabel, "Patient ID, Name, Age, and Diagnosis are required.", false);
            return;
        }
        try { Integer.parseInt(age); } catch (NumberFormatException e) {
            UI.setStatus(statusLabel, "Age must be a valid number.", false); return;
        }

        Map<String,String> data = new LinkedHashMap<>();
        data.put("patient_id",  id);
        data.put("full_name",   name);
        data.put("age",         age);
        data.put("gender",      (String) cbGender.getSelectedItem());
        data.put("phone",       fPhone.getText().trim());
        data.put("address",     fAddress.getText().trim());
        data.put("blood_group", fBlood.getText().trim());
        data.put("diagnosis",   diag);
        data.put("doctor",      fDoctor.getText().trim());
        data.put("status",      (String) cbStatus.getSelectedItem());
        data.put("admitted_on", fAdmitDate.getText().trim().isEmpty()
            ? java.time.LocalDate.now().toString() : fAdmitDate.getText().trim());

        try {
            if (existing == null) DAO.addPatient(data);
            else                  DAO.updatePatient(data);
            saved = true;
            dispose();
        } catch (Exception e) {
            UI.setStatus(statusLabel, "Error: " + e.getMessage(), false);
        }
    }

    public boolean isSaved() { return saved; }
}
