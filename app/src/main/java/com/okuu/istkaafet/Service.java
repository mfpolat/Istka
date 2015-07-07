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

    @GET(Constants.SERVICE_REGISTER)
    public void register (@Query("username") String userName,@Query("password") String password,Callback<RegisterResponse> response);
    @GET(Constants.SERVICE_HOSPITALS)
    public void getHospitals(@Query("access-token") int accesToken,Callback<HospitalsResponse> response);
}
