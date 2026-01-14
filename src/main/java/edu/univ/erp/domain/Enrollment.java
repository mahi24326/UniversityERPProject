package edu.univ.erp.domain;

public class Enrollment {

    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status; // enrolled, dropped, completed

    public Enrollment(int enrollmentId, int studentId, int sectionId, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
