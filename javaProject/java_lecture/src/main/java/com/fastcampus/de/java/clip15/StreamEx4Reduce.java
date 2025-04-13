package com.fastcampus.de.java.clip15;

import java.util.Arrays;
import java.util.List;

public class StreamEx4Reduce {
    public static void main(String[] args) {
        List<Integer> numArr = Arrays.asList(1,3,5,7,9);
        int result = numArr.stream()
//                .reduce(0, Integer::sum);
                .reduce(0, StreamEx4Reduce::sum);
        System.out.println(result);

        String[] arr = {"데이터를","배우는","가장 쉬운", "방법은","fastcampus","초격차 데이터 엔지니어링!"};
        List<String> strArr = Arrays.asList(arr);
        String result2 = strArr.stream()
                .reduce("수업 설명 :  ", (param, next) -> param + next + " " );
        System.out.println(result2);
    }

    public static int sum(int a, int b){
        System.out.println("a : " +a + " b : " + b);
        return a + b;
    }
}
