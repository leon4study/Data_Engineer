package com.fastcampus.de.java.study_interface_abstact;

import java.util.ArrayDeque;

public class ArrayDequeExample {
    public static void main(String[] args) {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        System.out.println(arrayDeque);
        arrayDeque.addFirst(2);
        System.out.println(arrayDeque);
        arrayDeque.addFirst(3);
        System.out.println(arrayDeque);
        arrayDeque.addFirst(4);
        System.out.println(arrayDeque);


        arrayDeque.addLast(0);
        System.out.println(arrayDeque);

        arrayDeque.offer(10);
        System.out.println(arrayDeque);
        arrayDeque.offerLast(11);
        System.out.println(arrayDeque);
        arrayDeque.offerFirst(13);
        System.out.println(arrayDeque);

        arrayDeque.push(6);
        System.out.println(arrayDeque);
        arrayDeque.pop();
        System.out.println(arrayDeque);

        arrayDeque.poll();
        System.out.println(arrayDeque);
        arrayDeque.pollLast();
        System.out.println(arrayDeque);

        arrayDeque.remove(0 );
        System.out.println(arrayDeque);
    }
}
