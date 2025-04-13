package com.fastcampus.de.java.clip8;

public class DoWhileStatement {
    public static void main(String[] args) {
        int sum = 0;
        int i = 0;
        do{
            sum += i + 1;
            i++;
        } while (i < 10);

        System.out.println("sum of 1-10 : " + sum);
    }
}
