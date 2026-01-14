package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.NewUserRequest;
import edu.univ.erp.auth.AuthRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLIntegrityConstraintViolationException;



public class AdminRepository {

    public int countStudents() {
        return querySingleInt("SELECT COUNT(*) FROM erp_db.students");
    }

    public int countInstructors() {
        return querySingleInt("SELECT COUNT(*) FROM erp_db.instructors");
    }

    public int countCourses() {
        return querySingleInt("SELECT COUNT(*) FROM erp_db.courses");
    }

    public int countSections() {
        return querySingleInt("SELECT COUNT(*) FROM erp_db.sections");
    }

    /**
     * Read maintenance mode from the ERP DB settings table
     */
    public boolean getMaintenanceMode() {
        String sql = "SELECT value FROM erp_db.settings WHERE setting_key = 'maintenance_mode'";

        try (Connection conn = DBUtil.getERPConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString("value").equalsIgnoreCase("true");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Persistently update maintenance mode flag
     */
    public void setMaintenanceMode(boolean enable) {
        String sql = "UPDATE erp_db.settings SET value = ? WHERE setting_key = 'maintenance_mode'";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, enable ? "true" : "false");
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int querySingleInt(String sql) {
        try (Connection conn = DBUtil.getERPConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String addUser(NewUserRequest req) {
        AuthRepository authRepo = new AuthRepository();

        try (Connection erpConn = DBUtil.getERPConnection()) {

            erpConn.setAutoCommit(false);

            // 1. Insert into auth_db using dedicated repo
            int userId = authRepo.createAuthUser(req);

            // 2. Insert into ERP tables
            if (req.getRole().equals("student")) {

                String sql = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";

                PreparedStatement st = erpConn.prepareStatement(sql);
                st.setInt(1, userId);
                st.setString(2, req.getRollNumber());
                st.setString(3, req.getProgram());
                st.setInt(4, req.getYear());
                st.executeUpdate();

            } else if (req.getRole().equals("instructor")) {

                String sql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";

                PreparedStatement st = erpConn.prepareStatement(sql);
                st.setInt(1, userId);
                st.setString(2, req.getDepartment());
                st.executeUpdate();
            }

            erpConn.commit();
            return "success";

        } catch (SQLIntegrityConstraintViolationException e) {
            return "duplicate";

        } catch (Exception e) {
            e.printStackTrace();
            return "error:" + e.getMessage();
        }
    }

}
