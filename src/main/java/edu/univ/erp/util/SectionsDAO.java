package edu.univ.erp.util;

import edu.univ.erp.db.DBUtil;
import edu.univ.erp.domain.Section;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SectionsDAO {


    private LocalDate calculateSemesterStart(String semester, int year) {
        switch (semester.toLowerCase()) {
            case "winter":
                return LocalDate.of(year, 1, 1);
            case "summer":
                return LocalDate.of(year, 6, 1);
            case "monsoon":
                return LocalDate.of(year, 8, 1);
            default:
                throw new IllegalArgumentException("Unknown semester: " + semester);
        }
    }


    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();

        String sql = "SELECT section_id, course_id, instructor_id, day, time, room, capacity, semester, year "
                + "FROM sections";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Section s = new Section(
                        rs.getInt("section_id"),
                        rs.getInt("course_id"),
                        rs.getInt("instructor_id"),
                        rs.getString("day"),
                        rs.getString("time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getString("semester"),
                        rs.getInt("year")
                );

                // Compute semester start automatically
                LocalDate start = calculateSemesterStart(s.getSemester(), s.getYear());
                s.setSemesterStart(start);

                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertSection(Section s) {

        LocalDate start = calculateSemesterStart(s.getSemester(), s.getYear());
        s.setSemesterStart(start);

        String sql = "INSERT INTO sections "
                + "(course_id, instructor_id, day, time, room, capacity, semester, year, semester_start) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseId());
            ps.setInt(2, s.getInstructorId());
            ps.setString(3, s.getDay());
            ps.setString(4, s.getTime());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getCapacity());
            ps.setString(7, s.getSemester());
            ps.setInt(8, s.getYear());
            ps.setDate(9, Date.valueOf(start));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateSection(Section s) {

        LocalDate start = calculateSemesterStart(s.getSemester(), s.getYear());
        s.setSemesterStart(start);

        String sql = "UPDATE sections SET "
                + "course_id=?, instructor_id=?, day=?, time=?, room=?, capacity=?, "
                + "semester=?, year=?, semester_start=? "
                + "WHERE section_id=?";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseId());
            ps.setInt(2, s.getInstructorId());
            ps.setString(3, s.getDay());
            ps.setString(4, s.getTime());
            ps.setString(5, s.getRoom());
            ps.setInt(6, s.getCapacity());
            ps.setString(7, s.getSemester());
            ps.setInt(8, s.getYear());
            ps.setDate(9, Date.valueOf(start));
            ps.setInt(10, s.getSectionId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a section
     */
    public boolean deleteSection(int sectionId) {

        String sql = "DELETE FROM sections WHERE section_id=?";

        try (Connection conn = DBUtil.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
