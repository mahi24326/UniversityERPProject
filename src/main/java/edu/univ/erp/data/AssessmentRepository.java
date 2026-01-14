package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.AssessmentComponent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentRepository {


    public List<AssessmentComponent> getComponentsForCourse(int courseId) throws Exception {

        String sql = """
            SELECT component_id, course_id, name, weight, max_marks
            FROM assessment_components
            WHERE course_id = ?
            ORDER BY component_id
        """;

        List<AssessmentComponent> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new AssessmentComponent(
                        rs.getInt("component_id"),
                        rs.getInt("course_id"),
                        rs.getString("name"),
                        rs.getInt("weight"),
                        rs.getInt("max_marks")
                ));
            }
        }

        return list;
    }


    public void addComponent(int courseId, String name, int weight, int maxMarks) throws Exception {

        String sql = """
            INSERT INTO assessment_components (course_id, name, weight, max_marks)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setString(2, name);
            ps.setInt(3, weight);
            ps.setInt(4, maxMarks);
            ps.executeUpdate();
        }
    }


    public void deleteComponent(int componentId) throws Exception {

        String sql = "DELETE FROM assessment_components WHERE component_id = ?";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, componentId);
            ps.executeUpdate();
        }
    }

    public void saveScore(int enrollmentId, int componentId, double score) throws Exception {

        String sql = """
            INSERT INTO assessment_scores (enrollment_id, component_id, score)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE score = VALUES(score)
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, componentId);
            ps.setDouble(3, score);

            ps.executeUpdate();
        }
    }


    public double getClassAverage(int sectionId) throws Exception {
        String sql = """
            SELECT AVG(score) AS avg_score
            FROM assessment_scores sc
            JOIN enrollments e ON sc.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("avg_score");
            return 0;
        }
    }

    public double getClassMax(int sectionId) throws Exception {
        String sql = """
            SELECT MAX(score) AS max_score
            FROM assessment_scores sc
            JOIN enrollments e ON sc.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("max_score");
            return 0;
        }
    }

    public double getClassMin(int sectionId) throws Exception {
        String sql = """
            SELECT MIN(score) AS min_score
            FROM assessment_scores sc
            JOIN enrollments e ON sc.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("min_score");
            return 0;
        }
    }

    public int getStudentCount(int sectionId) throws Exception {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM enrollments
            WHERE section_id = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("cnt");
            return 0;
        }
    }

    /* -------------------------
       COMPUTE FINAL SCORE (with max_marks)
    ------------------------- */
    public double computeFinalScore(int enrollmentId, int courseId) throws Exception {

        String sql = """
            SELECT ac.weight, ac.max_marks, sc.score
            FROM assessment_components ac
            LEFT JOIN assessment_scores sc
                ON ac.component_id = sc.component_id
                AND sc.enrollment_id = ?
            WHERE ac.course_id = ?
        """;

        double finalScore = 0;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, courseId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int weight = rs.getInt("weight");
                int maxMarks = rs.getInt("max_marks");
                Double rawScore = (Double) rs.getObject("score");

                if (rawScore == null) continue;
                if (maxMarks <= 0) continue;

                double normalized = rawScore / maxMarks;
                double weighted = normalized * weight;

                finalScore += weighted;
            }
        }

        return finalScore;
    }

    public List<String[]> getGradebookRows(int sectionId) throws Exception {

        String sql = """
        SELECT 
            u.username AS student,
            fg.final_score
        FROM enrollments e
        JOIN students st ON e.student_id = st.student_id
        JOIN auth_db.users u ON st.user_id = u.user_id
        LEFT JOIN final_grades fg ON fg.enrollment_id = e.enrollment_id
        WHERE e.section_id = ?
        ORDER BY u.username
    """;

        List<String[]> rows = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                String student = rs.getString("student");
                Double score = (Double) rs.getObject("final_score");

                rows.add(new String[]{
                        student,
                        score == null ? "" : String.valueOf(score)
                });
            }
        }

        return rows;
    }

}
