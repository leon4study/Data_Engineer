package com.fastcampus.de.java.clip11;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class PeriodExample {
    public static void main(String[] args) {
        LocalDate initialDate = LocalDate.of(2025,4,8);
        LocalDate finalDate = LocalDate.of(2025,5,8);
        System.out.println("between 4/8 ~ 5/8 : " + Period.between(initialDate, finalDate).getMonths());

        /*
        Period 객체는 다음 세 가지 필드를 가집니다:
        1) years 2) months 3) days

        period.getDays() → 0
        period.getMonths() → 1
        period.getYears() → 0

        Period.between(2025-04-08, 2025-05-08)는
        전체 차이는 1개월이고, 일(day) 차이는 없다

        전체 일(day) 수를 계산하려면?
        ChronoUnit.DAYS.between() 를 사용해야 함.
        */

        LocalDate finalDate2 = initialDate.plus(Period.ofDays(5));
        int five = Period.between(initialDate, finalDate2).getDays(); // 반대면 -5
        System.out.println(five);

        //Days 기준으로 얼마나 차이나냐?
        System.out.println(ChronoUnit.DAYS.between(initialDate,finalDate2));

    }


}
