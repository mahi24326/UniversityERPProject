package edu.univ.erp.service;

import edu.univ.erp.data.CourseRepository;
import edu.univ.erp.domain.Course;

import java.sql.SQLException;
import java.util.List;

public class CourseService {
    private final CourseRepository repo = new CourseRepository();

    public List<Course> getAllCourses() throws SQLException {
        return repo.getAllCourses();
    }
}
