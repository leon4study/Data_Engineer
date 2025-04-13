package com.fastcampus.de.java.study_abstract2;

abstract class Animal{
    String name;

    public Animal(String name) {
        this.name = name;
    }

    public void eat(){
        System.out.println(name + " is eating.");
    }

    abstract void makeSound();
}


class Dog extends Animal{
    public Dog(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " says : Woof!");
    }
}


class Cat extends Animal{

    public Cat(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " says Meow");
    }
}

public class Main {
    public static void main(String[] args) {
        Dog d1 = new Dog("Buddy");
        Cat c1 = new Cat("Kitty");

        d1.eat();
        d1.makeSound();

        c1.eat();
        c1.makeSound();
    }
}
