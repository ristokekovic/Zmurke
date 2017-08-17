package com.example.riki.myplaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class MyPlacesMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp {

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;

    private boolean selCoorsEnabled = false;
    private LatLng placeLoc;
    public String apiKey;

    GoogleMap map;
    LocationManager locationManager;
    int state = 0;
    int timer = 0;
    Bitmap bmp;

    private HashMap<Marker, Integer> markerPlaceIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_places_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //  map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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
        Intent mapIntent = getIntent();
        apiKey = mapIntent.getExtras().getString("api");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        DownloadManager.getInstance().setThreadWakeUp(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if(state == SHOW_MAP)
        {
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
        }
        else if(state == SELECT_COORDINATES)
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
                @Override
                public void onMapClick(LatLng latLng){
                    if(state == SELECT_COORDINATES && selCoorsEnabled){
                        String lon = Double.toString(latLng.longitude);
                        String lat = Double.toString(latLng.latitude);
                        Intent locationIntent = new Intent();
                        locationIntent.putExtra("lon", lon);
                        locationIntent.putExtra("lat", lat);
                        setResult(Activity.RESULT_OK, locationIntent);
                        finish();
                    }
            }
        });
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLoc, 15));

        state = 1;
        DownloadManager.getInstance().getFriendsLocation(apiKey);

    }

    private void addMyPlacesMarkers(JSONArray friends){
        //ArrayList<MyPlace> places = MyPlacesData.getInstance().getMyPlaces();
        markerPlaceIdMap = new HashMap<Marker, Integer>((int)((double)friends.length()*1.2));
        for(int i=0; i<friends.length(); i++)
        {
            try{
                JSONObject friend = friends.getJSONObject(i);
                String lat = friend.getString("latitude");
                String lon = friend.getString("longitude");
                String name = friend.getString("name");
                String avatar = friend.getString("avatar");

                //Float distanceFromMarker = distanceBetween((float)myNewLat,(float)myNewLon,(float)marker.getPosition().latitude, (float)marker.getPosition().longitude);

                LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                /*byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(decodedByte));*/
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.myplace));
                markerOptions.title(name);
                final int iterator = i;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui

                        Marker marker = map.addMarker(markerOptions);
                        markerPlaceIdMap.put(marker,iterator);
                    }
                });
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
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

    @Override
    public void onLocationChanged(Location location) {

        timer++;
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        state = 2;
        DownloadManager.getInstance().addLocation((float)location.getLatitude(), (float)location.getLongitude(), apiKey);
        if(timer == 10){
            map.clear();
            state = 1;
            DownloadManager.getInstance().getFriendsLocation(apiKey);
        }
        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(state == SELECT_COORDINATES && !selCoorsEnabled)
        {
            menu.add(0, 1, 1,"Select Coordinates");
            menu.add(0, 2, 2,"Cancel");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case 1:
                selCoorsEnabled = true;
                Toast.makeText(this, "Select coordinates", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void ResponseOk(String s) //on ceka da se thread zavrsi odnosno da dobije podatke sa servera
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
                    try {
                        JSONArray friends = new JSONArray(s);
                        addMyPlacesMarkers(friends);
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
                    }
                }

                if(state == 2){

                }

            }
        }
    }
}
