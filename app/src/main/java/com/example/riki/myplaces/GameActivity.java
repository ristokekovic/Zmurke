package com.example.riki.myplaces;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp {

    GoogleMap map;
    Snackbar snackbar;
    LocationManager locationManager;
    private String apiKey;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        DownloadManager.getInstance().setThreadWakeUp(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        apiKey = intent.getExtras().getString("api");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void ResponseOk(String s)
    {
        if(s.isEmpty())
        {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje
        }
        else
        {
            String html = "<!DOCTYPE html>";
            if(s.toLowerCase().contains(html.toLowerCase()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                    }
                });
            }
            else {
                if(state == 1) {
                    /*try {
                        JSONArray friends = new JSONArray(s);
                        markerPlaceIdMap = new HashMap<Marker, Integer>((int)((double)friends.length()*1.2));
                        for(int i = 0; i < friends.length(); i++)
                        {
                            final JSONObject friend = friends.getJSONObject(i);
                            try {
                                if(!friend.getString("avatar").equals("default.jpg")) {
                                    URL url = new URL("zmurke.herokuapp.com/" + friend.getString("avatar"));
                                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                }
                                else
                                    bmp = null;
                                addMarker(friend, bmp);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Thread thread = new Thread(new Runnable(){
                                @Override
                                public void run(){
                                    URL url ;
                                    try {
                                        url = new URL("https://zmurke.herokuapp.com" + friend.getString("avatar"));
                                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        addMarker(friend, Bitmap.createScaledBitmap(bmp, 80, 80, false));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                        //addMyPlacesMarkers(friends);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //stuff that updates ui
                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        return true;
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }

                if(state == 2 || state == 3){
                    try{
                        JSONArray safeZones = new JSONArray(s);
                        for(int i = 0; i < safeZones.length(); i++){
                            final JSONObject safeZone = safeZones.getJSONObject(i);
                            final LatLng position = new LatLng(safeZone.getDouble("latitude"), safeZone.getDouble("longitude"));
                            Log.d("Color", String.valueOf(Color.parseColor(safeZone.getString("color"))));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        map.addCircle(new CircleOptions()
                                                .center(position)
                                                .radius(50)
                                                .strokeWidth(0f)
                                                .fillColor(Color.parseColor(safeZone.getString("color"))));
                                        final MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(position);
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.safehouse));
                                        markerOptions.title(getString(R.string.safe_zone_title));
                                        Marker marker = map.addMarker(markerOptions);
                                    } catch (JSONException e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    1
            );
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        state = 2;
        DownloadManager.getInstance().getFriendsSafeZones(apiKey);
    }
}
