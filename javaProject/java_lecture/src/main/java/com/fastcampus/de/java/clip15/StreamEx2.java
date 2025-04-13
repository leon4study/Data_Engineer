package com.fastcampus.de.java.clip15;

import java.util.Arrays;
import java.util.stream.Stream;

public class StreamEx2 {
    public static void main(String[] args) {
        String[] arr = {"데이터를","배우는","가장 쉬운", "방법은","fastcampus","초격차 데이터 엔지니어링!"};
        Stream<String> stream = Arrays.stream(arr);
        stream.forEach(
                param -> System.out.print(param + " ")
        );
        System.out.println();
    }
}
