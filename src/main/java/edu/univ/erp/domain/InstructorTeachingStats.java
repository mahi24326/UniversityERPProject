package edu.univ.erp.domain;

public class InstructorTeachingStats {

    private int totalSections;
    private int totalStudents;
    private double avgClassSize;
    private int pendingGrades;

    public InstructorTeachingStats(int totalSections, int totalStudents,
                                   double avgClassSize, int pendingGrades) {
        this.totalSections = totalSections;
        this.totalStudents = totalStudents;
        this.avgClassSize = avgClassSize;
        this.pendingGrades = pendingGrades;
    }

    public int getTotalSections() {
        return totalSections;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public double getAvgClassSize() {
        return avgClassSize;
    }

    public int getPendingGrades() {
        return pendingGrades;
    }
}