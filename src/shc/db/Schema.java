package shc.db;

import java.sql.*;

/**
 * Creates all tables on first run if they don't exist.
 * Run once at application startup.
 */
public class Schema {

    public static void initialize() {
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement()) {

            // Users table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id         INT AUTO_INCREMENT PRIMARY KEY," +
                "  username   VARCHAR(50)  UNIQUE NOT NULL," +
                "  password   VARCHAR(64)  NOT NULL," +  // SHA-256 hex
                "  full_name  VARCHAR(100) NOT NULL," +
                "  role       ENUM('Admin','Doctor','Receptionist','Staff') DEFAULT 'Staff'," +
                "  active     TINYINT(1)   DEFAULT 1," +
                "  created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Patients table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS patients (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  patient_id   VARCHAR(20) UNIQUE NOT NULL," +
                "  full_name    VARCHAR(100) NOT NULL," +
                "  age          INT NOT NULL," +
                "  gender       ENUM('M','F','Other') NOT NULL," +
                "  phone        VARCHAR(20)," +
                "  address      TEXT," +
                "  blood_group  VARCHAR(5)," +
                "  diagnosis    VARCHAR(255) NOT NULL," +
                "  doctor       VARCHAR(100)," +
                "  status       ENUM('Admitted','Outpatient','Discharged') DEFAULT 'Admitted'," +
                "  admitted_on  DATE         DEFAULT (CURRENT_DATE)," +
                "  created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Appointments table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS appointments (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  patient_id   VARCHAR(20) NOT NULL," +
                "  patient_name VARCHAR(100)," +
                "  doctor       VARCHAR(100) NOT NULL," +
                "  appt_date    DATE        NOT NULL," +
                "  appt_time    VARCHAR(10) NOT NULL," +
                "  purpose      VARCHAR(255)," +
                "  status       ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled'," +
                "  created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Invoices table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS invoices (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  invoice_no   VARCHAR(20) UNIQUE NOT NULL," +
                "  patient_id   VARCHAR(20) NOT NULL," +
                "  patient_name VARCHAR(100)," +
                "  days         INT         NOT NULL," +
                "  room_type    VARCHAR(50)," +
                "  room_charge  DECIMAL(10,2)," +
                "  doctor_fee   DECIMAL(10,2)," +
                "  nursing_fee  DECIMAL(10,2)," +
                "  medicine_charge DECIMAL(10,2)," +
                "  subtotal     DECIMAL(10,2)," +
                "  tax          DECIMAL(10,2)," +
                "  total        DECIMAL(10,2)," +
                "  status       ENUM('Paid','Unpaid','Partial') DEFAULT 'Unpaid'," +
                "  created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Medicine/Inventory table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS medicines (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  medicine_id  VARCHAR(20) UNIQUE NOT NULL," +
                "  name         VARCHAR(100) NOT NULL," +
                "  category     VARCHAR(50)," +
                "  stock_qty    INT         DEFAULT 0," +
                "  unit         VARCHAR(20)," +
                "  unit_price   DECIMAL(10,2)," +
                "  reorder_level INT        DEFAULT 10," +
                "  expiry_date  DATE," +
                "  created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Seed default admin if users table is empty
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                String defaultHash = shc.util.Security.sha256("admin123");
                st.executeUpdate(
                    "INSERT INTO users (username, password, full_name, role) VALUES " +
                    "('admin', '" + defaultHash + "', 'Administrator', 'Admin')"
                );
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Schema init error: " + e.getMessage());
        }
    }
}
