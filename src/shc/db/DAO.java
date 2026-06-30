package shc.db;

import java.sql.*;
import java.util.*;

/**
 * Central DAO — all SQL goes here, never in UI classes.
 */
public class DAO {

    private static Connection conn() { return DBConnection.getInstance().getConnection(); }

    // ═══════════════════════════ AUTH ════════════════════════════════════════

    public static Map<String,String> loginUser(String username, String hashedPw) throws SQLException {
        PreparedStatement ps = conn().prepareStatement(
            "SELECT username, full_name, role, active FROM users WHERE username=? AND password=?");
        ps.setString(1, username); ps.setString(2, hashedPw);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Map<String,String> u=new HashMap<>();
            u.put("username",  rs.getString("username"));
            u.put("full_name", rs.getString("full_name"));
            u.put("role",      rs.getString("role"));
            u.put("active",    rs.getString("active"));
            rs.close(); ps.close(); return u;
        }
        rs.close(); ps.close(); return null;
    }

    public static List<Map<String,String>> getAllUsers() throws SQLException {
        List<Map<String,String>> list=new ArrayList<>();
        Statement st=conn().createStatement();
        ResultSet rs=st.executeQuery("SELECT username,full_name,role,active,created_at FROM users ORDER BY id");
        while (rs.next()) {
            Map<String,String> u=new HashMap<>();
            u.put("username",   rs.getString("username"));
            u.put("full_name",  rs.getString("full_name"));
            u.put("role",       rs.getString("role"));
            u.put("active",     rs.getString("active"));
            u.put("created_at", rs.getString("created_at"));
            list.add(u);
        }
        rs.close(); st.close(); return list;
    }

    public static void saveUser(String username,String hashPw,String fullName,String role) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(
            "INSERT INTO users(username,password,full_name,role) VALUES(?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE password=VALUES(password),full_name=VALUES(full_name),role=VALUES(role)");
        ps.setString(1,username); ps.setString(2,hashPw);
        ps.setString(3,fullName); ps.setString(4,role);
        ps.executeUpdate(); ps.close();
    }

    public static void changePassword(String username, String newHash) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("UPDATE users SET password=? WHERE username=?");
        ps.setString(1,newHash); ps.setString(2,username);
        ps.executeUpdate(); ps.close();
    }

    public static void setUserActive(String username,boolean active) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("UPDATE users SET active=? WHERE username=?");
        ps.setInt(1,active?1:0); ps.setString(2,username);
        ps.executeUpdate(); ps.close();
    }

    public static void deleteUser(String username) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("DELETE FROM users WHERE username=?");
        ps.setString(1,username); ps.executeUpdate(); ps.close();
    }

    // ═══════════════════════════ PATIENTS ════════════════════════════════════

    public static List<Map<String,String>> getAllPatients() throws SQLException {
        return queryPatients("SELECT * FROM patients ORDER BY created_at DESC", new Object[0]);
    }

    public static List<Map<String,String>> searchPatients(String term) throws SQLException {
        return queryPatients(
            "SELECT * FROM patients WHERE patient_id LIKE ? OR full_name LIKE ? OR phone LIKE ? ORDER BY created_at DESC",
            new Object[]{"%"+term+"%","%"+term+"%","%"+term+"%"});
    }

    public static Map<String,String> getPatientById(String pid) throws SQLException {
        List<Map<String,String>> r=queryPatients("SELECT * FROM patients WHERE patient_id=?",new Object[]{pid});
        return r.isEmpty()?null:r.get(0);
    }

    public static void addPatient(Map<String,String> p) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(
            "INSERT INTO patients(patient_id,full_name,age,gender,phone,address,blood_group,diagnosis,doctor,status,admitted_on) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1,p.get("patient_id")); ps.setString(2,p.get("full_name"));
        ps.setInt   (3,Integer.parseInt(p.getOrDefault("age","0")));
        ps.setString(4,p.get("gender"));     ps.setString(5,p.get("phone"));
        ps.setString(6,p.get("address"));    ps.setString(7,p.get("blood_group"));
        ps.setString(8,p.get("diagnosis"));  ps.setString(9,p.get("doctor"));
        ps.setString(10,p.getOrDefault("status","Admitted"));
        ps.setString(11,p.getOrDefault("admitted_on", java.time.LocalDate.now().toString()));
        ps.executeUpdate(); ps.close();
    }

    public static void updatePatient(Map<String,String> p) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(
            "UPDATE patients SET full_name=?,age=?,gender=?,phone=?,address=?,blood_group=?,diagnosis=?,doctor=?,status=? WHERE patient_id=?");
        ps.setString(1,p.get("full_name"));
        ps.setInt   (2,Integer.parseInt(p.getOrDefault("age","0")));
        ps.setString(3,p.get("gender"));   ps.setString(4,p.get("phone"));
        ps.setString(5,p.get("address")); ps.setString(6,p.get("blood_group"));
        ps.setString(7,p.get("diagnosis"));ps.setString(8,p.get("doctor"));
        ps.setString(9,p.get("status"));   ps.setString(10,p.get("patient_id"));
        ps.executeUpdate(); ps.close();
    }

    public static void deletePatient(String pid) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("DELETE FROM patients WHERE patient_id=?");
        ps.setString(1,pid); ps.executeUpdate(); ps.close();
    }

    private static List<Map<String,String>> queryPatients(String sql,Object[] params) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(sql);
        for (int i=0;i<params.length;i++) ps.setObject(i+1,params[i]);
        ResultSet rs=ps.executeQuery();
        List<Map<String,String>> list=new ArrayList<>();
        while (rs.next()) {
            Map<String,String> m=new LinkedHashMap<>();
            m.put("patient_id",  rs.getString("patient_id"));
            m.put("full_name",   rs.getString("full_name"));
            m.put("age",         rs.getString("age"));
            m.put("gender",      rs.getString("gender"));
            m.put("phone",       rs.getString("phone"));
            m.put("address",     rs.getString("address"));
            m.put("blood_group", rs.getString("blood_group"));
            m.put("diagnosis",   rs.getString("diagnosis"));
            m.put("doctor",      rs.getString("doctor"));
            m.put("status",      rs.getString("status"));
            m.put("admitted_on", rs.getString("admitted_on"));
            list.add(m);
        }
        rs.close(); ps.close(); return list;
    }

    // ═══════════════════════════ APPOINTMENTS ════════════════════════════════

    public static List<Map<String,String>> getAllAppointments() throws SQLException {
        return queryAppts("SELECT * FROM appointments ORDER BY appt_date ASC, appt_time ASC",new Object[0]);
    }

    public static List<Map<String,String>> getTodayAppointments() throws SQLException {
        return queryAppts("SELECT * FROM appointments WHERE appt_date=CURDATE() ORDER BY appt_time",new Object[0]);
    }

    public static void addAppointment(Map<String,String> a) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(
            "INSERT INTO appointments(patient_id,patient_name,doctor,appt_date,appt_time,purpose,status) VALUES(?,?,?,?,?,?,?)");
        ps.setString(1,a.get("patient_id")); ps.setString(2,a.get("patient_name"));
        ps.setString(3,a.get("doctor"));     ps.setString(4,a.get("appt_date"));
        ps.setString(5,a.get("appt_time"));  ps.setString(6,a.get("purpose"));
        ps.setString(7,a.getOrDefault("status","Scheduled"));
        ps.executeUpdate(); ps.close();
    }

    public static void updateApptStatus(int id, String status) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("UPDATE appointments SET status=? WHERE id=?");
        ps.setString(1,status); ps.setInt(2,id); ps.executeUpdate(); ps.close();
    }

    public static void deleteAppointment(int id) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("DELETE FROM appointments WHERE id=?");
        ps.setInt(1,id); ps.executeUpdate(); ps.close();
    }

    private static List<Map<String,String>> queryAppts(String sql,Object[] params) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(sql);
        for (int i=0;i<params.length;i++) ps.setObject(i+1,params[i]);
        ResultSet rs=ps.executeQuery(); List<Map<String,String>> list=new ArrayList<>();
        while (rs.next()) {
            Map<String,String> m=new LinkedHashMap<>();
            m.put("id",           String.valueOf(rs.getInt("id")));
            m.put("patient_id",   rs.getString("patient_id"));
            m.put("patient_name", rs.getString("patient_name"));
            m.put("doctor",       rs.getString("doctor"));
            m.put("appt_date",    rs.getString("appt_date"));
            m.put("appt_time",    rs.getString("appt_time"));
            m.put("purpose",      rs.getString("purpose"));
            m.put("status",       rs.getString("status"));
            list.add(m);
        }
        rs.close(); ps.close(); return list;
    }

    // ═══════════════════════════ INVOICES ════════════════════════════════════

    public static void saveInvoice(Map<String,String> inv) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(
            "INSERT INTO invoices(invoice_no,patient_id,patient_name,days,room_type,room_charge,doctor_fee,nursing_fee,medicine_charge,subtotal,tax,total,status) " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setString(1,inv.get("invoice_no")); ps.setString(2,inv.get("patient_id"));
        ps.setString(3,inv.get("patient_name")); ps.setInt(4,Integer.parseInt(inv.get("days")));
        ps.setString(5,inv.get("room_type"));
        ps.setDouble(6,Double.parseDouble(inv.get("room_charge")));
        ps.setDouble(7,Double.parseDouble(inv.get("doctor_fee")));
        ps.setDouble(8,Double.parseDouble(inv.get("nursing_fee")));
        ps.setDouble(9,Double.parseDouble(inv.get("medicine_charge")));
        ps.setDouble(10,Double.parseDouble(inv.get("subtotal")));
        ps.setDouble(11,Double.parseDouble(inv.get("tax")));
        ps.setDouble(12,Double.parseDouble(inv.get("total")));
        ps.setString(13,inv.getOrDefault("status","Unpaid"));
        ps.executeUpdate(); ps.close();
    }

    public static List<Map<String,String>> getAllInvoices() throws SQLException {
        List<Map<String,String>> list=new ArrayList<>();
        ResultSet rs=conn().createStatement().executeQuery(
            "SELECT * FROM invoices ORDER BY created_at DESC");
        while (rs.next()) {
            Map<String,String> m=new LinkedHashMap<>();
            m.put("id",           String.valueOf(rs.getInt("id")));
            m.put("invoice_no",   rs.getString("invoice_no"));
            m.put("patient_id",   rs.getString("patient_id"));
            m.put("patient_name", rs.getString("patient_name"));
            m.put("days",         String.valueOf(rs.getInt("days")));
            m.put("room_type",    rs.getString("room_type"));
            m.put("total",        String.valueOf(rs.getDouble("total")));
            m.put("status",       rs.getString("status"));
            m.put("created_at",   rs.getString("created_at"));
            list.add(m);
        }
        return list;
    }

    public static void updateInvoiceStatus(String invoiceNo, String status) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("UPDATE invoices SET status=? WHERE invoice_no=?");
        ps.setString(1,status); ps.setString(2,invoiceNo); ps.executeUpdate(); ps.close();
    }

    // ═══════════════════════════ MEDICINES ═══════════════════════════════════

    public static List<Map<String,String>> getAllMedicines() throws SQLException {
        List<Map<String,String>> list=new ArrayList<>();
        ResultSet rs=conn().createStatement().executeQuery(
            "SELECT * FROM medicines ORDER BY name");
        while (rs.next()) {
            Map<String,String> m=new LinkedHashMap<>();
            m.put("id",            String.valueOf(rs.getInt("id")));
            m.put("medicine_id",   rs.getString("medicine_id"));
            m.put("name",          rs.getString("name"));
            m.put("category",      rs.getString("category"));
            m.put("stock_qty",     String.valueOf(rs.getInt("stock_qty")));
            m.put("unit",          rs.getString("unit"));
            m.put("unit_price",    String.valueOf(rs.getDouble("unit_price")));
            m.put("reorder_level", String.valueOf(rs.getInt("reorder_level")));
            m.put("expiry_date",   rs.getString("expiry_date"));
            list.add(m);
        }
        return list;
    }

    public static void saveMedicine(Map<String,String> med) throws SQLException {
        PreparedStatement ps=conn().prepareStatement(
            "INSERT INTO medicines(medicine_id,name,category,stock_qty,unit,unit_price,reorder_level,expiry_date) VALUES(?,?,?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE name=VALUES(name),category=VALUES(category),stock_qty=VALUES(stock_qty)," +
            "unit=VALUES(unit),unit_price=VALUES(unit_price),reorder_level=VALUES(reorder_level),expiry_date=VALUES(expiry_date)");
        ps.setString(1,med.get("medicine_id")); ps.setString(2,med.get("name"));
        ps.setString(3,med.get("category"));
        ps.setInt   (4,Integer.parseInt(med.getOrDefault("stock_qty","0")));
        ps.setString(5,med.get("unit"));
        ps.setDouble(6,Double.parseDouble(med.getOrDefault("unit_price","0")));
        ps.setInt   (7,Integer.parseInt(med.getOrDefault("reorder_level","10")));
        ps.setString(8,med.get("expiry_date"));
        ps.executeUpdate(); ps.close();
    }

    public static void deleteMedicine(String medId) throws SQLException {
        PreparedStatement ps=conn().prepareStatement("DELETE FROM medicines WHERE medicine_id=?");
        ps.setString(1,medId); ps.executeUpdate(); ps.close();
    }

    // ═══════════════════════════ DASHBOARD STATS ═════════════════════════════

    public static int countPatients()              throws SQLException { return countOf("SELECT COUNT(*) FROM patients"); }
    public static int countAdmitted()              throws SQLException { return countOf("SELECT COUNT(*) FROM patients WHERE status='Admitted'"); }
    public static int countTodayAppointments()     throws SQLException { return countOf("SELECT COUNT(*) FROM appointments WHERE appt_date=CURDATE()"); }
    public static int countLowStockMedicines()     throws SQLException { return countOf("SELECT COUNT(*) FROM medicines WHERE stock_qty <= reorder_level"); }
    public static double totalRevenue()            throws SQLException {
        ResultSet rs=conn().createStatement().executeQuery("SELECT COALESCE(SUM(total),0) FROM invoices WHERE status='Paid'");
        double v=rs.next()?rs.getDouble(1):0; rs.close(); return v;
    }
    public static double pendingRevenue()          throws SQLException {
        ResultSet rs=conn().createStatement().executeQuery("SELECT COALESCE(SUM(total),0) FROM invoices WHERE status='Unpaid'");
        double v=rs.next()?rs.getDouble(1):0; rs.close(); return v;
    }

    private static int countOf(String sql) throws SQLException {
        ResultSet rs=conn().createStatement().executeQuery(sql);
        int v=rs.next()?rs.getInt(1):0; rs.close(); return v;
    }
}
