package com.fastcampus.de.java.clip14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StreamWithoutFunction {
    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>(Arrays.
                asList("Korea","Japan","China","France","England"));
        Stream<String> stream = stringList.stream();
        stream.map(s -> {
            System.out.println(s);
            System.out.println("logic");
            return s.toUpperCase();
        }).forEach(System.out::println);

        Stream<String> stream2 = stringList.stream();
        stream2.map(s -> {
            System.out.println(s);
            System.out.println("logic");
            return s.toUpperCase();
        }).forEach(System.out::println);
    }
}
