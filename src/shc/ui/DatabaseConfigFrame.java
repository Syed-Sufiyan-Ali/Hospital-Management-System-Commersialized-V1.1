package shc.ui;
import shc.db.DBConnection;
import shc.db.Schema;
import shc.ui.LoginFrame;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfigFrame extends JFrame {

    JTextField hostField;
    JTextField portField;
    JTextField dbField;
    JTextField userField;
    JPasswordField passwordField;

    JButton testButton;
    JButton saveButton;

    public DatabaseConfigFrame() {

        setTitle("Database Configuration");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        hostField = new JTextField("localhost", 20);
        portField = new JTextField("3306", 20);
        dbField = new JTextField("sufiyan_health_clinic", 20);
        userField = new JTextField("root", 20);
        passwordField = new JPasswordField(20);

        testButton = new JButton("Test Connection");
        saveButton = new JButton("Save");

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Host"), gbc);

        gbc.gridx = 1;
        panel.add(hostField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Port"), gbc);

        gbc.gridx = 1;
        panel.add(portField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Database"), gbc);

        gbc.gridx = 1;
        panel.add(dbField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        panel.add(userField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(testButton, gbc);

        gbc.gridx = 1;
        panel.add(saveButton, gbc);

        add(panel);

        testButton.addActionListener(e -> testConnection());
        saveButton.addActionListener(e -> saveConfiguration());
    }

    private void testConnection() {

        String host = hostField.getText().trim();
        String port = portField.getText().trim();
        String db = dbField.getText().trim();
        String user = userField.getText().trim();
        String pass = new String(passwordField.getPassword());

        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(url, user, pass);

            con.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Database connection successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Connection Failed",
                    JOptionPane.ERROR_MESSAGE);

        }

    }
    private void saveConfiguration() {

    try {

        Properties props = new Properties();

        props.setProperty("db.host", hostField.getText().trim());
        props.setProperty("db.port", portField.getText().trim());
        props.setProperty("db.name", dbField.getText().trim());
        props.setProperty("db.user", userField.getText().trim());
        props.setProperty("db.password",
                new String(passwordField.getPassword()));

        File file = new File("target/classes/db.properties");

        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "Database Configuration");
        }

        // Reset old connection
        DBConnection.getInstance();

        // Try connecting using newly saved config
        DBConnection.getInstance().getConnection();

        Schema.initialize();

        JOptionPane.showMessageDialog(
                this,
                "Configuration saved successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();

        SwingUtilities.invokeLater(() ->
                new LoginFrame().setVisible(true));

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Connection Failed",
                JOptionPane.ERROR_MESSAGE);

    }

}

}
