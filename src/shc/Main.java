package shc;

import shc.db.DBConnection;
import shc.db.Schema;
import shc.ui.LoginFrame;

import javax.swing.*;

/**
 * ╔══════════════════════════════════════════╗
 *  Sufiyan Health Clinic — HMS
 *  Entry point: initialises DB then launches UI
 * ╚══════════════════════════════════════════╝
 */
public class Main {
    public static void main(String[] args) {
        // System look & feel (looks native on Windows)
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Override a few global defaults for consistent rendering
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("ScrollBar.width", 8);

        // Splash / init on background thread, then show login
        SwingWorker<Void, Void> init = new SwingWorker<>() {
            protected Void doInBackground() {
                DBConnection.getInstance().getConnection(); // connect + auto-create DB if needed
                Schema.initialize();                        // create tables + seed admin
                return null;
            }
            protected void done() {
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        };
        init.execute();
    }
}
