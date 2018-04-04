package com.example.zohaibbutt.lab04;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserMessages extends AppCompatActivity {

    private ListView listView;
    private String userName;
    private ArrayList<String> messageList;
    private ArrayList<String> dateList;
    private ArrayList<String> userList;
    private ArrayAdapter arrayAdapter;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_messages);

        setupUI();
        messagesListOfUser();
    }

    private void setupUI(){
        this.listView = findViewById(R.id.LV1);
        this.messageList = new ArrayList<>();
        this.dateList = new ArrayList<>();
        this.userList = new ArrayList<>();
        this.arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1 , messageList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView t1 = view.findViewById(android.R.id.text1);
                TextView t2 = view.findViewById(android.R.id.text2);

                messageListOfSpecificUser();

                t1.setText(userName + ": " + messageList.get(position));
                t2.setText(dateList.get(position));

                return view;
            }
        };
        this.listView.setAdapter(arrayAdapter);
    }

    private void messageListOfSpecificUser(){
        for (int i = 0; i < messageList.size(); i++){
            if(!userList.get(i).equals(userName)){
                userList.remove(i);
                dateList.remove(i);
                messageList.remove(i);
            }
        }
        arrayAdapter.notifyDataSetChanged();
        listView.setSelection(messageList.size());
    }

    private void messagesListOfUser(){
        getIntentExtra();
        root.child("Message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("u").getValue().toString().equals(userName)) {
                        for(DataSnapshot dss : ds.getChildren()) {
                            if (dss.getKey().equals("u")) {
                                userList.add(dss.getValue().toString());
                            }
                            if (dss.getKey().equals("m")) {
                                messageList.add(dss.getValue().toString());
                            }

                            if (dss.getKey().equals("d")) {
                                dateList.add(dss.getValue().toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getIntentExtra(){
        String result[] = getIntent().getStringExtra("UserName").split(":");
        userName = result[0];
        Log.i("UserName", userName);
    }
}
