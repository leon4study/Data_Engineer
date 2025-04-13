package com.fastcampus.de.java.clip5;

import java.util.Arrays;

public class ReferenceTypes {
    public static void main(String[] args) {
        String str = "hello world!";
        System.out.println(str);

        int[] intArray = new int[]{1,2,3,4}; // 이것도 참조자료형.
        System.out.println(Arrays.toString(intArray));
    }
}
