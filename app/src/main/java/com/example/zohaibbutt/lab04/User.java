package com.example.zohaibbutt.lab04;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Struct;
import java.util.Objects;
import java.util.Random;


public class User extends AppCompatActivity {
    private static final CharSequence characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
    private static final int nameLength = 10;
    private EditText userName;
    private Button button;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setupUI();
        setRandomName();
        this.button.setOnClickListener(this::onClick);
    }

    private void setupUI(){
        this.userName = findViewById(R.id.ET1);
        this.button = findViewById(R.id.B1);
    }

    private void setRandomName(){
        userName.setText(randomName(nameLength));
    }

    private void onClick(View view) {
        Intent i = new Intent();
        i.putExtra("Anonymous_User", userName.getText().toString());

        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        super.onBackPressed();
    }

    private String randomName(int stringLength){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return stringBuilder.toString();
    }

}
