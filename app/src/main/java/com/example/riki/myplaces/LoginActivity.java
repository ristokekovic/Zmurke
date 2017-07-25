package com.example.riki.myplaces;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent myPlaces = new Intent(LoginActivity.this, Main2Activity.class);
                startActivity(myPlaces);
            }
        });

        final Button button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent myPlaces = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(myPlaces);
            }
        });



    }


}
