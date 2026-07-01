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
            System.out.println("✓ MySQL Driver Loaded");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                    System.out.println("URL = " + url);
                    System.out.println("USER = " + user);
                    System.out.println("PASSWORD LENGTH = " + pass.length());
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
             throw new RuntimeException(e);
        } 
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

   private Properties loadConfig() {

    Properties props = new Properties();

    try {

        File configFile;

        File appDir = new File(
                DBConnection.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                .getParentFile();

        configFile = new File(appDir, "db.properties");

        // Development mode fallback
        if (!configFile.exists()) {
            configFile = new File("target/classes/db.properties");
        }

        System.out.println("Reading config from:");
        System.out.println(configFile.getAbsolutePath());

        if (!configFile.exists()) {
            throw new RuntimeException(
            "db.properties not found:\n" +
            configFile.getAbsolutePath());
            
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        }

    } catch (Exception e) {

        throw new RuntimeException(e);
    }

    return props;
}
}
