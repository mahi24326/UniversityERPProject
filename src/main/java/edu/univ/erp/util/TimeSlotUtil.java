package edu.univ.erp.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeSlotUtil {


    public static LocalTime parseTime(String dbTime) {
        if (dbTime == null) return null;

        dbTime = dbTime.trim().toUpperCase();

        String[] patterns = {
                "h:mm a", "hh:mm a",
                "H:mm", "HH:mm",
                "H:mm:ss", "HH:mm:ss"
        };

        for (String p : patterns) {
            try {
                return LocalTime.parse(dbTime, DateTimeFormatter.ofPattern(p));
            } catch (Exception ignored) {}
        }

        System.err.println("Cannot parse time: " + dbTime);
        return null;
    }


}
