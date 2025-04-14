package com.fastcampus.de.java.clip16;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


// 내가 호출할 API의 명세. 주소를 뺀 API 그 자체에 대한 명세.
// 어떤 리소스에 접근할 것이고, QueryString 은 어떤 걸로 할 것인지
// 아니면 post 라고 한다면 post 요청이 되는 body가 어떤 것인를
// 이 interface 타입으로 선언한 다음에 @GET으로 annotation 으로 path 정하고
// @Query 로 QueryString 이나 body를 파라미터로 정하면 된다.
public interface RetrofitService {

    @GET("/api/users/") // @GET 실제로 어떤 API 인지 HTTP로 GET 요청을 할거다.
    Call<Object> retrofitTest(@Query("page") int page);
    // @Query : query string을 받아주는 annotation이다.
    // "page" : query string에 들어갈 key 값이 여기에 들어온다.
    // value 를 int page 변수에다가 할당해준다.

    // 여기 함수는 내용물이 없는데 여기있는 명세들을 가지고 레트로핏이 네트워크 요청할 수 있도록 자동으로 구현이 되기 때문에
    // 인터페이스 선언만 가지고도 API 호출이 된 것이다.
}
