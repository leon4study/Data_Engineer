package com.fastcampus.de.java.clip5;

import java.util.Arrays;

public class Array {
    public static void main(String[] args) {
        int[] intEmptyArray = new int[5];
        System.out.println(Arrays.toString(intEmptyArray));

        int[] intArray = new int[]{1,2,3,4,5};
        System.out.println(Arrays.toString(intArray));

        String[] StringEmptyArray = new String[5];
        System.out.println(Arrays.toString(StringEmptyArray) );

        String[] months = new String[]{"1월","2월","3월","4월","5월","6월"};
        System.out.println(Arrays.toString(months) );

        int[] scores = new int[4];
        scores[0] = 5;
        scores[1] = 10;
        System.out.println(Arrays.toString(scores));
        scores[1] = 100;
        System.out.println(Arrays.toString(scores));

        int[][] arr = new int[4][3];
        System.out.println(Arrays.toString(arr));
    }
}
