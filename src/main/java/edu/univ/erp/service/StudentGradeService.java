package edu.univ.erp.service;

import edu.univ.erp.data.StudentGradeRepository;
import edu.univ.erp.domain.StudentAssessmentRow;

import java.util.List;

public class StudentGradeService {

    private final StudentGradeRepository repo = new StudentGradeRepository();

    public List<StudentAssessmentRow> getAssessmentRows(int enrollmentId, int courseId) throws Exception {
        return repo.getAssessmentRows(enrollmentId, courseId);
    }

    public Double getFinalScore(int enrollmentId) throws Exception {
        return repo.getFinalScore(enrollmentId);
    }
}
