package com.fastcampus.de.java.clip9_7_2;


class Human{
    String name;
    int age;
    int speed;
    int x,y;

    public Human(String name, int age, int speed) {
        this.name = name;
        this.age = age;
        this.speed = speed;
    }

    public String getLocation(){
        return "(" + x + ", " + y + ")";
    }
    protected void printWhoAMI(){
        System.out.println("My name is " + name+". " + age + " aged.");
    }
}