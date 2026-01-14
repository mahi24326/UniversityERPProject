package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.StudentScoreRow;
import edu.univ.erp.domain.SectionItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class EnrollmentRepository {

    private static final String COUNT_STUDENTS_SQL = """
        SELECT COUNT(*) AS cnt
        FROM erp_db.enrollments e
        JOIN erp_db.sections s ON e.section_id = s.section_id
        WHERE s.instructor_id = ?
        AND e.status = 'enrolled'
    """;

    private static final String AVG_CLASS_SIZE_SQL = """
        SELECT AVG(student_count) AS avg_size FROM (
           SELECT COUNT(*) AS student_count
           FROM erp_db.enrollments e
           JOIN erp_db.sections s ON e.section_id = s.section_id
           WHERE s.instructor_id = ?
           GROUP BY e.section_id
        ) AS class_sizes
    """;

    private static final String COUNT_PENDING_GRADES_SQL = """
        SELECT COUNT(*) AS cnt
        FROM erp_db.grades g
        JOIN erp_db.enrollments e ON g.enrollment_id = e.enrollment_id
        JOIN erp_db.sections s ON e.section_id = s.section_id
        WHERE s.instructor_id = ?
        AND g.final_grade IS NULL
    """;

    public int countStudentsForInstructor(int instructorId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_STUDENTS_SQL)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double averageClassSize(int instructorId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(AVG_CLASS_SIZE_SQL)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_size");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int countPendingGrades(int instructorId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_PENDING_GRADES_SQL)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<StudentScoreRow> getStudentsWithScores(int sectionId) throws Exception {

        String sql = """
        SELECT 
            e.enrollment_id,
            u.username AS student_name,
            ac.component_id,
            sc.score
        FROM enrollments e
        JOIN students st ON st.student_id = e.student_id
        JOIN auth_db.users u ON st.user_id = u.user_id
        LEFT JOIN assessment_scores sc ON e.enrollment_id = sc.enrollment_id
        LEFT JOIN assessment_components ac ON sc.component_id = ac.component_id
        WHERE e.section_id = ?
        ORDER BY u.username
        """;

        Map<Integer, StudentScoreRow> map = new LinkedHashMap<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int enrollmentId = rs.getInt("enrollment_id");
                String name = rs.getString("student_name");

                map.putIfAbsent(enrollmentId, new StudentScoreRow(enrollmentId, name));

                Integer componentId = (Integer) rs.getObject("component_id");
                Double score = (Double) rs.getObject("score");

                if (componentId != null && score != null)
                    map.get(enrollmentId).addScore(componentId, score);
            }
        }

        return new ArrayList<>(map.values());
    }

    public int getEnrollmentId(int sectionId, String studentName) throws Exception {

        String sql = """
        SELECT e.enrollment_id
        FROM enrollments e
        JOIN students st ON st.student_id = e.student_id
        JOIN auth_db.users u ON st.user_id = u.user_id
        WHERE e.section_id = ? AND u.username = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setString(2, studentName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("enrollment_id");
        }

        return -1;
    }

    public List<SectionItem> getSectionsForInstructor(int instructorId) throws Exception {

        String sql = """
        SELECT 
            s.section_id,
            s.course_id,
            CONCAT(c.code, ' - ', c.title, ' (', s.semester, ' ', s.year, ')') AS label
        FROM sections s
        JOIN courses c ON s.course_id = c.course_id
        WHERE s.instructor_id = ?
        ORDER BY s.year DESC, s.semester DESC
        """;

        List<SectionItem> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new SectionItem(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getString("label")
                ));
            }
        }

        return list;
    }



    public int getCourseIdForSection(int sectionId) throws Exception {

        String sql = """
        SELECT course_id
        FROM sections
        WHERE section_id = ?
    """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("course_id");
            }
        }

        throw new Exception("Course ID not found for section " + sectionId);
    }





}