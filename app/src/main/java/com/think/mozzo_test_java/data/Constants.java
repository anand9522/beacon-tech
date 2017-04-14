package com.think.mozzo_test_java.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


public class Constants {

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 10 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    static {


        LANDMARKS.put("Home", new LatLng(37.621313,-122.378955));
    }
}
