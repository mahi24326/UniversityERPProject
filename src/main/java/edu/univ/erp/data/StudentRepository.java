package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.SectionWithAvailability;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    private static final String FIND_BY_USER_ID_SQL = """
        SELECT s.student_id,
               u.username,
               s.roll_no,
               s.program,
               s.year
        FROM erp_db.students s
        JOIN auth_db.users u ON s.user_id = u.user_id
        WHERE s.user_id = ?
    """;

    public Student findByUserId(int userId) {

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int studentId = rs.getInt("student_id");
                String username = rs.getString("username");
                String rollNo = rs.getString("roll_no");
                String program = rs.getString("program");
                int year = rs.getInt("year");

                int enrolledCourses = getEnrolledCourseCount(conn, studentId);
                double gpa = getStudentGPA(conn, studentId);
                int totalCredits = getTotalCredits(conn, studentId);

                return new Student(
                        userId,
                        username,
                        studentId,
                        rollNo,
                        program,
                        year,
                        enrolledCourses,
                        gpa,
                        totalCredits
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /* ======================================================
       Helper methods
    ====================================================== */

    private int getEnrolledCourseCount(Connection conn, int studentId) {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM erp_db.enrollments
            WHERE student_id = ? AND status = 'enrolled'
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("cnt") : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double getStudentGPA(Connection conn, int studentId) {

        String sql = """
            SELECT AVG(score) AS gpa
            FROM erp_db.grades g
            JOIN erp_db.enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.student_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("gpa") : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private int getTotalCredits(Connection conn, int studentId) {

        String sql = """
            SELECT SUM(c.credits) AS totalCredits
            FROM erp_db.enrollments e
            JOIN erp_db.sections s ON e.section_id = s.section_id
            JOIN erp_db.courses c ON s.course_id = c.course_id
            WHERE e.student_id = ? AND e.status='enrolled'
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("totalCredits") : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /* ======================================================
       Browse available sections
    ====================================================== */

    public List<SectionWithAvailability> getAvailableSections(int studentId) throws SQLException {

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
            s.capacity,
            COUNT(e.enrollment_id) AS enrolled,
            s.semester,
            s.year,
            CASE
                WHEN EXISTS (
                    SELECT 1 FROM erp_db.enrollments e2
                    WHERE e2.student_id = ?
                      AND e2.section_id = s.section_id
                      AND e2.status='enrolled'
                ) THEN 'Enrolled'
                WHEN COUNT(e.enrollment_id) >= s.capacity THEN 'Full'
                ELSE 'Available'
            END AS status
        FROM erp_db.courses c
        JOIN erp_db.sections s ON c.course_id = s.course_id
        JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id
        JOIN auth_db.users u ON i.user_id = u.user_id
        LEFT JOIN erp_db.enrollments e
            ON s.section_id = e.section_id AND e.status='enrolled'
        GROUP BY
            c.code, c.title, c.credits,
            s.section_id, s.course_id, s.instructor_id,
            instructor_name, s.day, s.time,
            s.room, s.capacity, s.semester, s.year
        ORDER BY c.code, s.day, s.time
    """;

        List<SectionWithAvailability> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
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


    /* ======================================================
       Course registration
    ====================================================== */

    public boolean registerStudentForSection(int studentId, int sectionId) throws SQLException {

        String sql = """
            INSERT INTO erp_db.enrollments (student_id, section_id, status)
            VALUES (?, ?, 'enrolled')
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;
        }
    }


    /* ======================================================
       NEW: Drop course
    ====================================================== */

    public boolean dropStudentFromSection(int studentId, int sectionId) throws SQLException {

        String sql = """
            UPDATE erp_db.enrollments
            SET status = 'dropped'
            WHERE student_id = ? AND section_id = ? AND status = 'enrolled'
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;
        }
    }
}







//
//package edu.univ.erp.data;
//import edu.univ.erp.domain.Student;
//import edu.univ.erp.domain.SectionWithAvailability;
//import edu.univ.erp.db.DBUtil;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StudentRepository {
//
//    private static final String FIND_BY_USER_ID_SQL = """
//        SELECT s.student_id,
//               u.username,
//               s.roll_no,
//               s.program,
//               s.year
//        FROM erp_db.students s
//        JOIN auth_db.users u ON s.user_id = u.user_id
//        WHERE s.user_id = ?
//    """;
//
//    public Student findByUserId(int userId) {
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement ps = conn.prepareStatement(FIND_BY_USER_ID_SQL)) {
//
//            ps.setInt(1, userId);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                int studentId = rs.getInt("student_id");
//                String username = rs.getString("username");
//                String rollNo = rs.getString("roll_no");
//                String program = rs.getString("program");
//                int year = rs.getInt("year");
//
//                int enrolledCourses = getEnrolledCourseCount(conn, studentId);
//                double gpa = getStudentGPA(conn, studentId);
//                int totalCredits = getTotalCredits(conn, studentId);
//
//                return new Student(
//                        userId,
//                        username,
//                        studentId,
//                        rollNo,
//                        program,
//                        year,
//                        enrolledCourses,
//                        gpa,
//                        totalCredits
//                );
//            }
//
//        } catch (Exception e) { e.printStackTrace(); }
//
//        return null;
//    }
//
//
//    /* ====================== GET SECTIONS ====================== */
//
//    public List<SectionWithAvailability> getAvailableSections(int studentId) throws SQLException {
//
//        String sql = """
//        SELECT
//            c.code,
//            c.title,
//            c.credits,
//            s.section_id,
//            s.course_id,
//            s.instructor_id,
//            u.username AS instructor_name,
//            s.day,
//            s.time,
//            s.room,
//            s.capacity,
//            COUNT(e.enrollment_id) AS enrolled,
//            s.semester,
//            s.year,
//            CASE
//                WHEN EXISTS (
//                    SELECT 1 FROM erp_db.enrollments e2
//                    WHERE e2.student_id = ?
//                      AND e2.section_id = s.section_id
//                      AND e2.status='enrolled'
//                ) THEN 'Enrolled'
//                WHEN COUNT(e.enrollment_id) >= s.capacity THEN 'Full'
//                ELSE 'Available'
//            END AS status
//        FROM erp_db.courses c
//        JOIN erp_db.sections s ON c.course_id = s.course_id
//        JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id
//        JOIN auth_db.users u ON i.user_id = u.user_id
//        LEFT JOIN erp_db.enrollments e
//            ON s.section_id = e.section_id AND e.status='enrolled'
//        GROUP BY
//            c.code, c.title, c.credits,
//            s.section_id, s.course_id, s.instructor_id,
//            instructor_name, s.day, s.time,
//            s.room, s.capacity, s.semester, s.year
//        ORDER BY c.code, s.day, s.time
//        """;
//
//        List<SectionWithAvailability> list = new ArrayList<>();
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                list.add(new SectionWithAvailability(
//                        rs.getString("code"),
//                        rs.getString("title"),
//                        rs.getInt("credits"),
//
//                        rs.getInt("section_id"),
//                        rs.getInt("course_id"),
//                        rs.getInt("instructor_id"),
//
//                        rs.getString("instructor_name"),
//                        rs.getString("day"),
//                        rs.getString("time"),
//
//                        rs.getString("room"),
//                        rs.getInt("enrolled"),
//                        rs.getInt("capacity"),
//
//                        rs.getString("semester"),
//                        rs.getInt("year"),
//
//                        rs.getString("status")
//                ));
//            }
//        }
//
//        return list;
//    }
//
//
//    /* ====================== REGISTER ====================== */
//
//    public boolean registerStudentForSection(int studentId, int sectionId) throws SQLException {
//
//        String sql = """
//            INSERT INTO erp_db.enrollments (student_id, section_id, status)
//            VALUES (?, ?, 'enrolled')
//        """;
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ps.setInt(2, sectionId);
//            return ps.executeUpdate() > 0;
//        }
//    }
//
//
//    /* ====================== DROP ====================== */
//
//    public boolean dropStudentFromSection(int studentId, int sectionId) throws SQLException {
//
//        String sql = """
//            UPDATE erp_db.enrollments
//            SET status = 'dropped'
//            WHERE student_id = ? AND section_id = ? AND status='enrolled'
//        """;
//
//        try (Connection conn = DBUtil.getERPConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, studentId);
//            ps.setInt(2, sectionId);
//            return ps.executeUpdate() > 0;
//        }
//    }
//
//
//    /* ====================== Helpers ====================== */
//
//    private int getEnrolledCourseCount(Connection conn, int studentId) { /* unchanged */ return 0; }
//    private double getStudentGPA(Connection conn, int studentId) { /* unchanged */ return 0; }
//    private int getTotalCredits(Connection conn, int studentId) { /* unchanged */ return 0; }
//}
