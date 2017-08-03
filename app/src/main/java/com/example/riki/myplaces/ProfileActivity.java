package com.example.riki.myplaces;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements  IThreadWakeUp{

    final int PICK_IMAGE = 100;
    EditText firstname,lastname,username1,phone, email1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        String apiKey = intent.getExtras().getString("api");
        Bundle extras = intent.getExtras();

        DownloadManager.getInstance().setThreadWakeUp(this);

        if (apiKey != null) {

            DownloadManager.getInstance().getUser(apiKey);

        }

        final ImageView button = (ImageView) findViewById(R.id.slika);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);

            }
        });

        final Button button1 = (Button) findViewById(R.id.oldPass);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this,PasswordActivity.class);
                startActivity(intent);

            }
        });


    }

     @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    final ImageView button = (ImageView) findViewById(R.id.slika);
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    button.setImageBitmap(yourSelectedImage);
                    final TextView tx = (TextView) findViewById(R.id.textView4);
                    tx.setVisibility(View.INVISIBLE);
                }
        }
    }

   @Override
    public void ResponseOk(final String s) //on ceka da se thread zavrsi odnosno da dobije podatke sa servera
    {

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
                        try{
                            JSONObject reader = new JSONObject(s);
                            String email = reader.getString("email");
                            String username = reader.getString("name");
                            String firstName = reader.getString("first_name");
                            String lastName = reader.getString("last_name");
                            String phoneNumber = reader.getString("phone_number");
                            String imgBase64 = reader.getString("avatar");
                            firstname = (EditText) findViewById(R.id.firstnameT);
                            if(firstName != "null")
                                firstname.setText(firstName);
                            else
                                firstname.setText("");

                            lastname = (EditText) findViewById(R.id.lastnameT);
                            if(lastName != "null")
                                lastname.setText(lastName);
                            else
                                lastname.setText("");

                            phone = (EditText) findViewById(R.id.phoneT);
                            if(phoneNumber != "null")
                                phone.setText(phoneNumber);
                            else
                                phone.setText("");
                            email1 = (EditText) findViewById(R.id.emailT);
                            email1.setText(email);
                            username1 = (EditText) findViewById(R.id.usernameT);
                            username1.setText(username);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                });




            }
        }
    }
}
