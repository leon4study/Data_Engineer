package com.fastcampus.de.java.clip9_5_1;

import com.fastcampus.de.java.clip9_5.AccessModifierTest;

public class Child extends AccessModifierTest{
    public void call(){
//        this.messageInside();
//        this.messageDefault();
        this.messageProtected();
        this.messageOutside();
    }

    public static void main(String[] args) {
        Child child = new Child();
        child.call();
    }
}
