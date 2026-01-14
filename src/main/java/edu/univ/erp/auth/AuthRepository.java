package edu.univ.erp.auth;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.NewUserRequest;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class AuthRepository {


    public int createAuthUser(NewUserRequest req) throws Exception {

        String hashed = BCrypt.hashpw(req.getRawPassword(), BCrypt.gensalt(12));

        String sql = """
            INSERT INTO users (username, password_hash, role, status)
            VALUES (?, ?, ?, 'active')
        """;

        try (Connection conn = DBUtil.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, req.getUsername());
            ps.setString(2, hashed);
            ps.setString(3, req.getRole());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Failed to retrieve user_id.");
            }

            return rs.getInt(1);
        }
    }


    public boolean deleteUser(String username) throws Exception {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DBUtil.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return true; // fail-safe: treat as existing
        }
    }
}
