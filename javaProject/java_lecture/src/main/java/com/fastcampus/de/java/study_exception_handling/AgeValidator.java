package com.fastcampus.de.java.study_exception_handling;

public class AgeValidator {
    public static boolean validate(int age)
            throws InvalidAgeException, TooOldError{
        if (age < 0 | age > 150){
            throw new InvalidAgeException(age);
        } else if (age >= 100) {
            throw new TooOldError(age);
        } else {
            return true;
        }
    }
}
