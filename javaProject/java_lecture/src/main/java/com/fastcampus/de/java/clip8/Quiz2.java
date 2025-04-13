package com.fastcampus.de.java.clip8;

public class Quiz2 {
    public static void main(String[] args) {
        int sumE = 0, sumO = 0;
        for(int i = 1; i <= 30 ; i ++){
            if(i%2 == 0){
                sumE +=i;
            }else{
                sumO +=i;
            }
        }
        System.out.println("Sum of even number : " + sumE);
        System.out.println("Sum of odd number : " + sumO);
    }
}
