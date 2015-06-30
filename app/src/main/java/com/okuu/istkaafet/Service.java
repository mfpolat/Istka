package com.okuu.istkaafet;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by fatih on 18.5.2015.
 */
public interface Service {

    @GET(Constants.SERVICE_DOCTORS)
    public void doctors(@Path("id") int id, @Query("access-token") int accessToken, Callback<Doctor> response);


}