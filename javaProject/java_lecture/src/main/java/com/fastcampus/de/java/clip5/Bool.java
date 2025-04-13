package com.fastcampus.de.java.clip5;

public class Bool {
    public static void main(String[] args) {
        boolean fact = true;
        System.out.println(fact);

        Defaults defaults = new Defaults();
        System.out.println("bool defaults = ".concat(String.valueOf(defaults.booleanDefault)));
    }

    static class Defaults{
        boolean booleanDefault;
    }
}
