package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity implements IThreadWakeUp {

    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        DownloadManager.getInstance().setThreadWakeUp(this);

        final Button button1 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                String name = username.getText().toString();
                EditText emailTxt = (EditText) findViewById(R.id.emailT);
                String email = emailTxt.getText().toString();
                EditText passwordTxt = (EditText) findViewById(R.id.password);
                String password = passwordTxt.getText().toString();

                if(name.equals("") || email.equals("") || password.equals(""))
                {
                    error = (TextView) findViewById(R.id.error);
                    error.setVisibility(View.VISIBLE);
                }
                else
                {
                    DownloadManager.getInstance().register(name, email, password);
                }



            }
        });
    }


    public void setErrorMessage()
    {
        error = (TextView) findViewById(R.id.error);
        error.setText(R.string.email_taken);
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
            try {
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
                else
                {
                    Intent intent = new Intent(this, LoginActivity.class);
                    JSONObject reader = new JSONObject(s);
                    String username = reader.getString("email");
                    intent.putExtra("email", username);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}
