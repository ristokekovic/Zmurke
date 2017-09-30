package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class RankingActivity extends AppCompatActivity implements IThreadWakeUp {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listNum = new ArrayList<String>();
    ArrayAdapter<String> adapter ,adapterHiden;
    String apiKey;
    String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        final Intent intent = getIntent();
        apiKey = intent.getExtras().getString("api");
        idUser = intent.getExtras().getString("id");
        listNum.add("hihi");

        DownloadManager.getInstance().setThreadWakeUp(this);

        //ListView number = (ListView) findViewById(R.id.rankListid);
        ListView friends = (ListView) findViewById(R.id.rankList);
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {


                String val =(String) listItems.get(position);
                val.substring(0,val.indexOf(' ')); // "72"
                val = val.replaceAll("\\D+","");


               // DownloadManager.getInstance().getAnyUser(apiKey,val);


                //TODO: Create a new profile activity that is non-editable, for opening profiles of user's friends
                //Or better yet, make a pop-up window with this information
            }
        });

        adapterHiden = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, listNum);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);

     /*   Collections.sort(listItems);
        Collections.reverse(listItems);*/

        friends.setAdapter(adapter);
       // number.setAdapter(adapterHiden);

        DownloadManager.getInstance().getFriends(apiKey);

      /*  final Button fab = (Button) findViewById(R.id.button5);
        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //  logOut.startAnimation(animation);
                Intent intent = new Intent(RankingActivity.this, HelpActivity.class);
                //intent.putExtra("id",idUser);
                startActivity(intent);

            }
        });
*/
    }


    @Override
    public void ResponseOk(final String s) {


        if (s.isEmpty()) {
            //nije dobio podatke, treba uraditi nesto
            //treba probati jos jednom da se pribave podaci, ako je doslo do greske, ponovo se poziva DownloadManager.getData
            //ako nije ni tada, onda treba nekako obezbediti da ne pukne aplikacija
            //ispisati poruku da je doslo do greske na serveru, to samo ako 2 puta ne dobijemo nista
            //promenljiva koja to obezbedjuje
        } else {
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




                    final JSONArray friends = new JSONArray(s);
                    JSONObject[] elements = new JSONObject[friends.length()];
                    final String[] names = new String[friends.length()];
                    final String[] id = new String[friends.length()];
                    final String[] points = new String[friends.length()];

                    for (int i = 0; i < friends.length(); i++) {
                        elements[i] = friends.getJSONObject(i);
                        names[i] = elements[i].getString("name");
                        id[i] = elements[i].getString("id");
                        points[i] = elements[i].getString("points");
                        final int iterator = i;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //stuff that updates ui
                              //  if(Integer.parseInt(points[iterator]) > Integer.parseInt(points[iterator+1]))

                                listItems.add( points[iterator]+ " points "+ "                      :                       " + names[iterator]);
                                //listNum.add(String.valueOf(iterator));

                                Collections.sort(listItems);
                                Collections.reverse(listItems);

                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


}
