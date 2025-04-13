package com.fastcampus.de.java.clip14;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// 람다를 사용하지 않고 함수 사용하면 더 간결하게 코드 작성할 수 있다.
public class StreamWithFunction {
    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>(Arrays.
                asList("Korea", "Japan", "China", "France", "England"));
        Stream<String> stream = stringList.stream();
        stream.map(StreamWithFunction::logic).forEach(System.out::println);

        Stream<String> stream2 = stringList.stream();
        stream2.map(StreamWithFunction::logic).forEach(System.out::println);
    }

    public static String logic(String param){
        System.out.println(param);
        System.out.println("logic");
        return param.toUpperCase();
    }
}
