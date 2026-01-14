package edu.univ.erp.domain;

import java.time.LocalDate;

public class Section {

    private int sectionId;
    private int courseId;
    private int instructorId;
    private String day;
    private String time;
    private String room;
    private int capacity;
    private String semester;
    private int year;
    private LocalDate semesterStart; // computed from semester + year

    // Constructor with all fields except semesterStart (auto-calculated)
    public Section(int sectionId, int courseId, int instructorId, String day,
                   String time, String room, int capacity, String semester, int year) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.day = day;
        this.time = time;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
        this.semesterStart = calculateSemesterStart(semester, year);
    }

    // Empty constructor for DAO / form usage
    public Section() {}

    // Automatically calculate semester start date
    private LocalDate calculateSemesterStart(String semester, int year) {
        return switch (semester.toLowerCase()) {
            case "winter" -> LocalDate.of(year, 1, 1);
            case "summer" -> LocalDate.of(year, 6, 1);
            case "monsoon" -> LocalDate.of(year, 8, 1);
            default -> throw new IllegalArgumentException("Unknown semester: " + semester);
        };
    }

    // ----------- Getters & Setters -----------

    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) {
        this.semester = semester;
        if (this.year > 0) this.semesterStart = calculateSemesterStart(semester, this.year);
    }

    public int getYear() { return year; }
    public void setYear(int year) {
        this.year = year;
        if (this.semester != null) this.semesterStart = calculateSemesterStart(this.semester, year);
    }

    public LocalDate getSemesterStart() { return semesterStart; }

    // Optional setter in case DAO needs to set it directly
    public void setSemesterStart(LocalDate semesterStart) {
        this.semesterStart = semesterStart;
    }
}
