package com.fastcampus.de.java.clip15;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamEx1 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("서울");
        list.add("부산");
        list.add("서초");
        list.add("서울");

        System.out.println(list);

        List<String> result = list.stream()
                .limit(2)
                .collect(Collectors.toList()); // 스트리밍 결과들 쭉 모아서 리스트로 만들어주꼐

        System.out.println(result);
        System.out.println(list);

        Set<String> result2 = list.stream()
                .filter("서울"::equals)
                .collect(Collectors.toSet());
        System.out.println(result2);
    }
}
