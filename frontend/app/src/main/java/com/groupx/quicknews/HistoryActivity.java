package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        int userId;
        Bundle extras = getIntent().getExtras();

        if (extras != null){
            userId = extras.getInt("USER_ID");
        }
        // Make a get request to get the history of this user
    }
}