package com.ak.search.app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dg hdghfd on 22-03-2017.
 */

public class ApiClient {
    public static final String BASE_URL = "http://192.168.1.126/";
    public static final String REGISTER_BASE_URL = "http://kmpl.ml/search/Register.php";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
