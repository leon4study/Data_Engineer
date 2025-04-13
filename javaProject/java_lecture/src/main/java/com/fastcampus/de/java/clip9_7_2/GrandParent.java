package com.fastcampus.de.java.clip9_7_2;


class GrandParent extends Human implements Walkable{
    public GrandParent(String name, int age) {
        super(name, age, 1);
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