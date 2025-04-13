package com.fastcampus.de.java.study_interface;

interface Soundable{
    void makeSound();
}

class Dog implements Soundable{

    @Override
    public void makeSound() {
        System.out.println("Woof!");
    }
}

class Cat implements Soundable{

    @Override
    public void makeSound() {
        System.out.println("Meow!");
    }
}

public class Main {
    public static void main(String[] args) {
        Soundable dog = new Dog();
        Soundable cat = new Cat();

        dog.makeSound(); // Woof!
        cat.makeSound(); // Meow!
    }
}
