package com.fastcampus.de.java.study_exception_handling;

public class TooOldError extends Error {
    private int age;

    public TooOldError(int age) {
        this.age = age;
    }

    @Override
    public String getMessage() {
        return "age "+age + "occurred, limit error!, TooOldError";
    }
}
