package ru.antsharapov.kobrastartstop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences preferences = this.getSharedPreferences("ru.antsharapov.kobrastartstop", Context.MODE_PRIVATE);
        String aaq_mail = preferences.getString("aaq_mail", "");
        String aer_mail = preferences.getString("aer_mail", "");
        String gdz_mail = preferences.getString("gdz_mail", "");
        String krr_mail = preferences.getString("krr_mail", "");
        String from_mail = preferences.getString("from_mail", "");
        String from_pass = preferences.getString("from_pass", "");
        String lock_min = preferences.getString("lock_min", "");
        String smb_pass = preferences.getString("smb_pass", "");
        String alert_pass = preferences.getString("alert_pass", "");

        if (aaq_mail.equals("")) aaq_mail="SharapovAV@aaq.basel.aero";
        if (aer_mail.equals("")) aer_mail="SharapovAV@aaq.basel.aero";
        if (gdz_mail.equals("")) gdz_mail="SharapovAV@aaq.basel.aero";
        if (krr_mail.equals("")) krr_mail="SharapovAV@aaq.basel.aero";
        if (from_mail.equals("")) from_mail="app.mail.relay@gmail.com";
        if (from_pass.equals("")) from_pass="***";
        if (lock_min.equals("")) lock_min="3";
        if (smb_pass.equals("")) smb_pass="***";
        if (alert_pass.equals("")) alert_pass="***";

        final EditText aaq_edit = (EditText) findViewById(R.id.aaq_editmail);
        final EditText aer_edit = (EditText) findViewById(R.id.aer_editmail);
        final EditText gdz_edit = (EditText) findViewById(R.id.gdz_editmail);
        final EditText krr_edit = (EditText) findViewById(R.id.krr_editmail);
        final EditText from_edit = (EditText) findViewById(R.id.sendfrom_edit);
        final EditText pass_edit = (EditText) findViewById(R.id.pass_edit);
        final EditText lock_edit = (EditText) findViewById(R.id.lock_edit);
        final EditText smb_edit = (EditText) findViewById(R.id.smb_pass);
        final EditText alert_edit = (EditText) findViewById(R.id.editText);

        aaq_edit.setText(aaq_mail);
        aer_edit.setText(aer_mail);
        gdz_edit.setText(gdz_mail);
        krr_edit.setText(krr_mail);
        from_edit.setText(from_mail);
        pass_edit.setText(from_pass);
        lock_edit.setText(lock_min);
        smb_edit.setText(smb_pass);
        alert_edit.setText(alert_pass);

        Button save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putString("aaq_mail", aaq_edit.getText().toString()).apply();
                preferences.edit().putString("aer_mail", aer_edit.getText().toString()).apply();
                preferences.edit().putString("gdz_mail", gdz_edit.getText().toString()).apply();
                preferences.edit().putString("krr_mail", krr_edit.getText().toString()).apply();
                preferences.edit().putString("from_mail", from_edit.getText().toString()).apply();
                preferences.edit().putString("from_pass", pass_edit.getText().toString()).apply();
                preferences.edit().putString("lock_min", lock_edit.getText().toString()).apply();
                preferences.edit().putString("smb_pass", smb_edit.getText().toString()).apply();
                preferences.edit().putString("alert_pass", alert_edit.getText().toString()).apply();
                finish();
            }
        });

    }
}
