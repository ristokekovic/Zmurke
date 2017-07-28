package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        String apiKey = intent.getExtras().getString("api");
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

       // setTitle("Zmurururke");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        final ImageView v = (ImageView) findViewById(R.id.imageViewMap);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                v.startAnimation(animation);

                Intent map = new Intent(Main2Activity.this, MyPlacesMapActivity.class);
                startActivity(map);
                return true;
            }
        });


        final ImageView v1 = (ImageView) findViewById(R.id.imageViewHelp);
        v1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                v1.startAnimation(animation);
                Intent map = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(map);
                return true;
            }
        });

        final ImageView v2 = (ImageView) findViewById(R.id.imageViewProfile);
        v2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                v2.startAnimation(animation);
                Intent map = new Intent(Main2Activity.this, ProfileActivity.class);
                startActivity(map);
                return true;
            }
        });
        final ImageView v3 = (ImageView) findViewById(R.id.imageViewPlay);
        v3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                v3.startAnimation(animation);
                return true;
            }
        });

        final ImageView v4 = (ImageView) findViewById(R.id.imageViewRank);
        v4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                v4.startAnimation(animation);
                return true;
            }
        });


        final ImageView v5 = (ImageView) findViewById(R.id.imageViewFriends);
        v5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                v5.startAnimation(animation);
                return true;
            }
        });
    }
}
