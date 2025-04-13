package com.fastcampus.de.java.clip15_5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quiz1 {
    private int count;

    public static void main(String[] args) {
        List<String> nameList = new ArrayList<>(Arrays.asList("김정우", "김호정", "이하늘", "이정희", "박정우", "박지현", "정우석", "이지수"));

        long count = nameList.stream()
                .filter(n -> n.startsWith("이"))
                .count();

        System.out.println("count : " + count);
    }
}

