package com.example.riki.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static int NEW_PLACE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            //    Intent map = new Intent(this, MyPlacesMapActivity.class);
            //    map.putExtra("state", MyPlacesMapActivity.SHOW_MAP);
            //    startActivity(map);

               Intent map = new Intent(this, Main2Activity.class);
             //   map.putExtra("state", MyPlacesMapActivity.SHOW_MAP);
                startActivity(map);
                break;

            case R.id.new_place_item:
                msg = getString(R.string.new_place);
              //  Intent place = new Intent(this,EditMyPlaceActivity.class);
              //  startActivityForResult(place, NEW_PLACE);
                Intent place = new Intent(this,RegistrationActivity.class);
                startActivityForResult(place, NEW_PLACE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            Toast.makeText(this, "New place added", Toast.LENGTH_LONG).show();
        }
    }
}
