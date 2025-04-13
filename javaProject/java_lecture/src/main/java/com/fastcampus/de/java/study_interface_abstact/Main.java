package com.fastcampus.de.java.study_interface_abstact;

abstract class Vehicle{
    String brand;

    public Vehicle(String brand) {
        this.brand = brand;
    }

    void startEngine(){
        System.out.println(brand + " engine started!");
    }

    abstract void drive();
}

interface Playable {
    void playMusic();
}


class Car extends Vehicle implements Playable{

    public Car(String brand) {
        super(brand);
    }

    @Override
    public void playMusic() {
        System.out.println(brand + "is playing music.");
    }

    @Override
    void drive() {
        System.out.println(brand + " is driving");
    }


}
public class Main {
    public static void main(String[] args) {
        Car myCar = new Car("Tesla");

        myCar.startEngine();
        myCar.drive();
        myCar.playMusic();

    }
}
