package com.fastcampus.de.java.clip9_4;
// 이 세개는 전부 오버로딩의 예제다.
public class Calculator {
    int add(int x, int y, int z){
        return x+y+z;
    }

    long add(int a, int b, long c){
        return a+b+c;
    }

    int add(int x, int y){
        return x+y;
    }
}
