package com.fastcampus.de.java.clip7;

import java.util.Scanner;

public class Quiz1 {
    public static void main(String[] args) {
        int score;

        Scanner sc = new Scanner(System.in);
        System.out.println("점수를 입력하세요");
         score = sc.nextInt();
         if (score > 90){
             System.out.println("A 등급");
         } else if (score > 80) {
             System.out.println("B 등급");
         } else if (score > 70) {
             System.out.println("C 등급");
         }else {
             System.out.println("F 등급");
         }
    }
}
