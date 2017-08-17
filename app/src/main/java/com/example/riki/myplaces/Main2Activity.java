package com.example.riki.myplaces;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    boolean clickEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        final String apiKey = intent.getExtras().getString("api");
        clickEnabled = true;

        if (getIntent().getBooleanExtra("EXIT", false)) {

            Intent intent1 = new Intent(Main2Activity.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        }

        if (getIntent().getBooleanExtra("remembered", true))
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(!connected){
            Snackbar snackbar = Snackbar
                    .make(constraintLayout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE);

            snackbar.show();

            clickEnabled = false;

        }

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        final ImageView v = (ImageView) findViewById(R.id.imageViewMap);
        v.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this, MyPlacesMapActivity.class);
                intent.putExtra("api", apiKey);
                startActivity(intent);

            }
        });

        final ImageView v1 = (ImageView) findViewById(R.id.imageViewHelp);
        v1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v1.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);


            }
        });

        final ImageView v2 = (ImageView) findViewById(R.id.imageViewProfile);
        v2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v2.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
                intent.putExtra("api", apiKey);
                startActivity(intent);
            }
        });

        final ImageView v3 = (ImageView) findViewById(R.id.imageViewPlay);
        v3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v3.startAnimation(animation);

            }
        });

        final ImageView v4 = (ImageView) findViewById(R.id.imageViewRank);
        v4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v4.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this,RankingActivity.class);
                startActivity(intent);

            }
        });

        final ImageView v5 = (ImageView) findViewById(R.id.imageViewFriends);
        v5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                v5.startAnimation(animation);
                Intent intent = new Intent(Main2Activity.this,FriendsActivity.class);
                intent.putExtra("api", apiKey);
                startActivity(intent);
            }
        });
        
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!clickEnabled)
            return true;

        return super.dispatchTouchEvent(event);
    }


//        final ImageView v4 = (ImageView) findViewById(R.id.imageViewRank);
//        v4.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                v4.startAnimation(animation);
//                Intent map = new Intent(Main2Activity.this, RankingActivity.class);
//                startActivity(map);
//                return true;
//            }
//        });



}
