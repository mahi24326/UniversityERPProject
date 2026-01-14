package edu.univ.erp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVHelper {


    public static void writeCSV(File file, String[] header, List<String[]> rows) throws Exception {

        try (Writer out = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {

            // Write BOM for Excel compatibility (optional)
            out.write('\uFEFF');

            // Write header
            if (header != null) {
                out.write(escapeRow(header));
                out.write("\n");
            }

            // Write data rows
            for (String[] row : rows) {
                out.write(escapeRow(row));
                out.write("\n");
            }
        }
    }


    private static String escapeRow(String[] row) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < row.length; i++) {
            String field = (row[i] == null ? "" : row[i]);

            boolean needsQuotes =
                    field.contains(",") ||
                            field.contains("\"") ||
                            field.contains("\n") ||
                            field.contains("\r");

            if (needsQuotes) {
                sb.append("\"");
                sb.append(field.replace("\"", "\"\"")); // Escape quotes
                sb.append("\"");
            } else {
                sb.append(field);
            }

            if (i < row.length - 1)
                sb.append(",");
        }

        return sb.toString();
    }
}
