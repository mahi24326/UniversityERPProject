package edu.univ.erp.util;

import edu.univ.erp.service.StudentTranscriptService;

import java.io.File;
import java.util.*;

public class StudentTranscriptExporter {

    public static void exportCSV(File file, int studentId) throws Exception {

        StudentTranscriptService service = new StudentTranscriptService();
        List<StudentTranscriptService.Row> data = service.getTranscriptRows(studentId);

        String[] header = {
                "Course Code", "Course Title", "Component",
                "Score", "Weight", "Final Score"
        };

        List<String[]> rows = new ArrayList<>();

        for (StudentTranscriptService.Row r : data) {
            rows.add(new String[]{
                    r.courseCode,
                    r.courseTitle,
                    r.component != null ? r.component : "-",
                    r.score != null ? String.format("%.2f", r.score) : "-",
                    r.weight != null ? r.weight.toString() : "-",
                    r.finalScore != null ? String.format("%.2f", r.finalScore) : "-"
            });
        }

        CSVHelper.writeCSV(file, header, rows);
    }
}
