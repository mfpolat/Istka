package com.okuu.istkaafet;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fatih on 27.7.2015.
 */
public class LocationUpdater extends android.app.Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static Handler timerHandler;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    private static int id;
    private static String token;
    private static long tenMins = 1000 * 60 * 10;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("XXXXXXXXXXX", "Servis basladi");
        getUpdateInfos();
        timerHandler = new Handler();
        startTimer();
        return START_STICKY;
    }

    private void getUpdateInfos() {
        SharedPreferences preferences = getSharedPreferences("doctorinfo", MODE_PRIVATE);
        id = preferences.getInt("id", 0);
        token = preferences.getString("token", "");
    }


    private void startTimer() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final String location = String.valueOf(mCurrentLocation.getLatitude()) + "  ,,,,, " + String.valueOf(mCurrentLocation.getLongitude());
                UserLocation userLocation = new UserLocation();
                userLocation.latitude = String.valueOf(mCurrentLocation.getLatitude());
                userLocation.longitude = String.valueOf(mCurrentLocation.getLongitude());
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(Constants.SERVICE_BASE_URL)
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build();
                Service service = restAdapter.create(Service.class);
                service.updateLocation(token, id, userLocation.latitude, userLocation.longitude, new Callback<UpdateLocationResponse>() {
                    @Override
                    public void success(UpdateLocationResponse updateLocationResponse, Response response) {
                        Log.e("XXXXXXXXXXXXXX", " Konum gonderildi  " + location);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("XXXXXXXXXXXXXX", " Konum gonderilemedi");
                    }
                });
                startTimer();
            }
        }, tenMins);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");

    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("XXXXXXXXXXXXXX", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }
        if (mCurrentLocation == null)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        startLocationUpdates();
    }
}
