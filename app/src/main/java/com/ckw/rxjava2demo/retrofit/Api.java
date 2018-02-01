package com.ckw.rxjava2demo.retrofit;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by ckw
 * on 2018/2/1.
 */

public interface Api {
    @GET("xixihaha")
    Observable<String> login(@Body String request);

    @GET("xixihaha")
    Observable<String> register(@Body String request);

}
