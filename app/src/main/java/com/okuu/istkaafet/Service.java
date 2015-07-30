package com.okuu.istkaafet;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by fatih on 18.5.2015.
 */
public interface Service {

    @GET(Constants.SERVICE_DOCTORS)
    void doctors(@Path("id") int id, @Query("access-token") String accessToken, Callback<Doctor> response);

    @GET(Constants.SERVICE_REGISTER)
    void register(@Query("username") String userName, @Query("password") String password, Callback<RegisterResponse> response);

    @GET(Constants.SERVICE_HOSPITALS)
    void getHospitals(@Query("access-token") String accesToken, Callback<HospitalsResponse> response);

    @GET(Constants.SERVICE_ASSIGMENT)
    void getAssigment(@Query("id") int id, @Query("access-token") String accessToken, Callback<AssigmentResponse> response);

    @PUT(Constants.SERVICE_DOCTORS)
    void updateDoctorLocation(@Path("id") int id, @Query("access-token") String accessToken, @Body UserLocation userLocation, Callback<BaseResponse> response);

    @GET(Constants.SERVICE_UPDATE_LOCATION)
    void updateLocation(@Query("access-token") String accesToken, @Query("id") int doctorId, @Query("latitude") String latitude, @Query("longitude") String longitude, Callback<UpdateLocationResponse> response);
}
