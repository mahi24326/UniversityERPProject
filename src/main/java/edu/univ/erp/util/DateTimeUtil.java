package edu.univ.erp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateTimeUtil {

    public static LocalDateTime calculateDropDeadline(LocalDate semesterStart, int dropPeriodDays) {
        return semesterStart.plusDays(dropPeriodDays).atTime(23, 59, 59);
    }

    public static boolean isBeforeDeadline(LocalDateTime deadline) {
        return LocalDateTime.now().isBefore(deadline);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.toString(); // Customize if needed
    }
}
