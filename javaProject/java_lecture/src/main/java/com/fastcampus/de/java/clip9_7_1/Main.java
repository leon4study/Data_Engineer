package com.fastcampus.de.java.clip9_7_1;


interface Swimmable{
    void swim(int x, int y);
}

interface Runnable{
    void run(int x, int y);
}

abstract class Human {
    String name;
    int age;
    int location_x = 0;
    int location_y = 0;
    int speed;

    public Human(String name, int age, int speed) {
        this.name = name;
        this.age = age;
        this.speed = speed;
    }

    void walk(int x, int y) {
        this.location_x = x;
        this.location_y = y;
        System.out.println("(" + this.getClass().getSimpleName().charAt(0) + ") "
                + name + " walked to (" + x + "," + y + ") with speed " + speed);
    }
}

class GrandParents extends Human{
    public GrandParents(String name, int age) {
        super(name, age, 1);
    }
}

class Parents extends Human implements Runnable{
    int speed = 3;
    public Parents(String name, int age) {
        super(name, age, 3);
    }

    @Override
    public void run(int x, int y) {
        System.out.println("(" + this.getClass().getSimpleName().charAt(0) + ") "
                + name + " ran to (" + x + "," + y + ") with speed " + (speed+2));
    }
}

class Child extends Human implements Runnable,Swimmable{
    public Child(String name, int age) {
        super(name, age,5);
    }

    @Override
    public void swim(int x, int y) {
        System.out.println("(" + this.getClass().getSimpleName().charAt(0) + ") "
                + name + " swam to (" + x + "," + y + ") with speed " + (speed+1));
    }

    @Override
    public void run(int x, int y) {
        System.out.println("(" + this.getClass().getSimpleName().charAt(0) + ") "
                + name + " ran to (" + x + "," + y + ") with speed " + (speed+2));
    }
}

public class Main {
    public static void main(String[] args) {
        Human gm1 = new GrandParents("grandma", 60 );
        Human m1 = new Parents("Marge", 43 );
        Human s1 = new Child("Bart", 15);

        gm1.walk(1,1);
        m1.walk(1,1);
        s1.walk(1,1);

        ((Runnable)m1).run(2,2);
        ((Runnable)s1).run(2,2);

        ((Swimmable)s1).swim(3,-1);
    }
}