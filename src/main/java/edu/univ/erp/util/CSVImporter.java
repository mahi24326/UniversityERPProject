package edu.univ.erp.util;

import edu.univ.erp.domain.AssessmentComponent;
import edu.univ.erp.service.AssessmentService;
import edu.univ.erp.data.EnrollmentRepository;

import java.io.*;
import java.util.*;

public class CSVImporter {

    private static final AssessmentService service = new AssessmentService();
    private static final EnrollmentRepository enrollRepo = new EnrollmentRepository();


    public static void importCSV(File file, int sectionId, int courseId) throws Exception {

        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // skip header
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                rows.add(line.split(","));
            }
        }

        List<AssessmentComponent> components = service.getComponentsForCourse(courseId);

        for (String[] row : rows) {

            if (row.length < 2) continue;

            String username = row[0].trim();
            int enrollmentId = enrollRepo.getEnrollmentId(sectionId, username);

            if (enrollmentId <= 0) continue;

            // import raw marks in component order
            for (int i = 0; i < components.size(); i++) {
                int colIndex = i + 1;
                if (colIndex >= row.length) break;

                String value = row[colIndex].trim();
                if (value.isEmpty()) continue;

                try {
                    double raw = Double.parseDouble(value);
                    service.saveSingleScore(enrollmentId, components.get(i).getComponentId(), raw);
                } catch (NumberFormatException ignore) {
                    // ignore invalid number
                }
            }

            // recompute final
            service.computeFinalGrade(enrollmentId, courseId);
        }
    }
}
