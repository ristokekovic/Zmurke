package com.example.riki.myplaces;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, IThreadWakeUp {

    private static final int NOTIFY_DISTANCE = 5000;
    private static final int CATCH_DISTANCE = 10;
    private static final int SAFE_ZONE_DISTANCE = 5000;
    private static int EARTH_RADIUS = 6371000;

    GoogleMap map;
    Snackbar snackbar;
    LocationManager locationManager;
    private String apiKey;
    private User user;
    private int state;
    private ArrayList<SafeZone> zones;
    private HashMap<Marker, Integer> markerPlaceIdMap;
    private Bitmap bmp;
    private int iterator;
    private double currentLat;
    private double currentLon;
    private boolean enabled = false;
    private boolean countdownStarted = false;
    private boolean loadedSafeZones = false;
    private boolean snackbarShown = false;
    private CountDownTimer countDownTimer;
    private CountDownTimer survivalTimer;
    private CountDownTimer inactiveTimer;
    private ConstraintLayout constraintLayout;
    private TextView countdown;
    private TextView points;
    private ImageView hunterIcon;
    private long miliseconds;
    private long survivedMiliseconds;
    private long inactiveMiliseconds;
    private Intent backgroundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        DownloadManager.getInstance().setThreadWakeUp(this);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        countdown = (TextView) findViewById(R.id.timer);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        apiKey = intent.getExtras().getString("api");
        user = (User) intent.getSerializableExtra("user");

        if(!user.hunter){
            survivalCountdown();
        }

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
        if(loadedSafeZones){
            enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (enabled) {
                if(snackbarShown){
                    snackbar.dismiss();
                }
            }

            currentLat = location.getLatitude();
            currentLon = location.getLongitude();

            LatLng position = new LatLng(currentLat, currentLon);

        /*map.addCircle(new CircleOptions()
                .center(position)
                .radius(50)
                .strokeWidth(5f)
                .strokeColor(Color.BLUE));*/

            state = 1;

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17.0f));
            SafeZone sz = zones.get(0);
            if (distanceBetween((float) currentLat, (float) currentLon, (float) sz.latitude, (float) sz.longitude) < SAFE_ZONE_DISTANCE) {
                if(!user.hunter){
                    if(!countdownStarted)
                        Toast.makeText(GameActivity.this, "You have entered your safe zone!", Toast.LENGTH_SHORT).show();
                    countdown();
                    state = 4;
                    DownloadManager.getInstance().addLocation((float) currentLat, (float) currentLon, true, apiKey);
                } else {
                    state = 4;
                    DownloadManager.getInstance().addLocation((float) currentLat, (float) currentLon, false, apiKey);
                }
            } else {
                if(!user.hunter)
                    stopCountdown();

                state = 4;
                DownloadManager.getInstance().addLocation((float) currentLat, (float) currentLon, false, apiKey);
            }
        }
    }

    public void countdown() {
        if (!countdownStarted) {
            countdownStarted = true;
            stopSurvivalCountdown();
            countdown.setVisibility(View.VISIBLE);

            countDownTimer = null;
            countDownTimer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    miliseconds = millisUntilFinished;
                    if (millisUntilFinished / 1000 > 10)
                        countdown.setTextColor(Color.GREEN);
                    else
                        countdown.setTextColor(Color.RED);
                    if((millisUntilFinished % 60000) / 1000 == 10)
                        Toast.makeText(GameActivity.this, "Get out of your safe zone!", Toast.LENGTH_SHORT).show();
                    if ((millisUntilFinished % 60000) / 1000 < 10){
                        countdown.setText(millisUntilFinished / 60000 + " : 0" + (millisUntilFinished % 60000) / 1000);
                    }
                    else{
                        countdown.setText(millisUntilFinished / 60000 + " : " + (millisUntilFinished % 60000) / 1000);
                    }

                }

                public void onFinish() {
                    countdown.setText("0 : 00");
                    Toast.makeText(GameActivity.this, "You lost 10 points", Toast.LENGTH_SHORT).show();
                    user.points -= 10;
                    points.setText("Points: " + user.points);
                    state = 5;
                    DownloadManager.getInstance().subtractPoints(apiKey, 10);
                }
            }.start();
        }
    }

    public void stopCountdown()
    {
        countDownTimer.cancel();
        countdown.setVisibility(View.GONE);
        countdownStarted = false;
    }

    public void survivalCountdown()
    {
        if(!user.hunter){

            survivalTimer = new CountDownTimer(600000, 1000) {

                long milisecondsPassed = 0;

                public void onTick(long millisUntilFinished) {
                    survivedMiliseconds = millisUntilFinished;
                    milisecondsPassed += 1000;
                    if (milisecondsPassed % 60000 == 0){
                        Toast.makeText(GameActivity.this, "You survived for one minute and gained 20 points", Toast.LENGTH_SHORT).show();
                        user.points += 20;
                        points.setText("Points: " + user.points);
                        state = 4;
                        DownloadManager.getInstance().subtractPoints(apiKey, 10);
                    }
                }

                public void onFinish() {
                }
            }.start();
        }
    }

    public void stopSurvivalCountdown()
    {
        survivalTimer.cancel();
        survivalCountdown();
    }

    public void inactiveCountdown()
    {
        Toast.makeText(GameActivity.this, "You have one minute to hide again", Toast.LENGTH_SHORT).show();
        inactiveTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                inactiveMiliseconds = millisUntilFinished;
                if(millisUntilFinished == 30000){
                    Toast.makeText(GameActivity.this, "You have 30 more seconds of invisibility", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFinish() {
                state = 10;
                DownloadManager.getInstance().setActive(apiKey);
            }
        }.start();
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
    public void ResponseOk(String s) {
        if (s.isEmpty()) {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje
        } else {
            String html = "<!DOCTYPE html>";
            if (s.toLowerCase().contains(html.toLowerCase())) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui
                    }
                });
            } else {
                if (state == 1) {
                    try {
                        JSONArray friends = new JSONArray(s);
                        markerPlaceIdMap = new HashMap<Marker, Integer>((int) ((double) friends.length() * 1.2));
                        for (int i = 0; i < friends.length(); i++) {
                            final JSONObject friend = friends.getJSONObject(i);
                            String lat = friend.getString("latitude");
                            String lon = friend.getString("longitude");
                            Float distance = distanceBetween((float) currentLat,
                                    (float) currentLon,
                                    Float.valueOf(lat),
                                    Float.valueOf(lon));
                            boolean inSafeZone = friend.getInt("in_safe_zone") == 1;
                            boolean active = friend.getInt("active") == 1;
                            if (distance < NOTIFY_DISTANCE && !inSafeZone && active) {
                                try {
                                    if (!friend.getString("avatar").equals("default.jpg")) {
                                        URL url = new URL("zmurke.herokuapp.com/" + friend.getString("avatar"));
                                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    } else
                                        bmp = null;
                                    addMarker(friend, bmp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        URL url;
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

                                if(distance < CATCH_DISTANCE && user.hunter && !friend.getBoolean("hunter")){
                                    Toast.makeText(GameActivity.this, "You caught " + friend.getString("name"), Toast.LENGTH_SHORT).show();
                                    user.points += 50;
                                    points.setText("Points: " + user.points);
                                    state = 6;
                                    DownloadManager.getInstance().addPoints(apiKey, 50);
                                }

                                if(distance < CATCH_DISTANCE && !user.hunter && friend.getBoolean("hunter")){
                                    Toast.makeText(GameActivity.this, "You have been caught by " + friend.getString("name"), Toast.LENGTH_SHORT).show();
                                    user.points -= 100;
                                    points.setText("Points: " + user.points);
                                    stopSurvivalCountdown();
                                    state = 7;
                                    DownloadManager.getInstance().subtractPoints(apiKey, 100);
                                }

                                if(distance < CATCH_DISTANCE && user.hunter && friend.getBoolean("hunter")){
                                    Toast.makeText(GameActivity.this, "Just another hunter...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (state == 2 || state == 3) {
                    try {
                        JSONArray safeZones = new JSONArray(s);
                        zones = new ArrayList<SafeZone>();
                        for (int i = 0; i < safeZones.length(); i++) {
                            final JSONObject safeZone = safeZones.getJSONObject(i);
                            final LatLng position = new LatLng(safeZone.getDouble("latitude"), safeZone.getDouble("longitude"));
                            Random rnd = new Random();
                            SafeZone zone = new SafeZone(
                                    safeZone.getInt("id"),
                                    position.latitude,
                                    position.longitude,
                                    safeZone.getInt("user_id"),
                                    Color.argb(50, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)),
                                    safeZone.getString("name")
                            );
                            zones.add(zone);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addSafeZones();
                                    loadedSafeZones = true;
                                    if (enabled) {
                                        DownloadManager.getInstance().getFriendsLocation(apiKey);
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (state == 4) {
                    state = 1;
                    DownloadManager.getInstance().getFriendsLocation(apiKey);
                }

                if (state == 5){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdownStarted = false;
                            countdown();
                        }
                    });
                }

                if(state == 7){
                    state = 8;
                    DownloadManager.getInstance().setInactive(apiKey);
                }

                if(state == 8){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        inactiveCountdown();
                        }
                    });
                }

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1
            );
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            snackbar = Snackbar.make(constraintLayout, getString(R.string.turn_on_location), Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            snackbarShown = true;
        }

        hunterIcon = (ImageView) findViewById(R.id.hunter_icon);
        if (user.hunter) {
            hunterIcon.setImageResource(R.drawable.hunter);
            hunterIcon.setColorFilter(R.color.red);
        } else {
            hunterIcon.setImageResource(R.drawable.regular);
            hunterIcon.setColorFilter(R.color.yellow);
        }

        points = (TextView) findViewById(R.id.points);
        points.setText("Points: " + user.points);

        state = 2;
        DownloadManager.getInstance().getFriendsSafeZones(apiKey);

    }

    public void addSafeZones() {
        for (int i = 0; i < zones.size(); i++) {
            LatLng position = new LatLng(zones.get(i).latitude, zones.get(i).longitude);
            map.addCircle(new CircleOptions()
                    .center(position)
                    .radius(50)
                    .strokeWidth(0f)
                    .fillColor(zones.get(i).color));
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(position);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.safehouse));
            markerOptions.title(zones.get(i).name);
            Marker marker = map.addMarker(markerOptions);
        }
    }

    private void addMarker(JSONObject friend, Bitmap bmp) {

        try {

            String lat = friend.getString("latitude");
            String lon = friend.getString("longitude");
            String name = friend.getString("name");

            //Float distanceFromMarker = distanceBetween((float)myNewLat,(float)myNewLon,(float)marker.getPosition().latitude, (float)marker.getPosition().longitude);

            LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            final MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            if (friend.getString("avatar").equals("default.jpg")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.def));
            } else
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
            markerOptions.title(name);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    Marker marker = map.addMarker(markerOptions);
                    markerPlaceIdMap.put(marker, iterator);
                    iterator++;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static float distanceBetween(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    @Override
    protected void onPause() {
        super.onPause();

        backgroundService = new Intent(GameActivity.this, BackgroundService.class);
        backgroundService.putExtra("api", apiKey);
        backgroundService.putExtra("miliseconds", miliseconds);

        if (!isMyServiceRunning(BackgroundService.class)) {
            startService(backgroundService);
        }

        System.gc();
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
