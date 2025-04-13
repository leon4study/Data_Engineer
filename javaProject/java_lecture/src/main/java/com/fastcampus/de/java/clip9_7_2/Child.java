package com.fastcampus.de.java.clip9_7_2;

class Child extends Human implements Walkable, Runnable, Swimmable{

    public Child(String name, int age ) {
        super(name, age, 5);
    }

    @Override
    public void swim(int x, int y) {
        printWhoAMI();
        System.out.println("swimming speed: "+(speed + 1));
        this.x = x;
        this.y = y;
        System.out.println("run to "+getLocation());
    }

    @Override
    public void run(int x, int y) {
        printWhoAMI();
        System.out.println("run speed: "+(speed + 2));
        this.x = x;
        this.y = y;
        System.out.println("run to "+getLocation());
    }

    @Override
    public void walk(int x, int y) {
        printWhoAMI();
        System.out.println("walk speed : " + speed);
        this.x = x;
        this.y = y;
        System.out.println("Walk to "+ getLocation());
    }
}
