package com.example.riki.myplaces;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPlacesMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp {

    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;

    private static final int NOTIFY_DISTANCE = 500;
    private static int EARTH_RADIUS = 6371000;

    private static boolean settingsBackgroundService;

    private boolean selCoorsEnabled = false;
    private LatLng placeLoc;
    public String apiKey;


    GoogleMap map;
    Snackbar snackbar;
    LocationManager locationManager;
    int state = 0;
    int timer = 0;
    Bitmap bmp;
    public int iterator;
    private FloatingActionButton selectSafeZone;
    private TextView countdown;
    private Intent backgroundService;
    private long miliseconds;
    CountDownTimer countDownTimer;

    private HashMap<Marker, Integer> markerPlaceIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to use network operations in main thread (BackgroundService)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_my_places_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        iterator = 0;
        miliseconds = 65000;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        apiKey = intent.getExtras().getString("api");
        if (extras.containsKey("time_left")) {
            miliseconds = intent.getExtras().getLong("time_left");
        }

        countdown = (TextView) findViewById(R.id.timer);
        countDownTimer = new CountDownTimer(miliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                miliseconds = millisUntilFinished;
                if(millisUntilFinished / 1000 > 60)
                    countdown.setTextColor(Color.GREEN);
                else
                    countdown.setTextColor(Color.RED);
                if((millisUntilFinished % 60000) / 1000 < 10)
                    countdown.setText(millisUntilFinished / 60000 + " : 0" + (millisUntilFinished % 60000) / 1000);
                else
                    countdown.setText(millisUntilFinished / 60000 + " : " + (millisUntilFinished % 60000) / 1000);
            }

            public void onFinish() {
                countdown.setText("0 : 00");
            }
        }.start();

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        DownloadManager.getInstance().setThreadWakeUp(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        selectSafeZone = (FloatingActionButton) findViewById(R.id.selectSafeZone);
        selectSafeZone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selCoorsEnabled = true;
                selectSafeZone.setEnabled(false);
                selectSafeZone.setVisibility(View.INVISIBLE);
                snackbar = Snackbar.make(coordinatorLayout, getString(R.string.safe_zone), Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng){
                if(selCoorsEnabled) {
                    String lon = Double.toString(latLng.longitude);
                    String lat = Double.toString(latLng.latitude);
                    selCoorsEnabled = false;
                    snackbar.dismiss();
                    Log.d("Clicked", "Picked up");
                }
            }
        });

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

    @Override
    protected void onPause() {
        super.onPause();

        backgroundService = new Intent(MyPlacesMapActivity.this, BackgroundService.class);
        backgroundService.putExtra("api", apiKey);
        backgroundService.putExtra("miliseconds", miliseconds);

        if(!isMyServiceRunning(BackgroundService.class)){
            startService(backgroundService);
        }

        System.gc();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }


    private void addMarker(JSONObject friend, Bitmap bmp){

        try{
            String lat = friend.getString("latitude");
            String lon = friend.getString("longitude");
            String name = friend.getString("name");

            //Float distanceFromMarker = distanceBetween((float)myNewLat,(float)myNewLon,(float)marker.getPosition().latitude, (float)marker.getPosition().longitude);

            LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            if(friend.getString("avatar").equals("default.jpg")){
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.def));
            }
            else
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
            markerOptions.title(name);


            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                        Marker marker = map.addMarker(markerOptions);
                        markerPlaceIdMap.put(marker,iterator);
                        iterator++;
                    }
            });
        }
        catch (JSONException e){
            e.printStackTrace();
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

        map.clear();
        map.addCircle(new CircleOptions()
                .center(currentLocation)
                .radius(NOTIFY_DISTANCE)
                .strokeWidth(0f)
                .fillColor(0x550000FF));

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
                    }
                }

                if(state == 2){

                }

            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
