package com.fastcampus.de;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class Oom {
    public static void main(String[] args) throws InterruptedException {
        List<String> list1 = new ArrayList<>();
        while (true){
            new Object();
            String str = RandomStringUtils.random(10);
            list1.add(str);
            if (str.contains("a")){
                Thread.sleep(500);
            }
        }
    }
}
