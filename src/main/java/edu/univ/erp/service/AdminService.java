package edu.univ.erp.service;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.AdminSystemStats;
import edu.univ.erp.domain.NewUserRequest;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;

public class AdminService {

    public AdminSystemStats loadSystemStats() {
        int totalStudents = 0;
        int totalInstructors = 0;
        int totalCourses = 0;
        int totalSectionsThisSemester = 0;

        try (Connection conn = DBUtil.getERPConnection()) {

            // Students
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM students")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) totalStudents = rs.getInt(1);
            }

            // Instructors
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM instructors")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) totalInstructors = rs.getInt(1);
            }

            // Courses
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM courses")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) totalCourses = rs.getInt(1);
            }

            // Sections THIS SEMESTER (by semester text)
            String currentSem = determineSemester(LocalDate.now().getMonthValue());

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT semester FROM sections")) {

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String sem = rs.getString("semester");
                    if (sem != null && sem.equalsIgnoreCase(currentSem)) {
                        totalSectionsThisSemester++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AdminSystemStats(
                totalStudents,
                totalInstructors,
                totalCourses,
                totalSectionsThisSemester
        );
    }


    // Determine semester by month
    private String determineSemester(int month) {
        if (month >= 1 && month <= 5) return "Winter";   // Jan–May
        if (month >= 6 && month <= 7) return "Summer";   // Jun–Jul
        return "Monsoon";                                // Aug–Dec
    }


    public void setMaintenanceMode(boolean enable) {
        try (Connection conn = DBUtil.getERPConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE settings SET value=? WHERE setting_key='maintenance_mode'");
            ps.setString(1, enable ? "true" : "false");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String addUser(NewUserRequest req) {

        String username = req.getUsername();
        String rawPassword = req.getRawPassword();
        String role = req.getRole();

        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        try (Connection authConn = DBUtil.getAuthConnection();
             Connection erpConn = DBUtil.getERPConnection()) {

            // 1. Insert into auth_db.users
            PreparedStatement ps = authConn.prepareStatement(
                    "INSERT INTO users (username, password_hash, role, status) VALUES (?,?,?, 'active')",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);

            int affected = ps.executeUpdate();
            if (affected == 0) return "Failed to add user";

            ResultSet rs = ps.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) userId = rs.getInt(1);

            if (userId <= 0) return "User created but could not fetch ID";


            // 2. If STUDENT → insert into students table
            if (role.equalsIgnoreCase("student")) {

                PreparedStatement ps2 = erpConn.prepareStatement(
                        "INSERT INTO students (user_id, roll_no, program, year) VALUES (?,?,?,?)"
                );

                ps2.setInt(1, userId);
                ps2.setString(2, req.getRollNumber() != null ? req.getRollNumber() : ("AUTO" + userId));
                ps2.setString(3, req.getProgram() != null ? req.getProgram() : "Unknown Program");
                ps2.setInt(4, req.getYear() != null ? req.getYear() : 1);

                ps2.executeUpdate();
            }


            // 3. If INSTRUCTOR → insert into instructors table
            if (role.equalsIgnoreCase("instructor")) {

                PreparedStatement ps3 = erpConn.prepareStatement(
                        "INSERT INTO instructors (user_id, department) VALUES (?,?)"
                );

                ps3.setInt(1, userId);
                ps3.setString(2, req.getDepartment() != null ? req.getDepartment() : "Unknown Department");

                ps3.executeUpdate();
            }

            return "success";

        } catch (SQLIntegrityConstraintViolationException e) {
            return "Username already exists";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
