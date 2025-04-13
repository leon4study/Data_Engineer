package com.fastcampus.de.java.study_exception_handling;

public class InvalidAgeException extends Exception{
    private int age;

    public InvalidAgeException(int age) {
        this.age = age;
    }

    @Override
    public String getMessage() {
        return "age " +age + " not permitted, InvalidAgeException";
    }
}
