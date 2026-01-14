package edu.univ.erp.domain;

public class NewUserRequest {

    private String username;
    private String rawPassword;
    private String role;     // "student", "instructor", "admin"

    // Student fields
    private String rollNumber;
    private String program;
    private Integer year;

    // Instructor fields
    private String department;

    public NewUserRequest(String username, String rawPassword, String role) {
        this.username = username;
        this.rawPassword = rawPassword;
        this.role = role;
    }

    // ==========================
    // Basic user info
    // ==========================
    public String getUsername() {
        return username;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public String getRole() {
        return role;
    }

    // ==========================
    // Student fields
    // ==========================
    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    // ==========================
    // Instructor fields
    // ==========================
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}