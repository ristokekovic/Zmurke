package com.example.riki.myplaces;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import android.content.SharedPreferences;
public class ProfileActivity extends AppCompatActivity implements  IThreadWakeUp{

    boolean updated = false;
    final int PICK_IMAGE = 100;
    EditText firstname,lastname,username1, email1, phone;
    String firstn, lastn, phonen;
    String apiKey;
    String encodedImage;
    ImageView buttonphoto;
    String userMail;
    File fajl, filee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");
        Bundle extras = intent.getExtras();
        encodedImage = "";
        DownloadManager.getInstance().setThreadWakeUp(this);

        firstname = (EditText) findViewById(R.id.firstnameT);
        lastname = (EditText) findViewById(R.id.lastnameT);
        phone = (EditText) findViewById(R.id.phoneT);

        if (apiKey != null) {

            DownloadManager.getInstance().getUser(apiKey);

        }

       // final ImageView button = (ImageView) findViewById(R.id.slika);
        final TextView tx = (TextView) findViewById(R.id.textView4);
      // tx.setVisibility(View.INVISIBLE);

        tx.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);

            }
        });




        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        final Button button1 = (Button) findViewById(R.id.oldPassButton);
        button1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
        button1.startAnimation(animation);
                Intent intent = new Intent(ProfileActivity.this,PasswordActivity.class);
                intent.putExtra("api", apiKey);
                intent.putExtra("email",userMail);
                startActivity(intent);

            }
        });

        final Button logOut = (Button) findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
        logOut.startAnimation(animation);

                SharedPreferences spreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor spreferencesEditor = spreferences.edit();
                spreferencesEditor.clear();
                spreferencesEditor.commit();
                Toast.makeText(ProfileActivity.this, "See you soon!", Toast.LENGTH_SHORT).show();


                Intent intent1 = new Intent(getApplicationContext(), Main2Activity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("EXIT",true);
                intent1.putExtra("remembered", false);
                startActivity(intent1);

            }
        });

        final ImageView iw1 = (ImageView) findViewById(R.id.cancelButton);
        iw1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
        iw1.startAnimation(animation);
                finish();

            }
        });

        final ImageView iw2= (ImageView) findViewById(R.id.checkButton);
        iw2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        iw2.startAnimation(animation);
                if (apiKey != null) {

                    updated = true;
                    firstn = firstname.getText().toString();
                    lastn = lastname.getText().toString();
                    phonen = phone.getText().toString();

                    if(filee != null)
                        DownloadManager.getInstance().newUpdate(firstn,lastn,phonen,filee,apiKey);
                    else
                        DownloadManager.getInstance().update(firstn,lastn,phonen,apiKey);

                }

            }
        });

    }


     /*@Override
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

                    buttonphoto = (ImageView) findViewById(R.id.slika);
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

                    Bitmap resized = Bitmap.createScaledBitmap(yourSelectedImage,(int)(yourSelectedImage.getWidth()*0.2), (int)(yourSelectedImage.getHeight()*0.2), true);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    b = baos.toByteArray();

                   encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                    buttonphoto.setImageBitmap(resized);

                    resized.recycle();
                  //  button.setImageBitmap(resized);
                    final TextView tx = (TextView) findViewById(R.id.textView4);
                    tx.setVisibility(View.INVISIBLE);


                }
        }
    }*/


     public String getPath(Uri uri)
     {
         String[] projection = { MediaStore.Images.Media.DATA };
         Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
         if (cursor == null) return null;
         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
         cursor.moveToFirst();
         String s=cursor.getString(column_index);
         cursor.close();
         return s;
     }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String realPath;
        if (resultCode == RESULT_OK) {
            try {

                final Uri imageUri = data.getData();
                filee = new File(getPath(imageUri));

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap selectedImage1 = BitmapFactory.decodeStream(imageStream);

                fajl = saveBitmap(selectedImage1,imageUri.getPath());
                buttonphoto = (ImageView) findViewById(R.id.slika);
                buttonphoto.setImageBitmap(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private File saveBitmap(final Bitmap bitmap, final String path) {
        File file = null;
        file = new File(path);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {


                if (bitmap != null) {

                    try {
                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
            return file;
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

                        if(updated){

                            Toast.makeText(ProfileActivity.this, "Profile succesfully updated.", Toast.LENGTH_SHORT).show();
                            updated = false;
                            finish();

                        }

                        else {

                            try {
                                JSONObject reader = new JSONObject(s);
                                String email = reader.getString("email");
                                userMail = email;
                                String username = reader.getString("name");
                                String firstName = reader.getString("first_name");
                                String lastName = reader.getString("last_name");
                                String phoneNumber = reader.getString("phone_number");
                                String urlImage = reader.getString("avatar");
                                firstname = (EditText) findViewById(R.id.firstnameT);
                                if (firstName != "null")
                                    firstname.setText(firstName);
                                else
                                    firstname.setText("");

                                lastname = (EditText) findViewById(R.id.lastnameT);
                                if (lastName != "null")
                                    lastname.setText(lastName);
                                else
                                    lastname.setText("");

                                phone = (EditText) findViewById(R.id.phoneT);
                                if (phoneNumber != "null")
                                    phone.setText(phoneNumber);
                                else
                                    phone.setText("");

                                if (urlImage != "null")
                                {
                                    new DownloadImageTask((ImageView) findViewById(R.id.slika))
                                            .execute("https://zmurke.herokuapp.com" + urlImage);

                                   /* b = Base64.decode(imgBase64, Base64.DEFAULT);
                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(b, 0, b.length);
                                    buttonphoto.setImageBitmap(decodedImage);*/

                                }


                                email1 = (EditText) findViewById(R.id.emailT);
                                email1.setText(email);
                                username1 = (EditText) findViewById(R.id.usernameT);
                                username1.setText(username);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });




            }
        }
    }





}
