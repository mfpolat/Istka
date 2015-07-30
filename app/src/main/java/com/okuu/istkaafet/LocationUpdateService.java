package com.okuu.istkaafet;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
 * Created by Fatih on 28.7.2015.
 */
public class LocationUpdateService extends IntentService implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static Handler timerHandler;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    private static int id;
    private static String token;
    private static long tenMins = 1000 * 60 * 10;
    SendLocationTask task;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        task = new SendLocationTask(5000, 1000);
    }

    public LocationUpdateService() {
        super("LocationUpdateService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("XXXXXXXXXXX", "Servis basladi");
        Bundle extras = intent.getExtras();
        id = extras.getInt("id");
        token = extras.getString("token", "");
        timerHandler = new Handler();
        task.start();
    }

    private void sendLocation() {

        if (mCurrentLocation != null) {
            UserLocation userLocation = new UserLocation();
            final String location = String.valueOf(mCurrentLocation.getLatitude()) + "  ,,,,, " + String.valueOf(mCurrentLocation.getLongitude());
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.SERVICE_BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            Service service = restAdapter.create(Service.class);
            service.updateDoctorLocation(id, token, userLocation, new Callback<BaseResponse>() {
                @Override
                public void success(BaseResponse baseResponse, Response response) {
                    Toast.makeText(getApplicationContext(), "Konum gonderildi", Toast.LENGTH_SHORT).show();
                    Log.e("XXXXXXXXXXXXXX", location + " Konum gonderildi");

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("XXXXXXXXXXXXXX", location + " Konum gonderilemedi");
                }
            });
        } else startLocationUpdates();

    }

    private void startTimer() {
    }

    private class SendLocationTask extends CountDownTimer {

        public SendLocationTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            Log.e("XXXXXXXXXX", "geri sayim");
        }

        @Override
        public void onFinish() {
            Log.e("XXXXXXXXXX", "geri sayim bitti");
            sendLocation();
            task.start();
        }
    }

    @Override
    public void onDestroy() {

        mGoogleApiClient.disconnect();
        Log.v("STOP_SERVICE", "DONE");
        super.onDestroy();
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
