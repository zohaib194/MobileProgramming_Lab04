package com.example.zohaibbutt.lab04;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class A1 extends AppCompatActivity {
    public static final int GET_RESULT = 1;
    private static final String PREF_TAG_USER = "TAG_USERNAME";
    private ListView messageList;
    private Button sendButton;
    private EditText message;
    public ArrayList<String> msgList;
    public ArrayList<String> dateList;
    private ArrayAdapter adapter;
    private String userName;
    private SharedPreferences preferences;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private Receiver receiver = new Receiver();
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a1);

        this.preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        this.userName = preferences.getString(PREF_TAG_USER, null);

        // if username name is null then start the activity to set username
        if(userName == null){
            Intent i = new Intent(this, User.class);
            startActivityForResult(i, GET_RESULT);
        }

        setupUI();
        onDBUpdate();
        this.sendButton.setOnClickListener(this::onClick);

    }

    // setup UI elements
    private void setupUI(){
        this.messageList = findViewById(R.id.LW1);
        this.sendButton = findViewById(R.id.B1);
        this.message = findViewById(R.id.T1);

        this.msgList = new ArrayList<>();
        this.dateList = new ArrayList<>();
        this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1 , msgList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView t1 = view.findViewById(android.R.id.text1);
                TextView t2 = view.findViewById(android.R.id.text2);

                t1.setText(msgList.get(position));
                t2.setText(dateList.get(position));

                return view;
            }
        };
        this.messageList.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // app is in background so starting broadcast receiver
        setPendingIntent();
        startBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // app is in foreground canceling alarm manager to stop broadcast receiver
        if(this.pendingIntent != null) {
            this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(this.pendingIntent);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alarmManager != null){
            alarmManager.cancel(this.pendingIntent);
        }
    }

    // on click for button
    private void onClick(View view) {
        if(message != null && !message.getText().toString().equalsIgnoreCase("")){
            String m = message.getText().toString();
            Calendar cal = new GregorianCalendar();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String d = dateFormat.format(cal.getTime());
            String u = userName;
            Map<String, Object> map = new HashMap<>();
            map.put("u", u);
            map.put("m", m);
            map.put("d", d);
            root.child("Message").push().updateChildren(map);
            message.setText("");
            onDBUpdate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != GET_RESULT) {
            return;
        }
        if (resultCode != RESULT_OK) {
            finish();
        } else {
            this.userName = data.getStringExtra("Anonymous_User");
            this.preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_TAG_USER, userName);
            editor.apply();

            Map<String, Object> map = new HashMap<>();
            map.put(userName, "");
            this.root.child("User").updateChildren(map);

        }
    }
    // goes to A2 for user list view
    public void goToA2(View view){
        Intent i = new Intent(this, A2.class);
        startActivity(i);
    }

    // goes to Setting activity for refresh time
    public void goToSettings(View view){
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    private void onDBUpdate(){
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateListView(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateListView(dataSnapshot);
            }
            /* Not implemented yet */

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateListView(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getKey().equalsIgnoreCase("Message")) {
            msgList.clear();
            String userName = null, message = null, date = null;
            for (DataSnapshot dss : dataSnapshot.getChildren()) {
                for (DataSnapshot dss2 : dss.getChildren()) {
                    if (dss2.getKey().equalsIgnoreCase("m")) {
                        message = (String) dss2.getValue();
                    } else if (dss2.getKey().equalsIgnoreCase("u")){
                        userName = (String) dss2.getValue();
                    } else {
                        date = (String) dss2.getValue();
                    }
                }
                msgList.add(userName + ": " + message);
                dateList.add(date);
            }
            adapter.notifyDataSetChanged();
            messageList.setSelection(msgList.size());
        }
    }


    private void startBroadcastReceiver(){
        SharedPreferences preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        int refreshTime = preferences.getInt("Refresh_Time", 5);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("OldMessageListSize", msgList.size());
        editor.apply();

        this.alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500,
                1000 * 60 * refreshTime, this.pendingIntent);

    }

    private void setPendingIntent() {
        Intent i = new Intent(this, this.receiver.getClass());
        this.pendingIntent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
