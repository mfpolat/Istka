package com.okuu.istkaafet;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class OnlineMapFragment extends Fragment {

    private static View mView;
    private GoogleMap googleMap;
    private LatLng hospitalPosition, userPosition;
    private ArrayList<LatLng> markers;
    private IstkaLocationListenet mlocListener;

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
        LocationManager mlocManager;
        mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new IstkaLocationListenet();
        //if condition to check if GPS is available
        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                    mlocListener, null);
        } else if (mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mlocManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER, mlocListener, null);
        }
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
        googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.googleMapView))
                .getMap();
        googleMap.setMyLocationEnabled(true);
        MarkerOptions options = new MarkerOptions();
        options.position(hospitalPosition);
        Marker hospitalMarker = googleMap.addMarker(options);
        return mView;
    }

    private void drawRoute(LatLng userLocation, LatLng hospitalLocaion) {

        String url = getMapsApiDirectionsUrl(userLocation, hospitalLocaion);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
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

    private class IstkaLocationListenet implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            try {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                userPosition = new LatLng(latitude, longitude);
                markers.add(userPosition);
                fixZoom();
                drawRoute(userPosition, hospitalPosition);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
    private void fixZoom() {
        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        for (LatLng item : markers) {
            bc.include(item);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
    }
}
