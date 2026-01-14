package edu.univ.erp.service;

import edu.univ.erp.db.DBUtil;

import java.sql.*;
import java.util.*;

public class TimetableService {

    /** A simple structure for timetable slots */
    public static class CourseSlot {
        public String code;
        public String title;
        public String time;     // e.g. "10:00 AM"
        public String room;
        public String instructor;

        public CourseSlot(String code, String title, String time, String room, String instructor) {
            this.code = code;
            this.title = title;
            this.time = time;
            this.room = room;
            this.instructor = instructor;
        }
    }

    /**
     * Returns a map:
     * "Monday" → List<CourseSlot>
     * "Tuesday" → ...
     */
    public static Map<String, List<CourseSlot>> getStudentTimetable(int studentId) {
        Map<String, List<CourseSlot>> map = new HashMap<>();

        String sql = """
            SELECT 
                c.code,
                c.title,
                s.day,
                s.time,
                s.room,
                u.username AS instructor
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            JOIN courses c ON s.course_id = c.course_id
            JOIN instructors i ON s.instructor_id = i.instructor_id
            JOIN auth_db.users u ON i.user_id = u.user_id
            WHERE e.student_id = ?
              AND e.status = 'enrolled'
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String day = rs.getString("day");
                if (day == null) continue;

                day = normalizeDay(day);

                CourseSlot slot = new CourseSlot(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getString("time"),
                        rs.getString("room"),
                        rs.getString("instructor")
                );

                map.computeIfAbsent(day, k -> new ArrayList<>()).add(slot);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    private static String normalizeDay(String d) {
        d = d.trim().toLowerCase();
        return switch (d) {
            case "mon", "monday" -> "Monday";
            case "tue", "tuesday" -> "Tuesday";
            case "wed", "wednesday" -> "Wednesday";
            case "thu", "thursday" -> "Thursday";
            case "fri", "friday" -> "Friday";
            default -> "Other";
        };
    }
}
