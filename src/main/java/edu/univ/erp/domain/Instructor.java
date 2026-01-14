package edu.univ.erp.domain;

public class Instructor {

    // From auth_db.users
    private int userId;
    private String username;

    // From erp_db.instructors
    private int instructorId;
    private String department;

    // Optional dashboard-level fields
    private int coursesTaught;
    private double rating;

    public Instructor(int userId, String username, int instructorId, String department,
                      int coursesTaught, double rating)
    {
        this.userId = userId;
        this.username = username;
        this.instructorId = instructorId;
        this.department = department;
        this.coursesTaught = coursesTaught;
        this.rating = rating;
    }

    // Minimal constructor (if you're not using extra fields yet)
    public Instructor(int userId, String username, int instructorId, String department)
    {
        this(userId, username, instructorId, department, 0, 0.0);
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public String getDepartment() {
        return department;
    }

    public int getCoursesTaught() {
        return coursesTaught;
    }

    public double getRating() {
        return rating;
    }

    // Setters (optional)
    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCoursesTaught(int coursesTaught) {
        this.coursesTaught = coursesTaught;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}