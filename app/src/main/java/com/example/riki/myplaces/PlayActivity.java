package com.example.riki.myplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {
    ListView myList;
    private Spinner spinner;
    String time;

    String[] listContent = {

            "Paja",

            "Gaja",

            "Vlaja",

            "Ringeraja",

            "Jaja",

            "Buc",

            "Deca",

            "Cuc",

            "Pasito",

            "Moj"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar1);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        addListenerOnSpinnerItemSelection();
        addListenerOnButton();

        final ImageView v3 = (ImageView) findViewById(R.id.cancelButton);
        v3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                v3.startAnimation(animation);
                finish();
            }
        });

        myList = (ListView)findViewById(R.id.list);

        ArrayAdapter<String> adapter

                = new ArrayAdapter<String>(this,

                android.R.layout.simple_list_item_multiple_choice,

                listContent);

        myList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        myList.setAdapter(adapter);

    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.spinner);
        //final Button btnSubmit = (Button) findViewById(R.id.buttonStart);
        final ImageView play = (ImageView) findViewById(R.id.playButton);

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                play.startAnimation(animation);
                time = String.valueOf(spinner.getSelectedItem());
               // btnSubmit.setText(time);


                String selected = "";


                int cntChoice = myList.getCount();

                SparseBooleanArray sparseBooleanArray = myList.getCheckedItemPositions();

                for(int i = 0; i < cntChoice; i++){

                    if(sparseBooleanArray.get(i)) {

                        selected += myList.getItemAtPosition(i).toString() + "\n";


                    }

                }


                Toast.makeText(PlayActivity.this,

                        selected,

                        Toast.LENGTH_LONG).show();

            }

        });
    }




}
