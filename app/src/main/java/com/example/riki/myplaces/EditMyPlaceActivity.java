package com.example.riki.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditMyPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    boolean editMode = true;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_place);
        try
        {
            Intent listIntent = getIntent();
            Bundle positionBundle = listIntent.getExtras();
            if(positionBundle != null)
                position = positionBundle.getInt("position");
            else
                editMode = false;
        } catch (Exception e){
            editMode = false;
        }
        final Button finishedButton = (Button) findViewById(R.id.editmyplace_finished_button);
        Button cancelButton = (Button) findViewById(R.id.editmyplace_cancel_button);
        EditText nameEditText = (EditText) findViewById(R.id.editmyplace_name_edit);
        if(!editMode){
            finishedButton.setEnabled(false);
            finishedButton.setText("Add");
        } else if(position >= 0){
            finishedButton.setText("Save");
            MyPlace place = MyPlacesData.getInstance().getPlace(position);
            nameEditText.setText(place.getName());
            EditText descEditText = (EditText) findViewById(R.id.editmyplace_desc_edit);
            descEditText.setText(place.getDesc());
        }
        finishedButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                finishedButton.setEnabled(s.length() > 0);
            }
        });

        Button locationButton = (Button) findViewById(R.id.editmyplace_location_button);
        locationButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.editmyplace_finished_button:
                EditText etName = (EditText) findViewById(R.id.editmyplace_name_edit);
                String nme = etName.getText().toString();
                EditText etDesc = (EditText) findViewById(R.id.editmyplace_desc_edit);
                String desc = etDesc.getText().toString();
                EditText latEdit = (EditText) findViewById(R.id.editmyplace_lat_edit);
                String lat = latEdit.getText().toString();
                EditText lonEdit = (EditText) findViewById(R.id.editmyplace_lon_edit);
                String lon = lonEdit.getText().toString();
                if(!editMode){
                    MyPlace place = new MyPlace(nme, desc);
                    place.setLatitude(lat);
                    place.setLongitude(lon);
                    MyPlacesData.getInstance().addNewPlace(place);
                } else {
                    MyPlace place = MyPlacesData.getInstance().getPlace(position);
                    place.setName(nme);
                    place.setDesc(desc);
                    place.setLatitude(lat);
                    place.setLongitude(lon);
                    MyPlacesData.getInstance().updatePlace(place);
                }
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case R.id.editmyplace_cancel_button:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            case R.id.editmyplace_location_button:
                Intent i = new Intent(this, MyPlacesMapActivity.class);
                i.putExtra("state", MyPlacesMapActivity.SELECT_COORDINATES);
                startActivityForResult(i,1);
                break;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                String lon = data.getExtras().getString("lon");
                EditText lonText = (EditText) findViewById(R.id.editmyplace_lon_edit);
                lonText.setText(lon);
                String lat = data.getExtras().getString("lat");
                EditText latText = (EditText) findViewById(R.id.editmyplace_lat_edit);
                latText.setText(lat);
            }

        }
        catch (Exception e) {
            //TODO: handle exception
        }
    }

}
