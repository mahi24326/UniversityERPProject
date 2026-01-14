package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.Course;

import java.sql.*;
import java.util.*;

public class CourseRepository {

    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT course_id, code, title, credits FROM courses ORDER BY code";

        List<Course> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits")
                ));
            }
        }
        return list;
    }
}