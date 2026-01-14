package edu.univ.erp.domain;

public class AdminSystemStats {

    private int totalStudents;
    private int totalInstructors;
    private int totalCourses;
    private int totalSections;

    // Default constructor required for AdminService
    public AdminSystemStats() {
    }

    // Optional full constructor
    public AdminSystemStats(int totalStudents, int totalInstructors, int totalCourses, int totalSections) {
        this.totalStudents = totalStudents;
        this.totalInstructors = totalInstructors;
        this.totalCourses = totalCourses;
        this.totalSections = totalSections;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalInstructors() {
        return totalInstructors;
    }

    public void setTotalInstructors(int totalInstructors) {
        this.totalInstructors = totalInstructors;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

    public int getTotalSections() {
        return totalSections;
    }

    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }
}
