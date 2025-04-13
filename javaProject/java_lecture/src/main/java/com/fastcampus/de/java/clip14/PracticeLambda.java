package com.fastcampus.de.java.clip14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PracticeLambda {
    public static void main(String[] args) {
        List<String> animalList = new ArrayList<>(Arrays.asList("Bird","Tiger","Lion","Rabbit", "Bat"));
        Stream<String> stream = animalList.stream();
        stream.filter(s -> s.toLowerCase().contains("a"))
                .map(s -> s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase())
                .forEach(System.out::println);
    }
}
