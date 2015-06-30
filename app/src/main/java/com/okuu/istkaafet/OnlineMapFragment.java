package com.okuu.istkaafet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlineMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineMapFragment extends Fragment {

    private View mView;
    MyItemizedOverlay myItemizedOverlay = null;

    public static OnlineMapFragment newInstance() {
        OnlineMapFragment fragment = new OnlineMapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_online_map, container, false);
        MapView mapView;
        MapController mapController;

        mapView = (MapView) mView.findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(5);
        GeoPoint gPt = new GeoPoint(51500000, -150000);
        mapController.setCenter(gPt);
        return mView;
    }


}
