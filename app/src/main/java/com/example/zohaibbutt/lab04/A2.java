package com.example.zohaibbutt.lab04;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class A2 extends AppCompatActivity {
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    private ListView usersListView;
    private ArrayList<String> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a2);

        setupUI();
        getAllUsersFromDB();
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), UserMessages.class);
                i.putExtra("UserName", usersListView.getItemAtPosition(position).toString());
                startActivity(i);
            }
        });
    }

    private void setupUI(){
        usersListView = findViewById(R.id.LW1);
        users = new ArrayList<>();
    }

    private void updateListView(){
        Log.i("TEST", "updateListView Runs!");
        Collections.sort(users, String.CASE_INSENSITIVE_ORDER);     // might not need to sort since DB does it automatically
        ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        usersListView.setAdapter(adapter);
    }

    private void getAllUsersFromDB(){
       DatabaseReference userChild =  root.child("User");
        userChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    users.add(String.valueOf(dss.getKey()));
                    Log.i("TEST", "onDataChange Runs!");
                }

                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
