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

public class FriendProfileActivity extends AppCompatActivity {
    String apiKey;
    String frname, frlastn, fremail, frphone,frurl,frpoints;
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
        frname = intent.getExtras().getString("fname");
        frlastn = intent.getExtras().getString("lname");
        fremail = intent.getExtras().getString("email");
        frphone = intent.getExtras().getString("phone");
        frurl = intent.getExtras().getString("url");
        frpoints = intent.getExtras().getString("points");
        //DownloadManager.getInstance().setThreadWakeUp(this);

        name1 = (TextView) findViewById(R.id.nameFriend);
        lastname1 = (TextView) findViewById(R.id.lastNameFriend);
        email1 = (TextView) findViewById(R.id.emailFriend);
        phone1 = (TextView) findViewById(R.id.phoneFriend);
        points1 = (TextView) findViewById(R.id.pointsFriend);

        name1.setText(frname);
        lastname1.setText(frlastn);
        email1.setText(fremail);
        phone1.setText(frphone);
        points1.setText(frpoints);
        if (frurl != "null") {
            new FriendProfileActivity.DownloadImageTask((ImageView) findViewById(R.id.friendPhoto))
                    .execute("https://zmurke.herokuapp.com" + frurl);
        }
       /* if (apiKey != null) {

            DownloadManager.getInstance().getUser(apiKey);

        }*/


        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        final ImageView iw1 = (ImageView) findViewById(R.id.cancelButton);
        iw1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                iw1.startAnimation(animation);
                finish();

            }
        });
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
