package shc.ui.panels;

import shc.db.DAO;
import shc.util.Theme;
import shc.util.Security;
import shc.ui.components.UI;
import shc.ui.dialogs.PatientDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PatientsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable            table;
    private UI.Field          searchField;
    private JLabel            statusLabel;
    private List<Map<String,String>> allPatients = new ArrayList<>();

    public PatientsPanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ── Top Bar ────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        topBar.add(UI.pageTitle("Patients", "Register, view, edit, and discharge patients"), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        searchField = new UI.Field("Search by name, ID, or phone...");
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.addActionListener(e -> doSearch());

        UI.PrimaryBtn btnSearch = new UI.PrimaryBtn("Search", Theme.PRIMARY);
        btnSearch.setPreferredSize(new Dimension(90, 36));
        btnSearch.addActionListener(e -> doSearch());

        UI.PrimaryBtn btnAdd = new UI.PrimaryBtn("＋ New Patient", Theme.SUCCESS);
        btnAdd.setPreferredSize(new Dimension(140, 36));
        btnAdd.addActionListener(e -> openAddDialog());

        UI.PrimaryBtn btnRefresh = new UI.PrimaryBtn("↻", Theme.PRIMARY);
        btnRefresh.setPreferredSize(new Dimension(40, 36));
        btnRefresh.setToolTipText("Refresh");
        btnRefresh.addActionListener(e -> refresh());

        actions.add(searchField);
        actions.add(btnSearch);
        actions.add(btnAdd);
        actions.add(btnRefresh);
        topBar.add(actions, BorderLayout.EAST);

        // ── Table ──────────────────────────────────────────────────────────
        String[] cols = {"Patient ID","Full Name","Age","Gender","Phone","Blood","Diagnosis","Doctor","Status","Admitted"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UI.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(180);

        // Double click to edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openEditDialog();
            }
        });

        JScrollPane sp = UI.scrollPane(table);

        // ── Bottom Bar ─────────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        statusLabel = UI.statusLabel();
        statusLabel.setForeground(Theme.TEXT_MUTED);

        JPanel bottomBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottomBtns.setOpaque(false);

        UI.OutlineBtn btnEdit     = new UI.OutlineBtn("✏  Edit",     Theme.PRIMARY);
        UI.OutlineBtn btnDischarge= new UI.OutlineBtn("✔  Discharge", Theme.SUCCESS);
        UI.OutlineBtn btnDelete   = new UI.OutlineBtn("🗑  Delete",   Theme.DANGER);

        btnEdit.setPreferredSize(new Dimension(100, 34));
        btnDischarge.setPreferredSize(new Dimension(120, 34));
        btnDelete.setPreferredSize(new Dimension(100, 34));

        btnEdit.addActionListener(e -> openEditDialog());
        btnDischarge.addActionListener(e -> dischargeSelected());
        btnDelete.addActionListener(e -> deleteSelected());

        bottomBtns.add(btnEdit);
        bottomBtns.add(btnDischarge);
        bottomBtns.add(btnDelete);

        bottomBar.add(statusLabel, BorderLayout.WEST);
        bottomBar.add(bottomBtns, BorderLayout.EAST);

        add(topBar,    BorderLayout.NORTH);
        add(sp,        BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void refresh() {
        SwingWorker<List<Map<String,String>>, Void> w = new SwingWorker<>() {
            protected List<Map<String,String>> doInBackground() throws Exception {
                return DAO.getAllPatients();
            }
            protected void done() {
                try {
                    allPatients = get();
                    populateTable(allPatients);
                    statusLabel.setText("  " + allPatients.size() + " patient(s) found");
                    statusLabel.setForeground(Theme.TEXT_MUTED);
                } catch (Exception e) {
                    UI.setStatus(statusLabel, "Error loading patients: " + e.getMessage(), false);
                }
            }
        };
        w.execute();
    }

    private void doSearch() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) { refresh(); return; }
        SwingWorker<List<Map<String,String>>,Void> w = new SwingWorker<>(){
            protected List<Map<String,String>> doInBackground() throws Exception { return DAO.searchPatients(term); }
            protected void done() {
                try { List<Map<String,String>> r=get(); populateTable(r); statusLabel.setText("  "+r.size()+" result(s) for \""+term+"\""); statusLabel.setForeground(Theme.TEXT_MUTED); }
                catch (Exception e) { UI.setStatus(statusLabel,"Search error: "+e.getMessage(),false); }
            }
        };
        w.execute();
    }

    private void populateTable(List<Map<String,String>> data) {
        tableModel.setRowCount(0);
        for (Map<String,String> p : data) {
            tableModel.addRow(new Object[]{
                p.get("patient_id"), p.get("full_name"), p.get("age"),
                p.get("gender"),     p.get("phone"),     p.get("blood_group"),
                p.get("diagnosis"),  p.get("doctor"),    p.get("status"),
                p.get("admitted_on")
            });
        }
    }

    private Map<String,String> getSelectedPatient() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String pid = (String) tableModel.getValueAt(row, 0);
        for (Map<String,String> p : allPatients) if (pid.equals(p.get("patient_id"))) return p;
        return null;
    }

    private void openAddDialog() {
        PatientDialog dlg = new PatientDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) refresh();
    }

    private void openEditDialog() {
        Map<String,String> p = getSelectedPatient();
        if (p == null) { UI.setStatus(statusLabel, "Select a patient first.", false); return; }
        PatientDialog dlg = new PatientDialog((Frame) SwingUtilities.getWindowAncestor(this), p);
        dlg.setVisible(true);
        if (dlg.isSaved()) refresh();
    }

    private void dischargeSelected() {
        Map<String,String> p = getSelectedPatient();
        if (p == null) { UI.setStatus(statusLabel, "Select a patient first.", false); return; }
        if ("Discharged".equals(p.get("status"))) { UI.setStatusWarn(statusLabel, "Patient is already discharged."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Mark " + p.get("full_name") + " as Discharged?", "Discharge Patient", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            p.put("status", "Discharged");
            DAO.updatePatient(p);
            UI.setStatus(statusLabel, "✓ Patient discharged successfully.", true);
            refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }

    private void deleteSelected() {
        if (!Security.isAdmin()) { UI.setStatus(statusLabel, "Only Admins can delete patient records.", false); return; }
        Map<String,String> p = getSelectedPatient();
        if (p == null) { UI.setStatus(statusLabel, "Select a patient first.", false); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "Permanently delete patient: " + p.get("full_name") + " (" + p.get("patient_id") + ")?\n\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            DAO.deletePatient(p.get("patient_id"));
            UI.setStatus(statusLabel, "✓ Patient deleted.", true);
            refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }
}
