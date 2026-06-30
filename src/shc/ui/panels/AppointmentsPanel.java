package shc.ui.panels;

import shc.db.DAO;
import shc.util.Theme;
import shc.ui.components.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AppointmentsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;

    // Form fields
    private UI.Field fPatientId, fPatientName, fDoctor, fDate, fTime, fPurpose;
    private JComboBox<String> cbStatus;

    public AppointmentsPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        refresh();
    }

    private void buildUI() {
        add(UI.pageTitle("Appointments", "Schedule, track, and manage patient appointments"), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(460);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(8);
        split.setOpaque(false);

        // ── Left: Table ──────────────────────────────────────────────────
        String[] cols = {"ID","Patient ID","Patient Name","Doctor","Date","Time","Purpose","Status"};
        tableModel = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){ return false; } };
        table = new JTable(tableModel);
        UI.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        split.setLeftComponent(UI.scrollPane(table));

        // ── Right: Add/Edit form ─────────────────────────────────────────
        UI.Card formCard = new UI.Card(14, Color.WHITE);
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel formTitle = new JLabel("Schedule Appointment");
        formTitle.setFont(Theme.FONT_HEADING); formTitle.setForeground(Theme.TEXT_H);

        JPanel form = new JPanel(new GridLayout(7, 1, 0, 10));
        form.setOpaque(false);

        fPatientId   = new UI.Field("Patient ID");
        fPatientName = new UI.Field("Patient full name");
        fDoctor      = new UI.Field("Doctor name");
        fDate        = new UI.Field("YYYY-MM-DD");
        fTime        = new UI.Field("e.g. 10:00 AM");
        fPurpose     = new UI.Field("Reason for visit");
        cbStatus     = new JComboBox<>(new String[]{"Scheduled","Completed","Cancelled"});
        cbStatus.setFont(Theme.FONT_BODY);

        form.add(UI.formRow("Patient ID",   fPatientId));
        form.add(UI.formRow("Patient Name", fPatientName));
        form.add(UI.formRow("Doctor",       fDoctor));
        form.add(UI.formRow("Date",         fDate));
        form.add(UI.formRow("Time",         fTime));
        form.add(UI.formRow("Purpose",      fPurpose));
        form.add(UI.formRow("Status",       cbStatus));

        statusLabel = UI.statusLabel();

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        UI.PrimaryBtn btnSave   = new UI.PrimaryBtn("＋ Schedule", Theme.SUCCESS);
        UI.OutlineBtn btnDelete = new UI.OutlineBtn("🗑 Delete Selected", Theme.DANGER);
        btnSave.addActionListener(e -> saveAppointment());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRow.add(btnSave); btnRow.add(btnDelete);

        JPanel formBottom = new JPanel(new BorderLayout(0,8));
        formBottom.setOpaque(false);
        formBottom.add(btnRow,      BorderLayout.NORTH);
        formBottom.add(statusLabel, BorderLayout.SOUTH);

        formCard.add(formTitle,  BorderLayout.NORTH);
        formCard.add(form,       BorderLayout.CENTER);
        formCard.add(formBottom, BorderLayout.SOUTH);

        split.setRightComponent(formCard);

        // Table row click populates form for status update
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow()>=0) {
                int row = table.getSelectedRow();
                fPatientId.setText((String)tableModel.getValueAt(row,1));
                fPatientName.setText((String)tableModel.getValueAt(row,2));
                fDoctor.setText((String)tableModel.getValueAt(row,3));
                fDate.setText((String)tableModel.getValueAt(row,4));
                fTime.setText((String)tableModel.getValueAt(row,5));
                fPurpose.setText((String)tableModel.getValueAt(row,6));
                cbStatus.setSelectedItem(tableModel.getValueAt(row,7));
            }
        });

        add(split, BorderLayout.CENTER);
    }

    public void refresh() {
        SwingWorker<List<Map<String,String>>,Void> w = new SwingWorker<>(){
            protected List<Map<String,String>> doInBackground() throws Exception { return DAO.getAllAppointments(); }
            protected void done(){
                try {
                    tableModel.setRowCount(0);
                    for (Map<String,String> a : get()) {
                        tableModel.addRow(new Object[]{
                            a.get("id"), a.get("patient_id"), a.get("patient_name"),
                            a.get("doctor"), a.get("appt_date"), a.get("appt_time"),
                            a.get("purpose"), a.get("status")
                        });
                    }
                } catch (Exception e){ e.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void saveAppointment() {
        String pid  = fPatientId.getText().trim();
        String doc  = fDoctor.getText().trim();
        String date = fDate.getText().trim();
        String time = fTime.getText().trim();
        if (pid.isEmpty()||doc.isEmpty()||date.isEmpty()||time.isEmpty()) {
            UI.setStatus(statusLabel,"Patient ID, Doctor, Date, and Time are required.",false); return;
        }

        // If row selected, update status instead of new insert
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = Integer.parseInt((String)tableModel.getValueAt(row,0));
            try {
                DAO.updateApptStatus(id, (String)cbStatus.getSelectedItem());
                UI.setStatus(statusLabel,"✓ Status updated.",true);
                clearForm(); refresh(); return;
            } catch (Exception e){ UI.setStatus(statusLabel,"Error: "+e.getMessage(),false); return; }
        }

        Map<String,String> a = new LinkedHashMap<>();
        a.put("patient_id",   pid);
        a.put("patient_name", fPatientName.getText().trim());
        a.put("doctor",       doc);
        a.put("appt_date",    date);
        a.put("appt_time",    time);
        a.put("purpose",      fPurpose.getText().trim());
        a.put("status",       (String)cbStatus.getSelectedItem());
        try {
            DAO.addAppointment(a);
            UI.setStatus(statusLabel,"✓ Appointment scheduled.",true);
            clearForm(); refresh();
        } catch (Exception e){ UI.setStatus(statusLabel,"Error: "+e.getMessage(),false); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0){ UI.setStatus(statusLabel,"Select a row first.",false); return; }
        int id = Integer.parseInt((String)tableModel.getValueAt(row,0));
        int c  = JOptionPane.showConfirmDialog(this,"Delete this appointment?","Confirm",JOptionPane.YES_NO_OPTION);
        if (c!=JOptionPane.YES_OPTION) return;
        try { DAO.deleteAppointment(id); UI.setStatus(statusLabel,"✓ Appointment deleted.",true); clearForm(); refresh(); }
        catch (Exception e){ UI.setStatus(statusLabel,"Error: "+e.getMessage(),false); }
    }

    private void clearForm(){
        fPatientId.setText(""); fPatientName.setText(""); fDoctor.setText("");
        fDate.setText(""); fTime.setText(""); fPurpose.setText("");
        cbStatus.setSelectedIndex(0); table.clearSelection();
    }
}
