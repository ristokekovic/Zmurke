package com.example.riki.myplaces;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Riki on 8/16/2017.
 */

public class BackgroundService extends Service implements LocationListener, IThreadWakeUp {

    private static final String TAG = "BackgroundService";
    private static final long TIME_BETWEEN_NOTIFICATIONS = 60L;
    private static final int NOTIFY_DISTANCE = 30;
    private static final int CATCH_DISTANCE = 5;
    private static final int ADD_POINTS = 10;

    private static boolean serviceRunning;

    private LocationManager locationManager;

    private String provider;

    public static Double currentLat = null;
    public static Double currentLon = null;
    public String apiKey;

    private String loggedUserUid;
    public static int myPoints = 0;

    private Long timeLastNotification = 0L;

    public BackgroundService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BackgroundService onCreate started");

        //TODO: Pick up API token for the user

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        Log.d(TAG,"Location provider is selected: " + provider);
        Log.d(TAG,"BackgroundService onCreate ended");

        DownloadManager.getInstance().setThreadWakeUp(this);

        //TODO: Requests to the server
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"BackgroundService onStartCommand started");
        int settingsGpsRefreshTime = intent.getIntExtra("settingsGpsRefreshTime", 1);
        loggedUserUid = intent.getStringExtra("loggedUserUid");

        locationManager.requestLocationUpdates(provider, settingsGpsRefreshTime *1000, 0, this); //Actual time to get a new location is a little big higher- 3s instead of 1, 6s instead 5, 12s instead 10
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return 0;
        }else{
            //Location location = locationManager.getLastKnownLocation(provider);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"BackgroundService onDestroy");
        locationManager.removeUpdates(this);
        serviceRunning=false;
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isInternetAvailable() && isNetworkConnected()) {
            System.gc();    //force garbage collector
            double myNewLat, myNewLon;
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();

            myNewLat = currentLat;
            myNewLon = currentLon;

            Log.d(TAG,"New location: " + myNewLat + " " + myNewLon);

            //TODO: Requests for the server
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public static float distanceBetween(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    private boolean isNetworkConnected() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("landmarkgo-d1a7c.firebaseio.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            Toast.makeText(this, "Internet connection not available.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void ResponseOk(String s) {

    }
}
