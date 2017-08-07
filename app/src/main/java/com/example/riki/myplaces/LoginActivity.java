package com.example.riki.myplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements IThreadWakeUp {

    EditText emailTxt;
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DownloadManager.getInstance().setThreadWakeUp(this);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);

        SharedPreferences app_preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        // Get the value for the run counter
        String apiKey = app_preferences.getString("api_key", "DEFAULT");
        if(!apiKey.equals("DEFAULT")){
            Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
            intent.putExtra("api", apiKey);
            intent.putExtra("remembered", true);
            startActivity(intent);
        }


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

            Button login = (Button) findViewById(R.id.button2);
            login.setEnabled(false);

        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String email = intent.getExtras().getString("email");
            emailTxt = (EditText) findViewById(R.id.editText);
            emailTxt.setText(email);
            Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
        }

        final Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                emailTxt = (EditText) findViewById(R.id.editText);
                String email = emailTxt.getText().toString();
                EditText passwordTxt = (EditText) findViewById(R.id.editText2);
                String password = passwordTxt.getText().toString();

                if(email.equals("") || password.equals(""))
                {
                    error = (TextView) findViewById(R.id.error);
                    error.setVisibility(View.VISIBLE);
                }
                else
                {
                    DownloadManager.getInstance().login(email, password);
                }

            }
        });

        final Button button = (Button) findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });



    }

    public void setErrorMessage()
    {
        error = (TextView) findViewById(R.id.error);
        error.setText(R.string.login_error);
        error.setVisibility(View.VISIBLE);
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
                        setErrorMessage();
                    }
                });
            }
            else {
                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                intent.putExtra("api", s);

                //saving user to persistent storage, in order to remember the user for future automated logins
                SharedPreferences app_preferences =
                        PreferenceManager.getDefaultSharedPreferences(this);

                SharedPreferences.Editor editor = app_preferences.edit();
                editor.putString("api_key", s);
                editor.apply(); // Very important
                startActivity(intent);
            }
        }
    }


}
