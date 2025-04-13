package com.fastcampus.de.java.clip12;

import java.util.HashMap;
import java.util.Map;

public class MapExample {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1,"apple");
        map.put(2,"berry");
        map.put(3,"cherry");
        map.put(100,"banana");
        System.out.println(map);

        Map<String, String> stirngMap = new HashMap<>();
        stirngMap.put("first","apple");
        stirngMap.put("second","berry");
        stirngMap.put("third","pineapple");
        stirngMap.putIfAbsent("second","banana"); // 존재하지 않으면 넣기
        System.out.println(stirngMap);

        if(stirngMap.containsKey("first")){
            System.out.println("exist! : "+ stirngMap.get("first"));
        }
        if (stirngMap.containsKey("not exists")){
            System.out.println("doesn't exist!");
        }

        for (String key : stirngMap.keySet()){
            System.out.println(stirngMap.get(key));
        }

        System.out.println(stirngMap.values());

        stirngMap.remove("first");
        System.out.println(stirngMap);

        stirngMap.clear();
        System.out.println("size : " + stirngMap.size());

    }
}
