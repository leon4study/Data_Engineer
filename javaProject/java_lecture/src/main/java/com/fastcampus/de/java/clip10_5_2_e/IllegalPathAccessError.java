package com.fastcampus.de.java.clip10_5_2_e;

public class IllegalPathAccessError extends Error{

    private String path;

    public IllegalPathAccessError(String path) {
        super(); //에러의 초기화함수도 호출을 해줄게
        this.path = path;
    }

    @Override
    public String getMessage() {
        return path + "is not allowed access." + super.getMessage();
    }
}
