package shc.db;

import java.sql.*;
import java.io.*;
import java.util.Properties;
import shc.util.ConfigManager;
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
        System.out.println(">>> connect()");
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
    }public void resetConnection() {

    try {

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

    } catch (SQLException ignored) {
    }

    connection = null;
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

        configFile = ConfigManager.getConfigFile();

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
private Connection connectWithoutDatabase() {
    System.out.println(">>> connectWithoutDatabase()");
    try {

        Properties props = loadConfig();

        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "3306");
        String user = props.getProperty("db.user", "root");
        String pass = props.getProperty("db.password", "");

        Class.forName("com.mysql.cj.jdbc.Driver");

        String url =
                "jdbc:mysql://" + host + ":" + port
                        + "/?useSSL=false"
                        + "&allowPublicKeyRetrieval=true"
                        + "&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, pass);

    } catch (Exception e) {

        throw new RuntimeException(e);

    }

}

public void createDatabaseIfNotExists() {
    System.out.println(">>> createDatabaseIfNotExists()");
    Properties props = loadConfig();

    String database =
            props.getProperty("db.name", "sufiyan_health_clinic");

    String sql =
            "CREATE DATABASE IF NOT EXISTS `" + database + "`";

    try (
            Connection conn = connectWithoutDatabase();
            Statement stmt = conn.createStatement()) {

        stmt.executeUpdate(sql);

    } catch (SQLException e) {

        throw new RuntimeException(e);

    }

}
}
