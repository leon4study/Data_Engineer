package com.fastcampus.de.java.clip11;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class LocalTimeExample {
    public static void main(String[] args) {
        LocalTime now = LocalTime.now();
        System.out.println(now);
        System.out.println(LocalTime.parse("08:30"));
        System.out.println(LocalTime.of(7, 30));
        LocalTime nieThirty = LocalTime.of(7, 30).plusHours(2);
        System.out.println(nieThirty);

        boolean isAfter = LocalTime.of(6,30).isAfter(LocalTime.parse("08:20"));
        System.out.println(isAfter);

        System.out.println(LocalTime.MAX);
    }
}
