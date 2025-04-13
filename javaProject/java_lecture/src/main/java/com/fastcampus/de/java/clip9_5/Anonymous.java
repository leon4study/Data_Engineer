package com.fastcampus.de.java.clip9_5;

public class Anonymous {
    public void call(){
        AccessModifierTest accessModifierTest = new AccessModifierTest();
        accessModifierTest.messageDefault();
        accessModifierTest.messageProtected();
        accessModifierTest.messageOutside();
    }

    public static void main(String[] args) {
        Anonymous anonymous = new Anonymous();
        anonymous.call();
    }
}
