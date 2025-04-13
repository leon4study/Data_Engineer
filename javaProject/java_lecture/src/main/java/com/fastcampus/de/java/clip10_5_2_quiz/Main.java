package com.fastcampus.de.java.clip10_5_2_quiz;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();

        try {
            if (FileValidator.validate(path)){
                System.out.println("File "+path + " exists.");
            }else {
                System.out.println("File "+path + " doesn't exist.");
            }
        } catch (IllegalPathAccessError e) {
            System.out.println(e.getMessage()+"\n");
            e.printStackTrace();
            System.out.println("Program is forced to quit");
            System.exit(1);
        }

        scanner.close();
    }
}
