package com.fastcampus.de.java.clip9_7_2;

public class Main {
    public static void main(String[] args) {
        Human grandFather = new GrandParent("할아버지", 70 );
        Human mom = new Parent("엄마", 50);
        Human me = new Child("나", 20);

        Human[] humans = {grandFather, mom, me};

        for (Human human : humans){
            System.out.println(human.name+ ", 나이: "+human.age + ", 속도: "
                    + human.speed+", 장소: "+human.getLocation());
        }
        System.out.println("<활동 시작>");

        for (Human human : humans){
            if (human instanceof Walkable){
                ((Walkable) human).walk(1,1);
                System.out.println(" - - - - - - - - - ");
            }
            if (human instanceof Runnable){
                ((Runnable) human).run(2,2);
                System.out.println(" - - - - - - - - - ");
            }
            if (human instanceof Swimmable){
                ((Swimmable) human).swim(3,-1);
                System.out.println(" - - - - - - - - - ");
            }
        }
    }
}
