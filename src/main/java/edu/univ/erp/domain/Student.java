package edu.univ.erp.domain;

public class Student {

    // From auth_db.users
    private int userId;
    private String username;

    // From erp_db.students
    private int studentId;
    private String rollNumber;
    private String program;
    private int year;

    // Additional dashboard-level derived data (not UI, still domain)
    private int enrolledCourses;
    private double gpa;
    private int totalCredits;

    // Constructor
    public Student(int userId, String username, int studentId, String rollNumber,
                   String program, int year, int enrolledCourses, double gpa,
                   int totalCredits) {

        this.userId = userId;
        this.username = username;
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.program = program;
        this.year = year;
        this.enrolledCourses = enrolledCourses;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getProgram() {
        return program;
    }

    public int getYear() {
        return year;
    }

    public int getEnrolledCourses() {
        return enrolledCourses;
    }

    public double getGpa() {
        return gpa;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    // Setters (optional)
    public void setYear(int year) {
        this.year = year;
    }

    public void setEnrolledCourses(int enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
}