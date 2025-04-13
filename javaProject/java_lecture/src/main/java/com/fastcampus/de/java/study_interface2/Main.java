package com.fastcampus.de.java.study_interface2;

interface Flyable{
    void fly();
}

interface Swimmable{
    void swim();
}

abstract class Animal{
    String name;

    public Animal(String name) {
        this.name = name;
    }

    abstract void makeSound();
}

class Dog extends Animal implements Swimmable{
    public Dog(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " : 멍");
    }

    @Override
    public void swim() {
        System.out.println(name + "swimming");
    }
}

class Eagle extends Animal implements Flyable{
    public Eagle(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " : 꼬");
    }

    @Override
    public void fly() {
        System.out.println(name + "is flying");
    }
}

class Duck extends Animal implements Swimmable, Flyable{

    public Duck(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " : 꽥");
    }

    @Override
    public void fly() {
        System.out.println(name + " is flying");
    }

    @Override
    public void swim() {
        System.out.println(name + "is swimming");
    }
}

public class Main {
    public static void main(String[] args) {
        Animal dg1 = new Dog("바둑");
        Animal eg1 = new Eagle("수리");
        Animal dk1 = new Duck("파오리");

        dg1.makeSound();
        eg1.makeSound();
        dk1.makeSound();

        ((Swimmable) dg1).swim();
//        ((Swimmable) eg1).swim();
        ((Swimmable) dk1).swim();


//        ((Flyable) dg1).fly();
        ((Flyable) eg1).fly();
        ((Flyable) dk1).fly();

    }
}