package edu.univ.erp.auth;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;
import edu.univ.erp.db.DBUtil;

public class AuthService {

    public static class AuthResult {
        public boolean success;
        public int userId;        // Added this field
        public String role;
        public String errorMessage;

        public AuthResult(boolean success, int userId, String role, String errorMessage) {
            this.success = success;
            this.userId = userId;
            this.role = role;
            this.errorMessage = errorMessage;
        }
    }

    public AuthResult authenticate(String username, String enteredPassword) {
        try (Connection conn = DBUtil.getAuthConnection()) {  // Changed to getAuthConnection()

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, password_hash, role, status FROM users WHERE username=?"
            );
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return new AuthResult(false, -1, null, "User not found.");
            }

            int userId = rs.getInt("user_id");
            String storedHash = rs.getString("password_hash");
            String role = rs.getString("role");
            String status = rs.getString("status");

            // Check if account is active
            if ("inactive".equalsIgnoreCase(status)) {
                return new AuthResult(false, -1, null, "Account is inactive.");
            }

            // Verify password
            if (!BCrypt.checkpw(enteredPassword, storedHash)) {
                return new AuthResult(false, -1, null, "Invalid password.");
            }

            // Update last login timestamp
            updateLastLogin(conn, userId);

            return new AuthResult(true, userId, role, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new AuthResult(false, -1, null, "DB error: " + ex.getMessage());
        }
    }

    /**
     * Update last login timestamp for the user
     */
    private void updateLastLogin(Connection conn, int userId) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET last_login = NOW() WHERE user_id = ?"
            );
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Log but don't fail authentication if this fails
            System.err.println("Warning: Could not update last_login: " + e.getMessage());
        }
    }
}