package com.fastcampus.de.java.clip9_6;

public class Main {
    public static void main(String[] args) {
        Bird pigeon = new Pigeon();
        Bird kiwi = new Kiwi();

        System.out.println("-- 비둘기 --");
        pigeon.fly(1,2,3);

        System.out.println("-- 키위 --");
        kiwi.fly(2,3,4);

        System.out.println("-- 높이 나는 비둘기 --");
        pigeon.fly(5,6,30000);
    }
}
