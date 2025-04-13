package com.fastcampus.de.java.clip9_7;

public class Main {
    public static String STATIC_VARIABLE = "STATIC";
    public static void main(String[] args) {
        Bird p1 = new Pigeon();
        p1.fly(1,2,3 );
        p1.printBread();
        p1.abstractMethod();
        Bird.staticMethod();
        System.out.println(Bird.STATIC_VARIABLE);
    }
}
