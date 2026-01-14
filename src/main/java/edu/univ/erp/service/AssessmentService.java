package edu.univ.erp.service;

import edu.univ.erp.data.AssessmentRepository;
import edu.univ.erp.data.EnrollmentRepository;
import edu.univ.erp.data.GradeRepository;
import edu.univ.erp.domain.AssessmentComponent;
import edu.univ.erp.domain.StudentScoreRow;
import edu.univ.erp.ui.instructor.ExportGradebookDialog;
import edu.univ.erp.domain.SectionItem;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;

public class AssessmentService {

    private final AssessmentRepository repo = new AssessmentRepository();
    private final EnrollmentRepository enrollRepo = new EnrollmentRepository();
    private final GradeRepository gradeRepo = new GradeRepository();


    public List<AssessmentComponent> getComponentsForCourse(int courseId) throws Exception {
        return repo.getComponentsForCourse(courseId);
    }

    public void addComponent(int courseId, String name, int weight, int maxMarks) throws Exception {
        repo.addComponent(courseId, name, weight, maxMarks);
    }

    public void deleteComponent(int componentId) throws Exception {
        repo.deleteComponent(componentId);
    }


    public List<StudentScoreRow> getStudentsForSection(int sectionId) throws Exception {
        return enrollRepo.getStudentsWithScores(sectionId);
    }


    public void saveScores(int sectionId, int courseId, DefaultTableModel model) throws Exception {

        List<AssessmentComponent> components = repo.getComponentsForCourse(courseId);

        for (int r = 0; r < model.getRowCount(); r++) {

            int enrollmentId = Integer.parseInt(model.getValueAt(r, 0).toString());

            int colIndex = 2;   // 0 = enrollmentId, 1 = studentName
            for (AssessmentComponent c : components) {

                Object val = model.getValueAt(r, colIndex++);

                if (val == null || val.toString().trim().isEmpty())
                    continue;

                double score = Double.parseDouble(val.toString());
                repo.saveScore(enrollmentId, c.getComponentId(), score);
            }

            // auto compute final grade
            double finalScore = repo.computeFinalScore(enrollmentId, courseId);
            gradeRepo.saveFinalGrade(enrollmentId, finalScore);
        }
    }



    public void saveSingleScore(int enrollmentId, int componentId, double score) throws Exception {
        repo.saveScore(enrollmentId, componentId, score);
    }

    // ======================================================
    // STATISTICS
    // ======================================================

    public double getClassAverage(int sectionId) throws Exception {
        return repo.getClassAverage(sectionId);
    }

    public double getClassMax(int sectionId) throws Exception {
        return repo.getClassMax(sectionId);
    }

    public double getClassMin(int sectionId) throws Exception {
        return repo.getClassMin(sectionId);
    }

    public int getStudentCount(int sectionId) throws Exception {
        return repo.getStudentCount(sectionId);
    }



    public double computeFinalGrade(int enrollmentId, int courseId) throws Exception {
        double finalScore = repo.computeFinalScore(enrollmentId, courseId);
        gradeRepo.saveFinalGrade(enrollmentId, finalScore);
        return finalScore;
    }

    public List<Double> getScores(int sectionId) throws Exception {
        return null;//repo.getAllScores(sectionId);
    }

    public List<String[]> getGradebookRows(int sectionId) throws Exception {

        int courseId = enrollRepo.getCourseIdForSection(sectionId);
        List<AssessmentComponent> components = repo.getComponentsForCourse(courseId);
        List<StudentScoreRow> rows = enrollRepo.getStudentsWithScores(sectionId);

        List<String[]> result = new ArrayList<>();

        for (StudentScoreRow s : rows) {

            String[] row = new String[components.size() + 2];

            int idx = 0;
            row[idx++] = s.getStudentName();

            // component scores
            for (AssessmentComponent c : components) {
                Double sc = s.getScoreFor(c.getComponentId());
                row[idx++] = (sc == null ? "" : sc.toString());
            }

            Double finalScore = repo.computeFinalScore(s.getEnrollmentId(), courseId);
            row[idx] = (finalScore == null ? "" : finalScore.toString());

            result.add(row);
        }

        return result;
    }



    public Double getFinalScoreSafe(int enrollmentId, int courseId) {
        try {
            return repo.computeFinalScore(enrollmentId, courseId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<SectionItem> getSectionsForInstructor(int instructorId) throws Exception {
        return enrollRepo.getSectionsForInstructor(instructorId);
    }

    public int importGradesFromCSV(int sectionId, List<String[]> rows) throws Exception {

        if (rows == null || rows.isEmpty())
            throw new Exception("CSV contains no data.");

        int courseId = enrollRepo.getCourseIdForSection(sectionId);
        List<AssessmentComponent> components = repo.getComponentsForCourse(courseId);

        int expectedCols = components.size() + 2;  // Student + each component + Final Score


        String[] header = rows.get(0);  // first element is header (because Importer skips actual header)
        if (header.length != expectedCols)
            throw new Exception("CSV column count does not match expected format.");

        // Normalize helper
        java.util.function.Function<String,String> norm = s ->
                s.toLowerCase().replace(" ", "").replace("_", "");

        // Expected normalized header
        String[] expected = new String[expectedCols];
        expected[0] = "student";

        int ci = 1;
        for (AssessmentComponent c : components)
            expected[ci++] = c.getName();

        expected[expectedCols - 1] = "finalscore";

        // Compare normalized header
        for (int i = 0; i < expectedCols; i++) {
            if (!norm.apply(header[i]).equals(norm.apply(expected[i]))) {
                throw new Exception(
                        "CSV header mismatch at column " + (i + 1)
                                + "\nExpected: " + expected[i]
                                + "\nFound: " + header[i]
                );
            }
        }


        int count = 0;

        for (int r = 1; r < rows.size(); r++) {

            String[] row = rows.get(r);
            if (row.length != expectedCols)
                throw new Exception("Row " + (r + 1) + " has incorrect number of columns.");

            String studentName = row[0].trim();
            if (studentName.isEmpty()) continue;

            int enrollmentId = enrollRepo.getEnrollmentId(sectionId, studentName);
            if (enrollmentId <= 0)
                throw new Exception("Student '" + studentName + "' not found in this section.");

            // Save each component score
            int col = 1;
            for (AssessmentComponent c : components) {

                String s = row[col++].trim();
                if (s.isEmpty()) continue;

                double score = Double.parseDouble(s);

                if (score < 0 || score > c.getMaxMarks())
                    throw new Exception("Invalid score for student '" + studentName +
                            "' in component '" + c.getName() +
                            "'. Score must be between 0 and " + c.getMaxMarks());

                repo.saveScore(enrollmentId, c.getComponentId(), score);
            }

            // Final score
            String finalStr = row[expectedCols - 1].trim();
            double finalScore = Double.parseDouble(finalStr);

            gradeRepo.saveFinalGrade(enrollmentId, finalScore);

            count++;
        }

        return count;
    }



}
