package com.okuu.istkaafet;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class OnlineMapFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static View mView;
    private GoogleMap googleMap;
    private LatLng hospitalPosition, userPosition;
    private ArrayList<LatLng> markers;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    public static OnlineMapFragment newInstance(double hospitalLat, double hospitalLong) {
        OnlineMapFragment fragment = new OnlineMapFragment();
        Bundle args = new Bundle();
        args.putDouble("hospLat", hospitalLat);
        args.putDouble("hospLng", hospitalLong);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new ArrayList<>();
        if (getArguments() != null) {
            double hospitalLat = getArguments().getDouble("hospLat");
            double hospitalLong = getArguments().getDouble("hospLng");
            hospitalPosition = new LatLng(hospitalLat, hospitalLong);
            markers.add(hospitalPosition);
        }
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }
        try {
            mView = inflater.inflate(R.layout.fragment_online_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        ((MainActivity)getActivity()).isOnlineMap = true;
        googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googleMapView))
                .getMap();

        googleMap.setMyLocationEnabled(true);
        MarkerOptions options = new MarkerOptions();
        options.position(hospitalPosition);
        if (hospitalPosition.latitude != 0 || hospitalPosition.longitude != 0)
            googleMap.addMarker(options);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.025232, 29.017080), 6.0f));
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        ((MainActivity)getActivity()).isOnlineMap = false;
        super.onStop();

    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("XXXXXXXXXXXXXX", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
        Log.i("XXXXXXXXXX", "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        }
        if (mCurrentLocation == null)
            startLocationUpdates();
        else {
            userPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            if (hospitalPosition.latitude == 0 || hospitalPosition.longitude == 0)
                showWarning("Hastane konumu alinamadý");
            else
                drawRoute(userPosition, hospitalPosition);
        }

    }

    private void showWarning(String message) {
        SnackBar snackbar = new SnackBar(getActivity(), message, "", null);
        snackbar.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (hospitalPosition.latitude == 0 || hospitalPosition.longitude == 0)
            showWarning("Hastane konumu alnamadý");
        else
            drawRoute(userPosition, hospitalPosition);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        startLocationUpdates();
    }

    private void drawRoute(LatLng userLocation, LatLng hospitalLocaion) {

        sendUserLocation();
        String url = getMapsApiDirectionsUrl(userLocation, hospitalLocaion);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        // fixZoom();
    }

    private String getMapsApiDirectionsUrl(LatLng userLotcaion, LatLng hospitalLocation) {
        String userLoc = String.valueOf(userLotcaion.latitude) + "," + String.valueOf(userLotcaion.longitude);
        String hospLoc = String.valueOf(hospitalLocation.latitude) + "," + String.valueOf(hospitalLocation.longitude);
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + userLoc + "&destination=" + hospLoc + "&sensor=false";
        return url;
    }


    private class ReadTask extends AsyncTask<String, Void, String> {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setTitle("");
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }


        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }
            googleMap.addPolyline(polyLineOptions);
        }
    }
    private void sendUserLocation(){
        String token = ((MainActivity)getActivity()).retriveAccessToken();
        Doctor doctor =((MainActivity)getActivity()).retriveDoctorInfor();
        int id = doctor.getId();
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
                Log.e("XXXXXXXXXXXXXX", " Konum gonderildi" );
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("XXXXXXXXXXXXXX",  " Konum gonderilemedi" );
            }
        });
    }
}
