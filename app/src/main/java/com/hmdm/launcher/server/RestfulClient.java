package com.hmdm.launcher.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulClient {

    // server URL
    // private static final String WEB_BASE_URL = ""; // local server host
    private static final String WEB_BASE_URL = "http://192.168.0.27:9001";

    // cookie 사용을 위한 코드
    static OkHttpClient client = new OkHttpClient.Builder().build();

    public static RetrofitAPI getApiService() {
        return getInstance().create(RetrofitAPI.class);
    }

    private static Retrofit getInstance() {
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(WEB_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
