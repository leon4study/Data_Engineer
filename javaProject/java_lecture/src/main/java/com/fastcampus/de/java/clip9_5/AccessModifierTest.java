package com.fastcampus.de.java.clip9_5;

public class AccessModifierTest {

    private void messageInside(){//private 같은 클래스 안에서만 호출
        System.out.println("This is private modifier");
    }

    void messageDefault(){
        messageInside();
        System.out.println("This is default(package-private) modifier");
    }

    protected void messageProtected(){
        messageInside();
        System.out.println("This is protected modifier");
    }

    public void messageOutside(){
        messageInside();
        System.out.println("This is public modifier");
    }


}
