package com.fastcampus.de.java.clip9_7;

public interface Bird {

    String STATIC_VARIABLE = "STATIC";

    void fly(int x, int y, int z);

    default void printBread(){
        System.out.println("나는 새 중에 " + getBread()+" 입니다");
    }

    String getBread();

    static void staticMethod(){
        System.out.println("This is static method");
    }

    abstract void abstractMethod();
    // abstract 했지만 사실상 인터페이스 함수 정의한 게 abstract method가 하는 거랑 기능이 같으니
    // 명세만 제공하고 구현하는 클래스한테 맡긴다는 거니까 가이드 따라서 없애서 일반함수로 활용하는 게
    // 더 좋은 디자인이다.
}
