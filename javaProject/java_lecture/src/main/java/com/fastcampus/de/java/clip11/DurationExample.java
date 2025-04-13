package com.fastcampus.de.java.clip11;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class DurationExample {
    public static void main(String[] args) {
        LocalTime initialTime = LocalTime.of(6,30);
        LocalTime finalTime = initialTime.plus(Duration.ofSeconds(30));

        System.out.println(Duration.between(initialTime, finalTime).getSeconds());
        System.out.println(ChronoUnit.SECONDS.between(initialTime, finalTime));
    }
}
