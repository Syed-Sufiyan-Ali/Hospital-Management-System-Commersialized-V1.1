package shc.db;

import java.sql.*;
import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Singleton MySQL connection manager.
 * Reads config from db.properties — never hardcode credentials.
 */
public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private static final String CONFIG_FILE = "db.properties";

    private DBConnection() {}

    public static DBConnection getInstance() {
        if (instance == null) instance = new DBConnection();
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }

    private void connect() {
        try {
            Properties props = loadConfig();
            String host = props.getProperty("db.host", "localhost");
            String port = props.getProperty("db.port", "3306");
            String name = props.getProperty("db.name", "sufiyan_health_clinic");
            String user = props.getProperty("db.user", "root");
            String pass = props.getProperty("db.password", "");

            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                    System.out.println("URL = " + url);
                    System.out.println("USER = " + user);
                    System.out.println("PASSWORD LENGTH = " + pass.length());
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver not found.\nPlace mysql-connector.jar in the /lib folder.",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to MySQL database.\n\n" +
                "Please check db.properties and ensure MySQL is running.\n\nError: " + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private Properties loadConfig() {
    Properties props = new Properties();

    try {
        File appDir = new File(
                DBConnection.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                .getParentFile();

        File f = new File(appDir, "db.properties");

        System.out.println("Reading config from:");
        System.out.println(f.getAbsolutePath());

        if (!f.exists()) {
            JOptionPane.showMessageDialog(null,
                    "db.properties not found:\n" + f.getAbsolutePath(),
                    "Configuration Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try (FileInputStream fis = new FileInputStream(f)) {
            props.load(fis);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
                "Failed to load db.properties\n\n" + e.getMessage(),
                "Configuration Error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    return props;
}
}
