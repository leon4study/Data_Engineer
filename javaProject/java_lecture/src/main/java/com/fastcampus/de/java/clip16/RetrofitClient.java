package com.fastcampus.de.java.clip16;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 호출하는 함수 만들 거다.
public class RetrofitClient {
    private static final String BASE_URL = "https://reqres.in/";

    // RetrofitService에서 선언된 클래스와 메소드의 타입 추론으로
    // 실제 RetrofitService 객체를 리턴하는 함수를 create 로 만든 것.
    public static RetrofitService getApi(){
        return getInstance() //getInstance를 이용해서 Retrofit 서비스를 받고
                .create(RetrofitService.class); //creat로 RetrofitService.class를 받는
        //실제로 API를 했을 때, (interface에서 만들었던) Annotation 단 RetrofitService 를 만들어서 넣어주는 함수.
    }

    // Create하기 위해서는 Retrofit 객체가 있어야 함 객체는 Retrofit.Builder 를 통해서 만듦.
    private static Retrofit getInstance(){
        Gson gson = new GsonBuilder() //json 파싱하기 위한 객체 만들어주기
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                //converterFactory : 메세지를 컨버팅 하는 규칙 넣어주는 것.
                .build();
        // 왜 이렇게 짜냐? 라고 하면 Retrofit이 이렇게 짜라고 강제했기 때문에.
    }
}
