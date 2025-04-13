package com.fastcampus.de.java.clip11;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Formatter {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.of(2025, Month.APRIL,8,6,30);
        System.out.println(localDateTime.format(DateTimeFormatter.ISO_DATE));

        System.out.println(localDateTime.format(DateTimeFormatter.
                ofLocalizedDateTime(FormatStyle.LONG).withLocale(Locale.KOREA)));

        System.out.println(localDateTime.format(DateTimeFormatter.ofPattern("yyyy_MM_dd, HH:mm:ss")));
    }
}
