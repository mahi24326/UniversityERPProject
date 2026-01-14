package edu.univ.erp.service;

import edu.univ.erp.data.InstructorRepository;
import edu.univ.erp.data.SectionRepository;
import edu.univ.erp.data.EnrollmentRepository;
import edu.univ.erp.domain.InstructorTeachingStats;
import edu.univ.erp.domain.SectionWithAvailability;

import java.util.List;

public class InstructorService {

    private final InstructorRepository instructorRepo;
    private final SectionRepository sectionRepo;
    private final EnrollmentRepository enrollmentRepo;

    public InstructorService() {
        this.instructorRepo = new InstructorRepository();
        this.sectionRepo = new SectionRepository();
        this.enrollmentRepo = new EnrollmentRepository();
    }

    public InstructorTeachingStats getTeachingStats(int instructorId) {

        int totalSections = sectionRepo.countByInstructor(instructorId);
        int totalStudents = enrollmentRepo.countStudentsForInstructor(instructorId);
        double avgClassSize = enrollmentRepo.averageClassSize(instructorId);
        int pendingGrades = enrollmentRepo.countPendingGrades(instructorId);

        return new InstructorTeachingStats(
                totalSections,
                totalStudents,
                avgClassSize,
                pendingGrades
        );
    }

    public List<SectionWithAvailability> getMySections(int instructorId) {
        return sectionRepo.getSectionsForInstructor(instructorId);
    }

}