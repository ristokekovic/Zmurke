package com.example.riki.myplaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity implements IThreadWakeUp  {

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        ListView friends = (ListView) findViewById(R.id.listViewFriends);
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                //TODO: Create a new profile activity that is non-editable, for opening profiles of user's friends
                //Or better yet, make a pop-up window with this information
            }
        });
        DownloadManager.getInstance().setThreadWakeUp(this);

        Intent intent = getIntent();
        final String apiKey = intent.getExtras().getString("api");

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        friends.setAdapter(adapter);

        DownloadManager.getInstance().getFriends(apiKey);
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
                    JSONArray friends = new JSONArray(s);
                    JSONObject[] elements = new JSONObject[friends.length()];
                    final String[] names = new String[friends.length()];
                    for(int i = 0; i < friends.length(); i++)
                    {
                        elements[i] = friends.getJSONObject(i);
                        names[i] = elements[i].getString("name");
                        final int iterator = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //stuff that updates ui
                                listItems.add(names[iterator]);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                }

        }
    }
}
