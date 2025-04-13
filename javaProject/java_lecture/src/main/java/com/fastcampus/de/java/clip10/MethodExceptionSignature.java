package com.fastcampus.de.java.clip10;

public class MethodExceptionSignature {
    static void methodTrowsException() throws Exception{

    }

    static void methodThrowsRuntimeException() throws RuntimeException{

    }

    public static void main(String[] args) {
        methodThrowsRuntimeException();
        try {
            methodTrowsException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
