package edu.univ.erp.service;

import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.domain.SectionWithAvailability;

import java.sql.SQLException;
import java.util.List;

public class SectionService {

    private final SectionRepository repo = new SectionRepository();

    public List<SectionWithAvailability> getSections(int courseId, int studentId) throws SQLException {
        return repo.getSectionsForCourse(courseId, studentId);
    }

    public List<SectionWithAvailability> getSectionsForAdmin(int courseId) {
        return repo.getSectionsForAdmin(courseId);
    }

    public String addSection(int courseId, int instructorId,
                             String day, String time, String room,
                             int capacity, String semester, int year) {
        return repo.addSection(courseId, instructorId, day, time, room, capacity, semester, year);
    }

    public String updateSection(int sectionId, int instructorId,
                                String day, String time, String room,
                                int capacity, String semester, int year) {
        return repo.updateSection(sectionId, instructorId, day, time, room, capacity, semester, year);
    }

    public String deleteSection(int sectionId) {
        return repo.deleteSection(sectionId);
    }
}