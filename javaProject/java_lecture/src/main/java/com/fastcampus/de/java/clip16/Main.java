package com.fastcampus.de.java.clip16;

import retrofit2.Call;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // 실제로 요청하는 class.
        // 클라이언트에서 getApi로 API를 호출할 레트로핏서비스 타입을 받아온 다음
        // 실제 인터페이스에서 만들었던 함수 호출로 RetrofitService를 가져올 수 있다.
        Call<Object> retrofitTest = RetrofitClient.getApi().retrofitTest(2);

        try {
            System.out.println(retrofitTest.execute().body());
            // execute 한 시점에 API가 호출된다.
            // execute 에서 네트워크를 쓰니까 IO Exception 핸들링 해줘야 함.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
