package edu.univ.erp.domain;

public class Course {

    private int courseId;
    private String code;
    private String title;
    private int credits;

    // Optional domain fields (useful for dashboards)
    private int totalSections;
    private int totalEnrollments;

    public Course(int courseId, String code, String title, int credits) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.totalSections = 0;
        this.totalEnrollments = 0;
    }

    public Course(int courseId, String code, String title, int credits,
                  int totalSections, int totalEnrollments) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.totalSections = totalSections;
        this.totalEnrollments = totalEnrollments;
    }

    // Getters
    public int getCourseId() {
        return courseId;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public int getTotalSections() {
        return totalSections;
    }

    public int getTotalEnrollments() {
        return totalEnrollments;
    }

    // Optional Setters
    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }

    public void setTotalEnrollments(int totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }
}
