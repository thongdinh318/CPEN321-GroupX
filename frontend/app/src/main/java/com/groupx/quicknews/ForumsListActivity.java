package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.databinding.ActivityForumsListBinding;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.forumlist.Forum;
import com.groupx.quicknews.ui.forumlist.ForumsViewAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public class ForumsListActivity extends AppCompatActivity {


    private RecyclerView viewForum;
    private List<Forum> forums;
    final static String TAG = "ForumsListActivity";
    
    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_list);

        ActivityForumsListBinding binding;

        binding = ActivityForumsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        viewForum = findViewById(R.id.view_forum);
        getForums();
    }

    // ChatGPT usage: No.
    private void getForums () {
        String url = getString(R.string.server_dns) + "forums";
        try {
            HttpClient.getRequest(url, new HttpClient.ApiCallback(){
                @Override
                public void onResponse(Response response) throws IOException {
                    int statusCode = response.code();
                    if (statusCode == 200){
                        String responseBody = response.body().string();
                        Log.d(TAG, responseBody);
                        //update forums list
                        ObjectMapper mapper = new ObjectMapper();
                        forums = Arrays.asList(mapper.readValue(responseBody, Forum[].class));
                        runOnUiThread(new Runnable() {
                            // ChatGPT usage: No.
                            @Override
                            public void run() {
                                viewForum.setLayoutManager(new LinearLayoutManager(ForumsListActivity.this));
                                viewForum.setAdapter(new ForumsViewAdapter(getApplicationContext(), forums));
                            }
                        });
                    }
                }
                // ChatGPT usage: No.
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "exception", e);
                }
            });
        }
        catch(Exception e) {
            Log.e(TAG, "exception", e);
        }
    }
}