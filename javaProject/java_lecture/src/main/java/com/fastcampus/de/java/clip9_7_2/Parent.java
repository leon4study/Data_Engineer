package com.fastcampus.de.java.clip9_7_2;

class Parent extends Human implements Walkable, Runnable {

    public Parent(String name, int age ) {
        super(name, age, 3);
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
