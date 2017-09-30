package com.example.riki.myplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity implements IThreadWakeUp  {

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String apiKey;
    String idUser;
    boolean ok = false;
    boolean notOk = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        final Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");
        DownloadManager.getInstance().setThreadWakeUp(this);





        ListView friends = (ListView) findViewById(R.id.listViewFriends);
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {


                String val =(String) listItems.get(position);
                val.substring(0,val.indexOf(' ')); // "72"
                val = val.replaceAll("\\D+","");
              //  Toast.makeText(FriendsActivity.this, "RAAADI" , Toast.LENGTH_LONG).show();
               // int idf = Integer.parseInt(val);
                ok=true;

                DownloadManager.getInstance().getAnyUser(apiKey,val);


                //TODO: Create a new profile activity that is non-editable, for opening profiles of user's friends
                //Or better yet, make a pop-up window with this information
            }
        });

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        friends.setAdapter(adapter);

        DownloadManager.getInstance().getFriends(apiKey);
        DownloadManager.getInstance().getUser(apiKey);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddNewFriend);
        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
              //  logOut.startAnimation(animation);
                Intent intent = new Intent(FriendsActivity.this, Friendz.class);
                intent.putExtra("api",apiKey);
               //intent.putExtra("id",idUser);
                startActivity(intent);

            }
        });



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
        }
        else
        {
            /*String html = "<!DOCTYPE html>";
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
            else {*/
                try {

                    if(ok){


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {



                                String firstName = null;
                                try {

                                    JSONObject reader = new JSONObject(s);
                                    firstName = reader.getString("first_name");
                                    String lastName = reader.getString("last_name");
                                    String email = reader.getString("email");
                                    String points = reader.getString("points");
                                    String phoneNumber = reader.getString("phone_number");
                                    String urlImage = reader.getString("avatar");

                                    Intent intent = new Intent(FriendsActivity.this,FriendProfileActivity.class);
                                    intent.putExtra("api", apiKey);
                                    intent.putExtra("fname", firstName);
                                    intent.putExtra("lname", lastName);
                                    intent.putExtra("email", email);
                                    intent.putExtra("phone", phoneNumber);
                                    intent.putExtra("url", urlImage);
                                    intent.putExtra("points",points);
                                    startActivity(intent);
                                   // Toast.makeText(FriendsActivity.this, "RAAADI" , Toast.LENGTH_LONG).show();

                                    ok =false;



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });



                    }
                    else {
                        if(notOk) {
                            JSONObject reader = new JSONObject(s);
                            idUser = reader.getString("id");
                            notOk= false;
                        }

                        JSONArray friends = new JSONArray(s);
                        JSONObject[] elements = new JSONObject[friends.length()];
                        final String[] names = new String[friends.length()];
                        final String[] id = new String[friends.length()];
                        for (int i = 0; i < friends.length(); i++) {
                            elements[i] = friends.getJSONObject(i);
                            names[i] = elements[i].getString("name");
                            id[i] = elements[i].getString("id");
                            final int iterator = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //stuff that updates ui
                                    listItems.add("#" + id[iterator] + " " + names[iterator] );
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }

        }
    }
}
