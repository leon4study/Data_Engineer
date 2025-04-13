package com.fastcampus.de.java.study_exception_handling;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int age = sc.nextInt();

        try{
            if (AgeValidator.validate(age)){
                System.out.println("Your age is " + age);
            }
        }catch (InvalidAgeException invalidAgeException){
            System.out.println(invalidAgeException.getMessage()+"\n");
            invalidAgeException.printStackTrace();
        }catch (TooOldError tooOldError){
            System.out.println(tooOldError.getMessage()+"\n");
            tooOldError.printStackTrace();
        }
    }
}
