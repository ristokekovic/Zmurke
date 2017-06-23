package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMyPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_place);
        int position = -1;
        try
        {
            Intent listIntent = getIntent();
            Bundle positionBundle = listIntent.getExtras();
            position = positionBundle.getInt("position");
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
        if(position >= 0)
        {
            MyPlace place = MyPlacesData.getInstance().getPlace(position);
            TextView twName = (TextView) findViewById(R.id.viewmyplace_name_text);
            twName.setText(place.getName());
            TextView twDesc = (TextView) findViewById(R.id.viewmyplace_desc_text);
            twDesc.setText(place.getDesc());
            TextView twLon = (TextView) findViewById(R.id.viewmyplace_lon_text);
            twLon.setText(place.getLongitude());
            TextView twLat = (TextView) findViewById(R.id.viewmyplace_lat_text);
            twLat.setText(place.getLatitude());
        }
        final Button finishedButton = (Button) findViewById(R.id.viewmyplace_finished_button);
        finishedButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_my_place, menu);
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

        Toast.makeText(this, msg + " clicked !", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
}
