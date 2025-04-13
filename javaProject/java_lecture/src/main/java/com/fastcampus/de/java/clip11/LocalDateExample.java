package com.fastcampus.de.java.clip11;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public class LocalDateExample {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
        System.out.println(LocalDate.of(2022, 2, 22));
        System.out.println(LocalDate.parse("2022-02-20"));

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        System.out.println(tomorrow);

        LocalDate previousMonthDay = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        System.out.println(previousMonthDay);

        DayOfWeek tuesday = LocalDate.now().getDayOfWeek();
        System.out.println(tuesday);

        int twelve = LocalDate.parse("2022-06-12").getDayOfMonth();
        System.out.println(twelve);

        System.out.println(LocalDate.parse("2024-01-03").isLeapYear());

        System.out.println(LocalDate.parse("2022-10-11").isAfter(LocalDate.parse("2023-10-11")));


        LocalDateTime beginningOfDay = LocalDate.parse("2022-04-21").atStartOfDay();
        System.out.println(beginningOfDay);

        LocalDate firstDayOfMonth = LocalDate.parse("2022-06-12").with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(firstDayOfMonth);


    }
}
