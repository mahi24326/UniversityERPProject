package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.StudentAssessmentRow;

import java.sql.*;
import java.util.*;

public class StudentGradeRepository {

    public List<StudentAssessmentRow> getAssessmentRows(int enrollmentId, int courseId) throws Exception {

        String sql = """
            SELECT ac.name AS component,
                   ac.weight,
                   ac.max_marks,
                   s.score
            FROM assessment_components ac
            LEFT JOIN assessment_scores s
                ON ac.component_id = s.component_id
               AND s.enrollment_id = ?
            WHERE ac.course_id = ?
            ORDER BY ac.component_id
        """;

        List<StudentAssessmentRow> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, courseId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String component = rs.getString("component");
                int weight = rs.getInt("weight");
                int maxMarks = rs.getInt("max_marks");
                Double score = rs.getObject("score") != null ? rs.getDouble("score") : null;

                list.add(new StudentAssessmentRow(
                        component,
                        score,
                        weight,
                        maxMarks
                ));
            }
        }

        return list;
    }

    public Double getFinalScore(int enrollmentId) throws Exception {

        String sql = """
            SELECT final_score
            FROM final_grades
            WHERE enrollment_id = ?
        """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getObject("final_score") != null ? rs.getDouble("final_score") : null;
        }
        return null;
    }
}
