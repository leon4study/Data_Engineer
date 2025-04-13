package com.fastcampus.de.java.study_abstract;

// Task 인터페이스 정의
interface Task{
    void taskName();
}

// Employee 추상 클래스 정의
abstract class Employee{
    String name;
    double salary;

    // 공통된 업무 메서드는 추상 메서드로 선언
    abstract void work();
}


class Manager extends Employee implements Task{
    @Override
    void work() {
        System.out.println(name + " is managing the team.");
    }

    @Override
    public void taskName() {
        System.out.println("Task: Managing the team.");
    }
}

class Developer extends Employee implements Task{

    @Override
    void work() {
        System.out.println(name + " is writing code.");
    }

    @Override
    public void taskName() {
        System.out.println("Task: Writing code.");
    }
}

public class Main {
    public static void main(String[] args) {
        Employee manager = new Manager();
        manager.name = "Alice";
        manager.salary = 5000;

        Employee developer = new Developer();
        developer.name = "Bob";
        developer.salary = 4000;

        // 직무 수행 및 업무 이름 출력
        manager.work();
        ((Task) manager).taskName(); // Task 인터페이스 메서드 호출

        developer.work();
        ((Task)developer).taskName(); // Task 인터페이스 메서드 호출
    }
}
