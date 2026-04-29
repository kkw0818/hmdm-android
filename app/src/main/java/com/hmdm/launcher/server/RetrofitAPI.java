package com.hmdm.launcher.server;

import retrofit2.Call;
import retrofit2.http.GET;
public interface RetrofitAPI {
    @GET("/routes/host")
    Call<String> getInitConfig();

}
