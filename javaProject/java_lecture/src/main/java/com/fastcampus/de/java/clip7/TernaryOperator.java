package com.fastcampus.de.java.clip7;

public class TernaryOperator {
    public static void main(String[] args) {
        int a = 10;
        String result = (a < 10) ? "10보다 작다" : (a == 10) ? "10이다" : "10보다 크다";
        System.out.println(result);
    }
}
