package ru.antsharapov.kobrastartstop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AirportSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_select);

        ImageButton pref_btn = (ImageButton) findViewById(R.id.imageButton);
        pref_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AirportSelect.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<String> list = new ArrayList<>();
        list.add("AAQ");
        list.add("AER");
        list.add("GDZ");
        list.add("KRR");
        ListView listView = (ListView) findViewById(R.id.lv);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AirportSelect.this,
                R.layout.lv_layout, list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String airport = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(),airport,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AirportSelect.this,MainActivity.class);
                intent.putExtra("airport",airport);
                startActivity(intent);
                finish();
            }
        });
    }
}
