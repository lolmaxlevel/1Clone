package com.lolmaxlevel.oneclone_backend.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter FORMATTER_FULL = DateTimeFormatter.ofPattern("dd MMMM yyyy года", new Locale("ru"));

    public static String formatDate(LocalDate date) {
        return date.format(FORMATTER);
    }

    public static String formatDateFull(LocalDate date) {
        return date.format(FORMATTER_FULL);
    }
}