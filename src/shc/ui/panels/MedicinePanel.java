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

public class MedicinePanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel statusLabel;
    private List<Map<String,String>> allMeds = new ArrayList<>();

    // Form
    private UI.Field fMedId, fName, fCategory, fStock, fUnit, fPrice, fReorder, fExpiry;
    private JLabel formTitle;
    private UI.PrimaryBtn btnSave;
    private boolean editing = false;

    public MedicinePanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUI();
        refresh();
    }

    private void buildUI() {
        add(UI.pageTitle("Medicine Inventory", "Track stock levels, pricing, and expiry dates"), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(520);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(8);

        // ── Left: Table ─────────────────────────────────────────────────────
        UI.Card leftCard = new UI.Card(14, Color.WHITE);
        leftCard.setLayout(new BorderLayout(0, 10));
        leftCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel lTop = new JPanel(new BorderLayout(8, 0));
        lTop.setOpaque(false);
        JLabel tTitle = new JLabel("Stock List");
        tTitle.setFont(Theme.FONT_HEADING); tTitle.setForeground(Theme.TEXT_H);
        UI.PrimaryBtn btnRefresh = new UI.PrimaryBtn("↻", Theme.PRIMARY);
        btnRefresh.setPreferredSize(new Dimension(36, 32));
        btnRefresh.addActionListener(e -> refresh());
        lTop.add(tTitle,     BorderLayout.WEST);
        lTop.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"Med ID","Name","Category","Stock","Unit","Price (Rs)","Reorder Level","Expiry"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UI.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(55);

        // Highlight low stock rows
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    try {
                        int stock   = Integer.parseInt((String) tableModel.getValueAt(row, 3));
                        int reorder = Integer.parseInt((String) tableModel.getValueAt(row, 6));
                        if (stock <= reorder) {
                            setBackground(new Color(254, 242, 242));
                            setForeground(Theme.DANGER);
                        } else {
                            setBackground(row % 2 == 0 ? Color.WHITE : Theme.TABLE_STRIPE);
                            setForeground(Theme.TEXT_BODY);
                        }
                    } catch (Exception e) {
                        setBackground(row % 2 == 0 ? Color.WHITE : Theme.TABLE_STRIPE);
                        setForeground(Theme.TEXT_BODY);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        });

        // Click row → populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateFormFromRow();
        });

        JPanel lBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        lBottom.setOpaque(false);
        UI.OutlineBtn btnEdit   = new UI.OutlineBtn("✏ Edit",   Theme.PRIMARY);
        UI.OutlineBtn btnDelete = new UI.OutlineBtn("🗑 Delete", Theme.DANGER);
        btnEdit.setPreferredSize(new Dimension(90, 32));
        btnDelete.setPreferredSize(new Dimension(90, 32));
        btnEdit.addActionListener(e -> startEdit());
        btnDelete.addActionListener(e -> deleteSelected());
        lBottom.add(btnEdit);
        lBottom.add(btnDelete);

        leftCard.add(lTop,                  BorderLayout.NORTH);
        leftCard.add(UI.scrollPane(table),  BorderLayout.CENTER);
        leftCard.add(lBottom,               BorderLayout.SOUTH);

        // ── Right: Form ─────────────────────────────────────────────────────
        UI.Card rightCard = new UI.Card(14, Color.WHITE);
        rightCard.setLayout(new BorderLayout(0, 14));
        rightCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formTitle = new JLabel("Add New Medicine");
        formTitle.setFont(Theme.FONT_HEADING);
        formTitle.setForeground(Theme.TEXT_H);

        JPanel form = new JPanel(new GridLayout(8, 1, 0, 10));
        form.setOpaque(false);

        fMedId    = new UI.Field("e.g. MED-001");
        fName     = new UI.Field("Medicine name");
        fCategory = new UI.Field("e.g. Antibiotic, Analgesic...");
        fStock    = new UI.Field("Quantity in stock");
        fUnit     = new UI.Field("e.g. Tablets, Vials, Bottles");
        fPrice    = new UI.Field("Unit price in Rs");
        fReorder  = new UI.Field("Minimum stock level (e.g. 10)");
        fExpiry   = new UI.Field("YYYY-MM-DD");

        form.add(UI.formRow("Medicine ID *", fMedId));
        form.add(UI.formRow("Name *",        fName));
        form.add(UI.formRow("Category",      fCategory));
        form.add(UI.formRow("Stock Qty *",   fStock));
        form.add(UI.formRow("Unit",          fUnit));
        form.add(UI.formRow("Unit Price",    fPrice));
        form.add(UI.formRow("Reorder Level", fReorder));
        form.add(UI.formRow("Expiry Date",   fExpiry));

        statusLabel = UI.statusLabel();

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnSave = new UI.PrimaryBtn("＋ Add Medicine", Theme.SUCCESS);
        UI.OutlineBtn btnClear = new UI.OutlineBtn("↺ Clear", Theme.TEXT_MUTED);
        btnSave.addActionListener(e -> save());
        btnClear.addActionListener(e -> clearForm());
        btnRow.add(btnSave);
        btnRow.add(btnClear);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(btnRow,      BorderLayout.NORTH);
        bottom.add(statusLabel, BorderLayout.SOUTH);

        // Low stock legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        legend.setOpaque(false);
        JLabel legendIcon = new JLabel("⬛");
        legendIcon.setForeground(new Color(254, 202, 202));
        JLabel legendText = new JLabel("Red rows = stock at or below reorder level");
        legendText.setFont(Theme.FONT_SMALL);
        legendText.setForeground(Theme.TEXT_MUTED);
        legend.add(legendIcon);
        legend.add(legendText);

        rightCard.add(formTitle, BorderLayout.NORTH);
        rightCard.add(form,      BorderLayout.CENTER);
        JPanel rightBottom = new JPanel(new BorderLayout(0, 8));
        rightBottom.setOpaque(false);
        rightBottom.add(bottom, BorderLayout.NORTH);
        rightBottom.add(legend, BorderLayout.SOUTH);
        rightCard.add(rightBottom, BorderLayout.SOUTH);

        split.setLeftComponent(leftCard);
        split.setRightComponent(rightCard);
        add(split, BorderLayout.CENTER);
    }

    public void refresh() {
        SwingWorker<List<Map<String,String>>, Void> w = new SwingWorker<>() {
            protected List<Map<String,String>> doInBackground() throws Exception { return DAO.getAllMedicines(); }
            protected void done() {
                try {
                    allMeds = get();
                    tableModel.setRowCount(0);
                    for (Map<String,String> m : allMeds) {
                        tableModel.addRow(new Object[]{
                            m.get("medicine_id"), m.get("name"),      m.get("category"),
                            m.get("stock_qty"),   m.get("unit"),       m.get("unit_price"),
                            m.get("reorder_level"),m.get("expiry_date")
                        });
                    }
                } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
            }
        };
        w.execute();
    }

    private void populateFormFromRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        fMedId.setText((String) tableModel.getValueAt(row, 0));
        fName.setText((String) tableModel.getValueAt(row, 1));
        fCategory.setText((String) tableModel.getValueAt(row, 2));
        fStock.setText((String) tableModel.getValueAt(row, 3));
        fUnit.setText((String) tableModel.getValueAt(row, 4));
        fPrice.setText((String) tableModel.getValueAt(row, 5));
        fReorder.setText((String) tableModel.getValueAt(row, 6));
        String exp = (String) tableModel.getValueAt(row, 7);
        fExpiry.setText(exp == null ? "" : exp);
    }

    private void startEdit() {
        if (table.getSelectedRow() < 0) { UI.setStatusWarn(statusLabel, "Select a medicine first."); return; }
        editing = true;
        fMedId.setEditable(false);
        formTitle.setText("Editing: " + fName.getText());
        btnSave.setText("✓ Save Changes");
    }

    private void save() {
        String id    = fMedId.getText().trim();
        String name  = fName.getText().trim();
        String stock = fStock.getText().trim();
        if (id.isEmpty() || name.isEmpty() || stock.isEmpty()) {
            UI.setStatus(statusLabel, "Medicine ID, Name, and Stock are required.", false); return;
        }
        try { Integer.parseInt(stock); } catch (NumberFormatException e) {
            UI.setStatus(statusLabel, "Stock must be a number.", false); return;
        }

        Map<String,String> med = new LinkedHashMap<>();
        med.put("medicine_id",   id);
        med.put("name",          name);
        med.put("category",      fCategory.getText().trim());
        med.put("stock_qty",     stock);
        med.put("unit",          fUnit.getText().trim());
        med.put("unit_price",    fPrice.getText().trim().isEmpty() ? "0" : fPrice.getText().trim());
        med.put("reorder_level", fReorder.getText().trim().isEmpty() ? "10" : fReorder.getText().trim());
        med.put("expiry_date",   fExpiry.getText().trim().isEmpty() ? null : fExpiry.getText().trim());

        try {
            DAO.saveMedicine(med);
            UI.setStatus(statusLabel, "✓ Medicine saved: " + name, true);
            clearForm();
            refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }

    private void deleteSelected() {
        if (!Security.isAdmin()) { UI.setStatus(statusLabel, "Only Admins can delete medicines.", false); return; }
        int row = table.getSelectedRow();
        if (row < 0) { UI.setStatusWarn(statusLabel, "Select a medicine first."); return; }
        String medId = (String) tableModel.getValueAt(row, 0);
        String medName = (String) tableModel.getValueAt(row, 1);
        int c = JOptionPane.showConfirmDialog(this, "Delete medicine: " + medName + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            DAO.deleteMedicine(medId);
            UI.setStatus(statusLabel, "✓ Deleted: " + medName, true);
            clearForm();
            refresh();
        } catch (Exception e) { UI.setStatus(statusLabel, "Error: " + e.getMessage(), false); }
    }

    private void clearForm() {
        editing = false;
        fMedId.setEditable(true);
        formTitle.setText("Add New Medicine");
        btnSave.setText("＋ Add Medicine");
        fMedId.setText(""); fName.setText(""); fCategory.setText("");
        fStock.setText(""); fUnit.setText(""); fPrice.setText("");
        fReorder.setText(""); fExpiry.setText("");
        table.clearSelection();
    }
}
