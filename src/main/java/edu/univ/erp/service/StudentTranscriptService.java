package edu.univ.erp.service;

import edu.univ.erp.db.DBUtil;

import java.sql.*;
import java.util.*;

public class StudentTranscriptService {

    public static class Row {
        public String courseCode;
        public String courseTitle;
        public String component;
        public Double score;
        public Integer weight;
        public Double finalScore;

        public Row(String courseCode, String courseTitle, String component,
                   Double score, Integer weight, Double finalScore) {
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.component = component;
            this.score = score;
            this.weight = weight;
            this.finalScore = finalScore;
        }
    }

    /** Returns a flat list of rows for transcript CSV */
    public List<Row> getTranscriptRows(int studentId) throws Exception {

        String sql = """
            SELECT 
                c.code AS course_code,
                c.title AS course_title,
                ac.name AS component,
                ac.weight AS weight,
                s.score AS score,
                fg.final_score AS final_score
            FROM enrollments e
            JOIN sections sec ON e.section_id = sec.section_id
            JOIN courses c ON sec.course_id = c.course_id
            LEFT JOIN assessment_components ac 
                ON ac.course_id = c.course_id
            LEFT JOIN assessment_scores s 
                ON s.component_id = ac.component_id
               AND s.enrollment_id = e.enrollment_id
            LEFT JOIN final_grades fg 
                ON fg.enrollment_id = e.enrollment_id
            WHERE e.student_id = ?
            ORDER BY c.code, ac.component_id
        """;

        List<Row> list = new ArrayList<>();

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Row(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("component"),
                        rs.getObject("score") != null ? rs.getDouble("score") : null,
                        rs.getObject("weight") != null ? rs.getInt("weight") : null,
                        rs.getObject("final_score") != null ? rs.getDouble("final_score") : null
                ));
            }
        }

        return list;
    }
}
