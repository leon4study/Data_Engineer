package com.fastcampus.de.java.clip12;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetExample {
    public static void main(String[] args) {
        Set<Integer> integerSet = new HashSet<>();
        integerSet.add(1);
        integerSet.add(3);
        integerSet.add(2);
        integerSet.add(9);
        integerSet.add(9);
        System.out.println(integerSet);

        Set<String> stringSet = new HashSet<>();
        stringSet.add("LA");
        stringSet.add("New York");
        stringSet.add("LasVegas");
        stringSet.add("San Francisco");
        stringSet.add("Seoul");
        System.out.println(stringSet);

        stringSet.remove("Seoul");
        System.out.println(stringSet);

        System.out.println("exist LA? : " + stringSet.contains("LA"));

        List<String> removeTarget = new ArrayList<>();
        removeTarget.add("LA");
        removeTarget.add("New York");
        stringSet.removeAll(removeTarget);
        System.out.println(stringSet);

        System.out.println("exist LA? : " + stringSet.contains("LA"));

        System.out.println("size : " + stringSet.size());
        stringSet.clear();
        System.out.println(stringSet);
    }
}
