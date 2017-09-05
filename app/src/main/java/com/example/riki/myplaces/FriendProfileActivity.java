package com.example.riki.myplaces;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class FriendProfileActivity extends AppCompatActivity implements  IThreadWakeUp {
    String apiKey;
    String namef, pnumber,emailf, pointsf;
    TextView name1,lastname1,phone1,email1,points1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9),(int) (height* 0.65));

        final Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");
        DownloadManager.getInstance().setThreadWakeUp(this);

        name1 = (TextView) findViewById(R.id.nameFriend);
        lastname1 = (TextView) findViewById(R.id.lastNameFriend);
        email1 = (TextView) findViewById(R.id.emailFriend);
        phone1 = (TextView) findViewById(R.id.phoneFriend);
        points1 = (TextView) findViewById(R.id.pointsFriend);

        if (apiKey != null) {

            DownloadManager.getInstance().getUser(apiKey);

        }


        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        final ImageView iw1 = (ImageView) findViewById(R.id.cancelButton);
        iw1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                iw1.startAnimation(animation);
                finish();

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
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
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
                        //  setErrorMessage();
                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stuff that updates ui


                            try {


                                JSONObject reader = new JSONObject(s);
                                String email = reader.getString("email");
                                //userMail = email;

                                String username = reader.getString("name");
                                String firstName = reader.getString("first_name");
                                String lastName = reader.getString("last_name");
                                String phoneNumber = reader.getString("phone_number");
                                String urlImage = reader.getString("avatar");
                                String pointsNumber = reader.getString("points");

                                if (firstName != "null")
                                    name1.setText(firstName);
                                else
                                    name1.setText("");

                                if (lastName != "null")
                                    lastname1.setText(lastName);
                                else
                                    lastname1.setText("");

                                if (phoneNumber != "null")
                                    phone1.setText(phoneNumber);
                                else
                                    phone1.setText("");

                                if (urlImage != "null")
                                {
                                    new FriendProfileActivity.DownloadImageTask((ImageView) findViewById(R.id.friendPhoto))
                                            .execute("https://zmurke.herokuapp.com" + urlImage);

                                   /* b = Base64.decode(imgBase64, Base64.DEFAULT);
                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(b, 0, b.length);
                                    buttonphoto.setImageBitmap(decodedImage);*/

                                }

                                if(pointsNumber!=null)
                                    points1.setText(pointsNumber);

                                if (email != null)
                                email1.setText(email);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                    }
                });




            }
        }


    }



    //klasa za ucitavanje slika sa URL-a
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
