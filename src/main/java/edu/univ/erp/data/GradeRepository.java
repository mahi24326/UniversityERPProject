package edu.univ.erp.data;

import edu.univ.erp.db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class GradeRepository {


    public void saveFinalGrade(int enrollmentId, double finalScore, String letterGrade) throws Exception {
        String sql = """
            INSERT INTO final_grades (enrollment_id, final_score, letter_grade)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
              final_score = VALUES(final_score),
              letter_grade = VALUES(letter_grade)
            """;

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setDouble(2, finalScore);
            ps.setString(3, letterGrade);
            ps.executeUpdate();
        }
    }

    /**
     * Convenience overload when you only have a numeric finalScore.
     */
    public void saveFinalGrade(int enrollmentId, double finalScore) throws Exception {
        saveFinalGrade(enrollmentId, finalScore, toLetterGrade(finalScore));
    }

    private String toLetterGrade(double score) {
        // Simple mapping — adapt to your institution rules
        if (score >= 90) return "A+";
        if (score >= 85) return "A";
        if (score >= 80) return "A-";
        if (score >= 75) return "B+";
        if (score >= 70) return "B";
        if (score >= 65) return "C+";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
}
