package edu.univ.erp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;
import java.sql.SQLException;

public class DBUtil {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get connection to Auth database
     */
    public static Connection getAuthConnection() throws SQLException {
        try {
            String url = properties.getProperty("db.auth.url");
            String user = properties.getProperty("db.user");
            String pass = properties.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    /**
     * Get connection to ERP database
     */
    public static Connection getERPConnection() throws SQLException {
        try {
            String url = properties.getProperty("db.erp.url");
            String user = properties.getProperty("db.user");
            String pass = properties.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    /**
     * Legacy method - defaults to Auth DB for backward compatibility
     */
    public static Connection getConnection() throws SQLException {
        return getAuthConnection();
    }

    public static String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE setting_key = ?";
        try (Connection conn = getERPConnection();
             var ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}