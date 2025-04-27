package com.fastcampus.de;


import java.util.Random;

public class JmxExample implements Runnable {
    public void run(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        JmxExample runnable = new JmxExample();

        while (true){
            int random = new Random(System.currentTimeMillis()).nextInt();

            ThreadGroup tg1 = new ThreadGroup("ThreadGroup" + random);
            Thread t1 = new Thread(tg1, runnable, random + "-thread-1");
            t1.start();
            Thread t2 = new Thread(tg1, runnable, random + "-thread-2");
            t2.start();
            Thread t3 = new Thread(tg1, runnable, random + "-thread-3");
            t3.start();

            System.out.println("Thread Group Name : " + tg1.getName());
            tg1.list();
            Thread.sleep(3 * 1000);
        }
    }
}
