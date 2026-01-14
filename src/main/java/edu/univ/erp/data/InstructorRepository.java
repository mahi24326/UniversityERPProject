package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorRepository {

    private static final String FIND_BY_USER_ID_SQL = """
        SELECT i.instructor_id,
               u.username,
               i.department
        FROM erp_db.instructors i
        JOIN auth_db.users u ON i.user_id = u.user_id
        WHERE i.user_id = ?
    """;

    public Instructor findByUserId(int userId) {

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int instructorId = rs.getInt("instructor_id");
                String username = rs.getString("username");
                String department = rs.getString("department");

                return new Instructor(
                        userId,
                        username,
                        instructorId,
                        department
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}