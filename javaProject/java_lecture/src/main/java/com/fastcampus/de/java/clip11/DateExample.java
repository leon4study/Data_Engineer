package com.fastcampus.de.java.clip11;

import java.util.Date;

public class DateExample {
    public static void main(String[] args) {
        System.out.println(new Date());
        System.out.println(new Date(1000));

        System.out.println(new Date().after(new Date(1)));
        System.out.println(new Date().compareTo(new Date(1)));

        long future = 2228562001000L;
        System.out.println(new Date().after(new Date(future)));
        System.out.println(new Date().compareTo(new Date(future)));
    }
}
