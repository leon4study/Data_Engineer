package com.fastcampus.de.java.static_study;

class Person{
    String name;
    static int population;

    Person(String name){
        this.name = name;
        population ++;
    }
}

public class Momo {
    public static void main(String[] args) {
        Person p1 = new Person("A");
        System.out.println(Person.population);

        Person p2 = new Person("B");
        System.out.println(p2.population);

        Person p3 = new Person("C");
        System.out.println(p3.population );
    }
}
