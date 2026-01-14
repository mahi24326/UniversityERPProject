package edu.univ.erp.service;

import edu.univ.erp.data.StudentRepository;
import edu.univ.erp.domain.SectionWithAvailability;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentRepository repository = new StudentRepository();

    public List<SectionWithAvailability> browseCourses(int studentId) throws SQLException {
        return repository.getAvailableSections(studentId);
    }

    public String registerForCourse(int studentId, int sectionId) {
        try {
            boolean ok = repository.registerStudentForSection(studentId, sectionId);
            return ok ? "success" : "failed";
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry"))
                return "duplicate";
            return "error:" + ex.getMessage();
        }
    }

    // --------------------------------------------------------------------
    // NEW FUNCTIONALITY: DROP COURSE (adds no side effects to existing code)
    // --------------------------------------------------------------------
    public String dropCourse(int studentId, int sectionId) {
        try {
            boolean ok = repository.dropStudentFromSection(studentId, sectionId);
            return ok ? "success" : "failed";
        } catch (SQLException ex) {
            return "error:" + ex.getMessage();
        }
    }
}







//package edu.univ.erp.service;
//import edu.univ.erp.data.StudentRepository;
//import edu.univ.erp.domain.SectionWithAvailability;
//
//import java.sql.SQLException;
//import java.util.List;
//
//public class StudentService {
//
//    private final StudentRepository repository = new StudentRepository();
//
//    public List<SectionWithAvailability> browseCourses(int studentId) throws SQLException {
//        return repository.getAvailableSections(studentId);
//    }
//
//    public String registerForCourse(int studentId, int sectionId) {
//        try {
//            boolean ok = repository.registerStudentForSection(studentId, sectionId);
//            return ok ? "success" : "failed";
//        } catch (SQLException ex) {
//            if (ex.getMessage().contains("Duplicate entry"))
//                return "duplicate";
//            return "error:" + ex.getMessage();
//        }
//    }
//
//    public String dropCourse(int studentId, int sectionId) {
//        try {
//            boolean ok = repository.dropStudentFromSection(studentId, sectionId);
//            return ok ? "success" : "failed";
//        } catch (SQLException ex) {
//            return "error:" + ex.getMessage();
//        }
//    }
//}
