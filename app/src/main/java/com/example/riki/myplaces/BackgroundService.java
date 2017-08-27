package com.example.riki.myplaces;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    //Different Id's will show up as different notifications
    private int mNotificationId;
    //Some things we only have to set the first time.
    private boolean firstNotification = true;
    NotificationCompat.Builder mBuilder = null;

    private String provider;

    public static float currentLat;
    public static float currentLon;
    public String apiKey;
    public long miliseconds;
    public static int myPoints = 0;

    CountDownTimer countDownTimer;

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
        int settingsGpsRefreshTime = 10;
        apiKey = intent.getStringExtra("api");
        miliseconds = intent.getLongExtra("miliseconds", 600000);

        countDownTimer = new CountDownTimer(miliseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                miliseconds = millisUntilFinished;
                if(millisUntilFinished / 1000 == 60)
                    showNotification(2, "One minute left!");

            }

            public void onFinish() {

            }
        }.start();



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
            currentLat = (float) location.getLatitude();
            currentLon = (float) location.getLongitude();

            myNewLat = currentLat;
            myNewLon = currentLon;

            //TODO: Requests for the server

            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            DownloadManager.getInstance().addLocation((float)location.getLatitude(), (float)location.getLongitude(), apiKey);

            DownloadManager.getInstance().getFriendsLocation(apiKey);

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
            }
            else {
                try {
                    JSONArray friends = new JSONArray(s);
                    for(int i = 0; i < friends.length(); i++)
                    {
                        final JSONObject friend = friends.getJSONObject(i);
                        double latitude = friend.getDouble("latitude");
                        double longitude = friend.getDouble("longitude");
                        Float distanceFromMarker = distanceBetween((float)currentLat,(float)currentLon,(float)latitude, (float)longitude);
                        if(distanceFromMarker < NOTIFY_DISTANCE) {
                           showNotification(1, friend.getString("name") + " is " + Math.round(distanceFromMarker) + " meters away from you!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showNotification(int uid,String text) {
        vibrationAndSoundNotification();

        mNotificationId = uid;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(firstNotification){
            firstNotification = false;
            mBuilder = new NotificationCompat.Builder(this)
                    .setOnlyAlertOnce(true)
                    .setPriority(Notification.PRIORITY_DEFAULT);

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MyPlacesMapActivity.class);
            resultIntent.putExtra("api", apiKey);
            resultIntent.putExtra("time_left", miliseconds);

            // The stack builder object will contain an artificial back stack for the started Activity.
            // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }

        if(uid==2){//landmark
            mBuilder
                    .setSmallIcon(R.drawable.crne)
                    .setContentTitle("Time is running out!");
        }else{//friend
            mBuilder
                    .setSmallIcon(R.drawable.crne)
                    .setContentTitle("You have friends nearby!");
        }

        mBuilder.setContentText(text);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
        System.gc(); //force garbage collector
    }

    private void vibrationAndSoundNotification() {
        Long time = System.currentTimeMillis()/1000;

        if(time-timeLastNotification>TIME_BETWEEN_NOTIFICATIONS){//notify user only every TIME_BETWEEN_NOTIFICATIONS seconds
            timeLastNotification = time;

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static void deleteNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public long getMiliseconds(){
        return this.miliseconds;
    }



}
