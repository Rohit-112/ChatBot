package com.map.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StopRepository {

    private static final String BASE_URL = "https://chartr-in.github.io/test-api/";
    private final StopApi api;

    public StopRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(StopApi.class);
    }

    public StopApi getApi() {
        return api;
    }
}
