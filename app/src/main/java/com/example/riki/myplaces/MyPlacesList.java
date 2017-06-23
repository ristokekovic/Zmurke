package com.example.riki.myplaces;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyPlacesList extends AppCompatActivity {

    static int NEW_PLACE = 1;

    ArrayList<String> places;

    Handler guiThread;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);
        guiThread = new Handler();
        context = this;
        progressDialog = new ProgressDialog(this);
        ListView myPlacesList = (ListView) findViewById(R.id.my_places_list);
        myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));
        myPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle positionBundle = new Bundle();
                positionBundle.putInt("position", position);
                Intent i = new Intent(MyPlacesList.this, ViewMyPlaceActivity.class);
                i.putExtras(positionBundle);
                startActivity(i);
                MyPlace place = (MyPlace) parent.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(), place.getName() + " selected", Toast.LENGTH_SHORT).show();
            }
        });

        myPlacesList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                MyPlace place = MyPlacesData.getInstance().getPlace(info.position);
                menu.setHeaderTitle(place.getName());
                menu.add(0, 1, 1, "View place");
                menu.add(0, 2, 2, "Edit place");
                menu.add(0, 3, 3, "Delete place");
                menu.add(0, 4, 4, "Show on map");
                menu.add(0, 5, 5, "Upload place");
            }

        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Bundle positionBundle = new Bundle();
        positionBundle.putInt("position", info.position);
        Intent i = null;
        if (item.getItemId() == 1) {
            i = new Intent(this, ViewMyPlaceActivity.class);
            i.putExtras(positionBundle);
            startActivity(i);
        } else if (item.getItemId() == 2) {
            i = new Intent(this, EditMyPlaceActivity.class);
            i.putExtras(positionBundle);
            startActivityForResult(i, 1);
        } else if (item.getItemId() == 3) {
            MyPlacesData.getInstance().deletePlace(info.position);
            setList();
        } else if(item.getItemId() == 4) {
            i = new Intent(this, MyPlacesMapActivity.class);
            i.putExtra("state", MyPlacesMapActivity.CENTER_PLACE_ON_MAP);
            MyPlace place = MyPlacesData.getInstance().getPlace(info.position);
            i.putExtra("lat", place.getLatitude());
            i.putExtra("lon", place.getLongitude());
            startActivityForResult(i, 2);
        }
        else if(item.getItemId() == 5){
            ExecutorService transThread = Executors.newSingleThreadExecutor();
            final int position = info.position;
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    MyPlace place = MyPlacesData.getInstance().getPlace(position);
                    guiStartProgressDialog("Sending place", "Sending" + place.getName());
                    try{
                        final String message = MyPlacesHTTPHelper.sendMyPlace(place);
                        guiNotifyUser(message);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    guiDismissProgressDialog();
                }
            });
        }
        return super.onContextItemSelected(item);
    }

    private void guiNotifyUser(final String message){
        guiThread.post(new Runnable(){
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guiStartProgressDialog(final String title, final String message){
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setTitle(title);
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void guiDismissProgressDialog(){
        guiThread.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    private void setList(){
        ListView myPlacesList = (ListView) findViewById(R.id.my_places_list);
        myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String msg = "";

        switch (id) {

            case R.id.show_map_title:
                msg = getString(R.string.show_map);
                Intent map = new Intent(this, MyPlacesMapActivity.class);
                map.putExtra("state", MyPlacesMapActivity.SHOW_MAP);
                startActivity(map);
                break;
            case R.id.new_place_item:
                msg = getString(R.string.new_place);
                Intent i = new Intent(this,EditMyPlaceActivity.class);
                startActivityForResult(i, NEW_PLACE);
                break;
            case R.id.server_list_item:
                Dialog v = new MyPlacesServerList(this);
                v.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        setList();
                    }
                });
                v.show();
                break;
            case R.id.my_places_list_item:
                msg = getString(R.string.my_places_list);
                Intent myPlaces = new Intent(this, MyPlacesList.class);
                startActivity(myPlaces);
                break;

            case R.id.about_item:
                msg = getString(R.string.about);
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;

        }

        //Toast.makeText(this, msg + " clicked !", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this, "New place added", Toast.LENGTH_LONG).show();
            ListView myPlacesList = (ListView) findViewById(R.id.my_places_list);
            myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));
        }
    }
}
