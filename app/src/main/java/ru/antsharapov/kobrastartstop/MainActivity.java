package ru.antsharapov.kobrastartstop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    private static final String FORMAT = "%02d:%02d:%02d";
    final SharedPreferences preferences = this.getSharedPreferences("ru.antsharapov.kobrastartstop", Context.MODE_PRIVATE);
    Spinner sp;
    ArrayList<String> list = new ArrayList<>();
    String first, airport;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
           airport  = extras.getString("airport");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sp = (Spinner) findViewById(R.id.spinner);
        final TextView text1 = (TextView) findViewById(R.id.textView1);
        final Button first_btn = (Button) findViewById(R.id.button2);
        final Button last_btn = (Button) findViewById(R.id.button);
        final Button ref_btn = (Button) findViewById(R.id.button3);
        final Button exit_btn = (Button) findViewById(R.id.button4);
        last_btn.setEnabled(false);

        first_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop_timer();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
                first = sdf.format(new Date());
                sp.setEnabled(false);
                first_btn.setEnabled(false);
                ref_btn.setEnabled(false);
                exit_btn.setEnabled(false);
                SharedPreferences preferences = getSharedPreferences("ru.antsharapov.kobrastartstop", Context.MODE_PRIVATE);
                String lock_min = preferences.getString("lock_min", "");
                int i = Integer.parseInt(lock_min);
                new CountDownTimer(i * 60 * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        text1.setText("" + String.format(FORMAT,
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    }

                    public void onFinish() {
                        text1.setText("Last button unlocked!");
                        last_btn.setEnabled(true);
                    }
                }.start();
            }
        });

        last_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_timer();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
                final String last = sdf.format(new Date());
                sp.setEnabled(true);
                first_btn.setEnabled(true);
                last_btn.setEnabled(false);
                ref_btn.setEnabled(true);
                exit_btn.setEnabled(true);
                text1.setText("none");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendEmail(sp.getSelectedItem().toString() + ":\nПервая сумка в " + first + ",\nпоследняя сумка в " + last);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                readSMB();
            }
        });

        ref_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readSMB();
            }
        });
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop_timer();
                finish();
            }
        });
        readSMB();

        start_timer();

    }

    private StringBuilder readFileContent(SmbFile sFile, StringBuilder builder) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(sFile)));
        } catch (SmbException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        String lineReader = null;
        {
            try {
                while ((lineReader = reader.readLine()) != null) {
                    builder.append(lineReader).append("\n");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return builder;
    }

    protected void sendEmail(String sending_text) {
        final String username = preferences.getString("from_mail", "");
        final String password = preferences.getString("from_pass", "");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            String aaq_mail = preferences.getString("aaq_mail", "");
            String aer_mail = preferences.getString("aer_mail", "");
            String gdz_mail = preferences.getString("gdz_mail", "");
            String krr_mail = preferences.getString("krr_mail", "");
            if (aaq_mail.equals("")) aaq_mail="SharapovAV@aaq.basel.aero";
            if (aer_mail.equals("")) aer_mail="SharapovAV@aaq.basel.aero";
            if (gdz_mail.equals("")) gdz_mail="SharapovAV@aaq.basel.aero";
            if (krr_mail.equals("")) krr_mail="SharapovAV@aaq.basel.aero";
            switch (airport) {
                case "AAQ":
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(aaq_mail));
                    break;
                case "AER":
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(aer_mail));
                    break;
                case "GDZ":
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(gdz_mail));
                    break;
                case "KRR":
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(krr_mail));
                    break;
                default:
                    break;
            }
            message.setSubject("Baggage");
            message.setText(sending_text);
            Transport.send(message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"E-mail sent successfully",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void readSMB() {
                jcifs.Config.registerSmbURLHandler();
                jcifs.Config.setProperty("jcifs.encoding", "CP1251");
                jcifs.Config.setProperty("jcifs.smb.client.useUnicode", "false");

                try {
                    String user = "10.121.0.75\\olr";
                    String pass = preferences.getString("smb_pass", "");
                    String sharedFolder = "olr/NEW/";
                    String url = "smb://10.121.0.75/" + sharedFolder + airport + "/Arrival.xml";
                    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, pass);
                    SmbFile sfile = new SmbFile(url, auth);
                    sfile.connect();
                    StringBuilder builder;
                    builder = new StringBuilder();
                    builder = readFileContent(sfile, builder);
                    String converted_str = "";
                    try {
                        converted_str = new String(builder.toString().getBytes("CP1251"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                    list.clear();
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    String flight = "", plan = "", status = "";
                    xpp.setInput(new StringReader(converted_str));
                    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xpp.getEventType() == XmlPullParser.START_TAG
                                && xpp.getName().equals("en_flight_number")) {
                            flight = xpp.nextText();
                        }
                        else if (xpp.getEventType() == XmlPullParser.START_TAG
                                    && xpp.getName().equals("en_status")) {
                                status = xpp.nextText();
                        }
                        else if (xpp.getEventType() == XmlPullParser.START_TAG
                                && xpp.getName().equals("fact")) {
                            plan = xpp.nextText();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
                            String today = sdf.format(new Date());
                            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:");
                            String hour = sdf1.format(new Date());
                            String last_hour = sdf1.format(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)));
                            if (status.equals("LANDED") && plan.contains(today) && ( plan.contains(hour) || plan.contains(last_hour)))
                                list.add(airport + " " + flight + " " + plan + " " + status);
                        }
                        xpp.next();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, list);
                    sp.setAdapter(adapter);
                    Toast.makeText(getApplicationContext(),"Daily flight plan updated",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

            public void start_timer (){
                this.timer = new Timer();
                this.timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                readSMB();
                            }
                        });
                    }
                },0,30000);
            }

            public void stop_timer(){
                this.timer.cancel();
            }
}
