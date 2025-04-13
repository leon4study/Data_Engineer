package com.fastcampus.de.java.clip14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LambdaErrorExample {
    public static void main(String[] args) {
        List<String> stringList =
                new ArrayList<>(Arrays.asList("Korea", "Japan", "China", "France", "England"));
        Stream<String> errorStream = stringList.stream();


        errorStream.map(LambdaErrorExample::logic).map((str) ->
                new ArrayList<>(Arrays.asList(str)).stream()
                        .map(String::toLowerCase).map((nextStr) -> {
                            System.out.println("inner lambda");
                            if ("korea".equals(nextStr)) {
                                throw new RuntimeException("error");
                            }
                            return nextStr;
                        }).findFirst()).collect(Collectors.toList());
    }

    public static String logic(String param) {
        System.out.println(param);
        System.out.println("logic");
        return param.toUpperCase();
    }
}