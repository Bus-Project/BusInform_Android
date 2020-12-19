package com.example.businformapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class LocationTracker {
    private final Context mContext;
    private final Activity mActivity;
    private Location location;
    private double longitude;
    private double latitude;

    private final String TAG = "Location";

    LocationTracker(Object activity) {
        this.mContext = (Context) activity;
        this.mActivity = (Activity) activity;

        getLocation();
    }

    private void getLocation() {
        try {
            LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[] {
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 0);
                Log.e(TAG, "ACCESS_FINE_LOCATION is NOT permitted. Please allow it and restart the application.");
                return;
            }

            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.e(TAG, "Neither GPS nor Network is NOT enabled.");
                return;
            }

            if(isNetworkEnabled) {
                Log.i(TAG, "Network Status: Enabled");

                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null) {
                    this.longitude = location.getLongitude();
                    this.latitude = location.getLatitude();

                    return;
                }
            }

            if(isGPSEnabled) {
                Log.i(TAG, "GPS Status: Enabled");

                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    this.longitude = location.getLongitude();
                    this.latitude = location.getLatitude();

                    return;
                }
            }

            Log.e(TAG, "Could NOT get location. Something went wrong.");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public Double getLong() {
        if (location == null) {
            Log.e(TAG, "Unable to get longitude.");
            return null;
        }
        return this.longitude;
    }

    public Double getLat() {
        if (location == null) {
            Log.e(TAG, "Unable to get latitude.");
            return null;
        }
        return this.latitude;
    }
}
