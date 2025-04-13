package com.fastcampus.de.java.clip9_5_1;

import com.fastcampus.de.java.clip9_5.AccessModifierTest;

public class Anonymous {
    public void call(){
        AccessModifierTest accessModifierTest = new AccessModifierTest();
//        accessModifierTest.messageDefault();
//        accessModifierTest.messageProtected();
        accessModifierTest.messageOutside();
    }
}
