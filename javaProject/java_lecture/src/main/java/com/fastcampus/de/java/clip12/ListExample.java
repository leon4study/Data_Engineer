package com.fastcampus.de.java.clip12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListExample {
    public static void main(String[] args) {
        List<Integer> l1 = new ArrayList<>();
        l1.add(1);
        l1.add(5);
        l1.add(4);
        l1.add(11);
        l1.add(10);
        System.out.println(l1);

        Collections.sort(l1);
        System.out.println(l1);
        System.out.println("size : " + l1.size());

        l1.remove(4); //index로 접근해서 삭제
        System.out.println(l1);



        int[] intArr = new int[3];
        intArr[0] = 1;
        intArr[1] = 5;
        intArr[2] = 4;

        int[] intArr2 = new int[5];
        intArr2[0] = intArr[0];
        intArr2[1] = intArr[1];
        intArr2[2] = intArr[2];
        intArr2[3] = 11;
        intArr2[4] = 10;


        for (int i=1; i<=l1.size(); i++){
            System.out.println(l1.get(i));
        }

        for (int cur : l1){
            System.out.println(cur);
        }
    }
}
