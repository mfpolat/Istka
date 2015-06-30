package com.okuu.istkaafet;


import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class OfflineMapFragment extends Fragment {

    private View mView;
    // The MapView variable:
    private MapView mMapView;

    // Default map zoom level:
    private int MAP_ZOOM = 15;

    // Default map Latitude:
    private double MAP_LATITUDE = 41.046543;

    // Default map Longitude:
    private double MAP_LONGITUDE = 28.988153;

    private String osmDirName = "osmdroid";
    public static OfflineMapFragment newInstance() {
        OfflineMapFragment fragment = new OfflineMapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public OfflineMapFragment() {
        // Required empty public constructor
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
        mView =  inflater.inflate(R.layout.fragment_offline_map, container, false);
        mMapView = (MapView)mView.findViewById(R.id.mapview);

        copyTilesToSDCard();

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setClickable(true);

        // Setup the initial zoom and location for the mapview
        mMapView.getController().setZoom(MAP_ZOOM);
        mMapView.getController().setCenter(
                new GeoPoint(MAP_LATITUDE, MAP_LONGITUDE));

        // To prevent download online tiles using the network connection.
        mMapView.setUseDataConnection(false);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        return mView;
    }

    private void copyTilesToSDCard() {
        AssetManager assetManager = getActivity().getAssets();
        InputStream is;
        // the zip file lies in assets root
        String sourceFileName = "istanbul.zip";
        // the zip file in the scdard
        String destinationFileName = "osmdroid.zip";

        File osmDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + osmDirName);
        if (!osmDir.exists()) {
            osmDir.mkdir();
        }
        String filePath = Environment.getExternalStorageDirectory()
                + File.separator + osmDirName + File.separator
                + destinationFileName;
        try {
            is = assetManager.open(sourceFileName);
            FileOutputStream fo = new FileOutputStream(filePath);

            byte[] b = new byte[1024];
            int length;
            while ((length = is.read(b)) != -1) {
                fo.write(b, 0, length);
            }
            fo.flush();
            fo.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
