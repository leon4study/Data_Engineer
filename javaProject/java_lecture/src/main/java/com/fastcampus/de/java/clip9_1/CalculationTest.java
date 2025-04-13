package com.fastcampus.de.java.clip9_1;

public class CalculationTest {
    static class Calculation{
        int extra = 1;

        static int add(int x, int y){
            return x + y;
        }

        int subtract(int x, int y){
            return x-y - extra;
        }
    }

    public static void main(String[] args) {
        Calculation calculation = new Calculation();
        System.out.println("100 + 90 = " + calculation.add(100,90));
        System.out.println("100 + 80 = " + Calculation.add(100,80));
        System.out.println("100 - 90 = " + calculation.subtract(100,90));
    }
}
