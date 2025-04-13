package com.fastcampus.de.java.clip9_1;

public class Main {

    static class Person{
        String name;
        String country;
        int age;

        Person(String name, String country, int age){
            this.name = name;
            this.country = country;
            if ("Korea".equals(country)){
                this.age = age + 1;
            }else {
                this.age = age;
            }
        }

        Person(){}
    }

    public static void main(String[] args) {
        Person minsoo = new Person("민수","Korea",10);
//        minsoo.name = "minsoo";
//        minsoo.country = "Korea";
//        minsoo.age = 10;


        Person paul = new Person("paul","USA", 40);

        Person[] persons = {minsoo, paul};

        for (Person cur : persons){
            System.out.println("<자기소개>");
            System.out.println("안녕하세요 저는 "+cur.name+" 입니다.");
            System.out.println(cur.country+" 에서 태어났고, "+ cur.age+ "살 입니다");
        }
    }
}
