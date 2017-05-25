package ru.antsharapov.kobrastartstop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class AirportSelect extends AppCompatActivity {

    String pass, password;

    boolean exists = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_select);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ImageButton pref_btn = (ImageButton) findViewById(R.id.imageButton);
        pref_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AirportSelect.this);
                alertDialog.setTitle("Авторизация");
                alertDialog.setMessage("Введите пароль:");

                final EditText input = new EditText(AirportSelect.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final SharedPreferences preferences = getSharedPreferences("ru.antsharapov.kobrastartstop", Context.MODE_PRIVATE);
                                pass = preferences.getString("alert_pass", "");
                                if (pass.equals("")) pass="***";
                                password = input.getText().toString();
                                if (password.compareTo("") != 0) {
                                    if (pass.equals(password)) {
                                        Intent intent = new Intent(AirportSelect.this, SettingsActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Неверный пароль", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                alertDialog.setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
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

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SocketAddress sockaddr = new InetSocketAddress("10.121.0.75", 445);
                            Socket sock = new Socket();
                            int timeoutMs = 1000;
                            sock.connect(sockaddr, timeoutMs);
                            exists = true;
                        } catch(IOException e) {
                            Log.d("socket error:",e.toString());
                        }
                    }
                });
                thread.run();

                if (exists) {
                    String airport = adapterView.getItemAtPosition(i).toString();
                    Toast.makeText(getApplicationContext(), airport, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AirportSelect.this, MainActivity.class);
                    intent.putExtra("airport", airport);
                    startActivity(intent);
                    finish();
                }
                else Toast.makeText(getApplicationContext(),"Connect to inner wlan!",Toast.LENGTH_LONG).show();
            }
        });
    }
}


