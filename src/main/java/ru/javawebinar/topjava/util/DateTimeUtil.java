package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String format(LocalDateTime localDateTime) {
        return FORMATTER.format(localDateTime);
    }

    public static LocalDateTime parse(String s) {
        if (s.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(s.replace("T", " "), FORMATTER);
    }
}
