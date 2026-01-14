package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.SectionWithAvailability;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLIntegrityConstraintViolationException;

public class SectionRepository {

    /* ============================================
       Count how many sections instructor teaches
    ============================================ */
    private static final String COUNT_BY_INSTRUCTOR_SQL = """
        SELECT COUNT(*) AS cnt
        FROM erp_db.sections
        WHERE instructor_id = ?
    """;

    public int countByInstructor(int instructorId) {
        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_BY_INSTRUCTOR_SQL)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? rs.getInt("cnt") : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /* ============================================================
       STUDENT — Get sections for a course
    ============================================================ */
    public List<SectionWithAvailability> getSectionsForCourse(int courseId, int studentId) throws SQLException {

        String sql = """
        SELECT 
            c.code,
            c.title,
            c.credits,

            s.section_id,
            s.course_id,
            s.instructor_id,
            u.username AS instructor_name,

            s.day,
            s.time,
            s.room,

            COUNT(e.enrollment_id) AS enrolled,
            s.capacity,

            s.semester,
            s.year,

            CASE
                WHEN EXISTS (
                    SELECT 1 
                    FROM erp_db.enrollments e2
                    WHERE e2.student_id = ?
                      AND e2.section_id = s.section_id
                      AND e2.status='enrolled'
                ) THEN 'Already Enrolled'
                WHEN COUNT(e.enrollment_id) >= s.capacity THEN 'Full'
                ELSE 'Available'
            END AS status

        FROM erp_db.courses c
        JOIN erp_db.sections s ON c.course_id = s.course_id
        JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id
        JOIN auth_db.users u ON i.user_id = u.user_id
        LEFT JOIN erp_db.enrollments e
            ON s.section_id = e.section_id AND e.status='enrolled'

        WHERE c.course_id = ?

        GROUP BY 
            c.code, c.title, c.credits,
            s.section_id, s.course_id, s.instructor_id,
            instructor_name, s.day, s.time,
            s.room, s.capacity,
            s.semester, s.year

        ORDER BY s.day, s.time
        """;

        List<SectionWithAvailability> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new SectionWithAvailability(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),

                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),

                        rs.getString("instructor_name"),
                        rs.getString("day"),
                        rs.getString("time"),

                        rs.getString("room"),
                        rs.getInt("enrolled"),
                        rs.getInt("capacity"),

                        rs.getString("semester"),
                        rs.getInt("year"),

                        rs.getString("status")
                ));
            }
        }

        return list;
    }


    /* ============================================================
       ADMIN — Get all sections of a course
    ============================================================ */
    public List<SectionWithAvailability> getSectionsForAdmin(int courseId) {

        List<SectionWithAvailability> list = new ArrayList<>();

        String sql = """
            SELECT 
                c.code AS courseCode,
                c.title AS courseTitle,
                c.credits,

                s.section_id,
                s.course_id,
                s.instructor_id,
                u.username AS instructor_name,

                s.day,
                s.time,
                s.room,

                (SELECT COUNT(*) FROM erp_db.enrollments e
                  WHERE e.section_id = s.section_id AND e.status='enrolled') AS enrolled,

                s.capacity,
                s.semester,
                s.year

            FROM erp_db.sections s
            JOIN erp_db.courses c ON s.course_id = c.course_id
            JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id
            JOIN auth_db.users u ON i.user_id = u.user_id

            WHERE s.course_id = ?
            ORDER BY s.section_id
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new SectionWithAvailability(
                        rs.getString("courseCode"),
                        rs.getString("courseTitle"),
                        rs.getInt("credits"),

                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),

                        rs.getString("instructor_name"),
                        rs.getString("day"),
                        rs.getString("time"),

                        rs.getString("room"),
                        rs.getInt("enrolled"),
                        rs.getInt("capacity"),

                        rs.getString("semester"),
                        rs.getInt("year"),

                        "Available"     // Admin does not care
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /* ============================================================
       INSTRUCTOR — Get my teaching sections
    ============================================================ */
    public List<SectionWithAvailability> getSectionsForInstructor(int instructorId) {

        List<SectionWithAvailability> list = new ArrayList<>();

        String sql = """
            SELECT 
                c.code,
                c.title,
                c.credits,

                s.section_id,
                s.course_id,
                s.instructor_id,

                u.username AS instructor_name,

                s.day,
                s.time,
                s.room,

                (SELECT COUNT(*) FROM erp_db.enrollments e
                  WHERE e.section_id = s.section_id AND e.status='enrolled') AS enrolled,

                s.capacity,
                s.semester,
                s.year,

                'Teaching' AS status

            FROM erp_db.sections s
            JOIN erp_db.courses c ON c.course_id = s.course_id
            JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id
            JOIN auth_db.users u ON i.user_id = u.user_id

            WHERE s.instructor_id = ?

            ORDER BY c.code, s.section_id
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new SectionWithAvailability(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"),

                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),

                        rs.getString("instructor_name"),
                        rs.getString("day"),
                        rs.getString("time"),

                        rs.getString("room"),
                        rs.getInt("enrolled"),
                        rs.getInt("capacity"),

                        rs.getString("semester"),
                        rs.getInt("year"),

                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /* ============================================================
       Add, Update, Delete remain unchanged
    ============================================================ */

    public String addSection(int courseId, int instructorId, String day, String time,
                             String room, int capacity, String semester, int year) {

        String sql = """
            INSERT INTO sections (course_id, instructor_id, day, time, room, capacity, semester, year)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, instructorId);
            ps.setString(3, day);
            ps.setString(4, time);
            ps.setString(5, room);
            ps.setInt(6, capacity);
            ps.setString(7, semester);
            ps.setInt(8, year);

            ps.executeUpdate();
            return "success";

        } catch (SQLIntegrityConstraintViolationException e) {
            return "duplicate";

        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }


    public String updateSection(int sectionId, int instructorId, String day,
                                String time, String room, int capacity,
                                String semester, int year) {

        String sql = """
            UPDATE sections
            SET instructor_id = ?, day = ?, time = ?, room = ?, capacity = ?, semester = ?, year = ?
            WHERE section_id = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setString(2, day);
            ps.setString(3, time);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setString(6, semester);
            ps.setInt(7, year);
            ps.setInt(8, sectionId);

            ps.executeUpdate();
            return "success";

        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }


    public String deleteSection(int sectionId) {

        String checkSql = "SELECT COUNT(*) FROM erp_db.enrollments WHERE section_id = ? AND status='enrolled'";
        String deleteSql = "DELETE FROM erp_db.sections WHERE section_id = ?";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps1 = conn.prepareStatement(checkSql)) {

            ps1.setInt(1, sectionId);
            ResultSet rs = ps1.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return "error:section has enrolled students";
            }

            try (PreparedStatement ps2 = conn.prepareStatement(deleteSql)) {
                ps2.setInt(1, sectionId);
                ps2.executeUpdate();
            }

            return "success";

        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
}
