package shc.util;

import java.security.MessageDigest;

public class Security {

    // Active session info — set on login
    public static String currentUser     = "";
    public static String currentFullName = "";
    public static String currentRole     = "";

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }

    public static boolean isAdmin()        { return "Admin".equals(currentRole); }
    public static boolean isDoctor()       { return "Doctor".equals(currentRole); }
    public static boolean isReceptionist() { return "Receptionist".equals(currentRole); }
    public static boolean canModify()      { return isAdmin() || isDoctor() || isReceptionist(); }
}
