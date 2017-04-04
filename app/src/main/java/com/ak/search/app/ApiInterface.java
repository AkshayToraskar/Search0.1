package com.ak.search.app;

import com.ak.search.model.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by dg hdghfd on 22-03-2017.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("DataCollectionApp/Login.php")
    Call<Login> getLogin(@Field("username") String username,
                         @Field("password") String password);


}
