package shc;

import shc.db.DBConnection;
import shc.db.Schema;
import shc.ui.DatabaseConfigFrame;
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Override a few global defaults
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("ScrollBar.width", 8);

        SwingWorker<Void, Void> init = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                try {
                    DBConnection.getInstance().getConnection();
                    Schema.initialize();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
                return null;
            }

            @Override
protected void done() {

    try {

        get();

        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true));

    } catch (Exception ex) {

        // No debug popup in production
        SwingUtilities.invokeLater(() ->
                new DatabaseConfigFrame().setVisible(true));

    }

}
        };

        init.execute();

    }

}
