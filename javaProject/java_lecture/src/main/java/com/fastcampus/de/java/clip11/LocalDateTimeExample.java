package com.fastcampus.de.java.clip11;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

public class LocalDateTimeExample {
    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
        System.out.println(LocalDateTime.of(2022, Month.AUGUST,20,4,30));
        System.out.println(LocalDateTime.parse("2025-04-08T14:19:59.552"));


        LocalDateTime localDateTime  = LocalDateTime.now();
        System.out.println(localDateTime.plusDays(1));
        System.out.println(localDateTime.minusHours(1));
        System.out.println(localDateTime.of(2022, Month.DECEMBER,20,6,30).plusMonths(1));


    }
}
