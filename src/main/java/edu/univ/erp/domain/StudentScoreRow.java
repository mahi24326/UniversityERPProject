package edu.univ.erp.domain;

import java.util.HashMap;
import java.util.Map;

public class StudentScoreRow {

    private int enrollmentId;
    private String studentName;
    private Map<Integer, Double> scores = new HashMap<>();

    public StudentScoreRow(int enrollmentId, String studentName) {
        this.enrollmentId = enrollmentId;
        this.studentName = studentName;
    }

    public void addScore(int componentId, Double score) {
        scores.put(componentId, score);
    }

    public Double getScoreFor(int componentId) {
        return scores.getOrDefault(componentId, null);
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public String getStudentName() {
        return studentName;
    }
}
