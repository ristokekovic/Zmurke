package com.example.riki.myplaces;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final Button button1 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent myPlaces = new Intent(RegistrationActivity.this, Main2Activity.class);
                startActivity(myPlaces);
            }
        });
    }



}
