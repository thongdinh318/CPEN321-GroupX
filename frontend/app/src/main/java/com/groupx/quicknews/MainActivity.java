package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button forumButton  ;
    private Button articlesButton;
    final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forumButton = findViewById(R.id.forum_button);
        articlesButton = findViewById(R.id.article_button);

        articlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Trying to open articles view");
                Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);
                startActivity(articleIntent);
            }
        });

        forumButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Trying to open forum view");
                Intent forumIntent = new Intent(MainActivity.this, ForumActivity.class);
                startActivity(forumIntent);
            }
        });
    }
}