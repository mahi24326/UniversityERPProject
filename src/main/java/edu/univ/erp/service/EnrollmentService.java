package edu.univ.erp.service;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.util.DateTimeUtil;

import java.sql.*;
import java.time.LocalDateTime;

public class EnrollmentService {

    /**
     * Drop a course for a student if before deadline
     */
    public static boolean dropCourse(int studentId, String courseCode) throws SQLException {
        String deadlineQuery = "SELECT dd.drop_deadline " +
                "FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "JOIN drop_deadlines dd ON s.section_id = dd.section_id " +
                "WHERE e.student_id = ? AND c.code = ? AND e.status = 'enrolled'";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(deadlineQuery)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, courseCode);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return false;

            LocalDateTime deadline = rs.getTimestamp("drop_deadline").toLocalDateTime();

            if (!DateTimeUtil.isBeforeDeadline(deadline)) {
                return false; // Deadline passed
            }

            // Update enrollment status
            String updateQuery = "UPDATE enrollments e " +
                    "JOIN sections s ON e.section_id = s.section_id " +
                    "JOIN courses c ON s.course_id = c.course_id " +
                    "SET e.status = 'dropped' " +
                    "WHERE e.student_id = ? AND c.code = ? AND e.status = 'enrolled'";

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, studentId);
                updateStmt.setString(2, courseCode);
                int rows = updateStmt.executeUpdate();
                return rows > 0;
            }
        }
    }
}
