package edu.univ.erp.domain;

public class SectionWithAvailability {

    private String courseCode;
    private String courseTitle;
    private int credits;

    private int sectionId;
    private int courseId;       // now always populated
    private int instructorId;   // now always populated
    private String instructorName;

    private String day;
    private String time;
    private String schedule;

    private String room;
    private int enrolled;
    private int capacity;

    private String semester;
    private int year;

    private String status;

    /* ===========================================================
       FULL canonical constructor — used by ALL repos now
       =========================================================== */
    public SectionWithAvailability(
            String courseCode,
            String courseTitle,
            int credits,
            int sectionId,
            int courseId,
            int instructorId,
            String instructorName,
            String day,
            String time,
            String room,
            int enrolled,
            int capacity,
            String semester,
            int year,
            String status
    ) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;

        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;

        this.instructorName = instructorName;

        this.day = day;
        this.time = time;
        this.schedule = (day != null && time != null)
                ? day + " " + time
                : "";

        this.room = room;
        this.enrolled = enrolled;
        this.capacity = capacity;

        this.semester = semester;
        this.year = year;

        this.status = status;
    }

    // ===========================================================
    // Helper to split schedule → day & time
    // ===========================================================
    private void parseSchedule(String schedule) {
        if (schedule == null) return;

        String[] parts = schedule.split(" ", 2);
        if (parts.length == 2) {
            this.day = parts[0];
            this.time = parts[1];
        } else {
            this.day = schedule;
            this.time = "";
        }
    }

    // ===========================================================
    // Original GETTERS (unchanged names for UI compatibility)
    // ===========================================================
    public String getcourseCode() { return courseCode; }
    public String getcourseTitle() { return courseTitle; }
    public int getcredits() { return credits; }
    public int getsectionId() { return sectionId; }
    public String getinstructorName() { return instructorName; }
    public String getschedule() { return schedule; }
    public String getroom() { return room; }
    public int getenrolled() { return enrolled; }
    public int getcapacity() { return capacity; }
    public String getstatus() { return status; }


    // ===========================================================
    // NEW GETTERS (admin use)
    // ===========================================================
    public int getInstructorId() { return instructorId; }
    public int getCourseId() { return courseId; }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }

    // ===========================================================
    // Optional setters for admin editing
    // ===========================================================
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setDay(String day) { this.day = day; this.schedule = day + " " + time; }
    public void setTime(String time) { this.time = time; this.schedule = day + " " + time; }
    public void setRoom(String room) { this.room = room; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setYear(int year) { this.year = year; }
}
