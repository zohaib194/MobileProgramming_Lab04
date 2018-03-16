package com.example.zohaibbutt.lab04;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity {
    private static final String PREF_TAG_REFERESHTIME = "TAG_REFRESH_TIME";
    private TextView T1;
    private Button B1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupUI();
        this.B1.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        SharedPreferences preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(PREF_TAG_REFERESHTIME, Integer.parseInt(T1.getText().toString()));
        Log.i("Refresh_Time", ""+T1.getText().toString());
        editor.apply();
        finish();
    }

    private void setupUI(){
        T1 = findViewById(R.id.T1);
        B1 = findViewById(R.id.B1);
    }


}
