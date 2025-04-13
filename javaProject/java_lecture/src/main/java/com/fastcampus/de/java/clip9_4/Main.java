package com.fastcampus.de.java.clip9_4;

public class Main {
    static class Animal{
        String name;

        Animal(String name){
            this.name = name;
        }

        public void cry(){
            System.out.println(name + " is crying");
        }
    };

    static class Giraffe extends Animal{

        Giraffe(String name) {
            super(name);
        }

        @Override
        public void cry(){
            super.cry();
            System.out.println(name + " cannot cry");
        }
    }

    static class Dog extends Animal {
        String bread;

        Dog(String name, String bread) {
            super(name);
            this.bread = bread;
        }

        public void bark(){
            System.out.println(name +"("+bread+") is barking");
        }

    };


    public static void main(String[] args) {
        Dog d1 = new Dog("chill", "똥개");
        d1.cry();
        d1.bark();

        Animal animal = d1;
        animal.cry();

        Animal giraffe = new Giraffe("기린이");
        giraffe.cry();
    }
}
