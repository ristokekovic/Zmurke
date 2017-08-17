package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordActivity extends AppCompatActivity implements IThreadWakeUp{

    String apiKey,oldpString, newpString1, newpString2, uEmail;
    boolean correct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        setTitle("Change password");
        Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");
        uEmail = intent.getExtras().getString("email");
        DownloadManager.getInstance().setThreadWakeUp(this);

        final EditText oldp = (EditText) findViewById(R.id.oPass);
        final EditText newp1 = (EditText) findViewById(R.id.nPass);
        final EditText newp2 = (EditText) findViewById(R.id.nPass2);

        final ImageView button1 = (ImageView) findViewById(R.id.cancelButtonPassword);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                finish();

            }
        });

        final ImageView button2 = (ImageView) findViewById(R.id.checkButtonPassword);
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //proverimo da li je stari password dobar
                oldpString = oldp.getText().toString();

                newpString1 = newp1.getText().toString();
                newpString2 = newp2.getText().toString();

                    if (newpString1.equals(newpString2)) {

                        DownloadManager.getInstance().updatePass(oldpString,newpString1,uEmail,apiKey);
                    }
                    else
                    {
                        Toast.makeText(PasswordActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                    }

            }

        });
}

    @Override
    public void ResponseOk(final String s) {


        if(s.isEmpty())
        {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje

            Toast.makeText(PasswordActivity.this, "Error with password update!", Toast.LENGTH_SHORT).show();

        }
        else
        {
            String html = "<!DOCTYPE html>";
            if(s.toLowerCase().contains(html.toLowerCase()))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            Toast.makeText(PasswordActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                        {
                            Toast.makeText(PasswordActivity.this, "Password successfully updated!", Toast.LENGTH_SHORT).show();

                        }


                });




            }
        }

    }
}
