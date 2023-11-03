package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.databinding.ActivityForumBinding;
import com.groupx.quicknews.databinding.ActivityHistoryBinding;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;
import com.groupx.quicknews.ui.articles.ArticlesViewAdapter;
import com.groupx.quicknews.ui.forumlist.Forum;
import com.groupx.quicknews.ui.forumlist.ForumsViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private String userId;
    private List<Article> articles;
    private RecyclerView articleView;
    private final String TAG = "HistoryActivity";
    Context context = this;
    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Viewing History");
        articleView = findViewById(R.id.view_article);

        getArticleHistory();
    }
    // ChatGPT usage: No.
    private void getArticleHistory() {
        // Make a get request to get the history of this user
        String getUrl = getString(R.string.server_dns) +"profile/"+ LoginActivity.getUserId() +"/history";
        HttpClient.getRequest(getUrl, new HttpClient.ApiCallback() {
            // ChatGPT usage: No.
            @Override
            public void onResponse(Response response) {
                try {
                    Log.d(TAG, response.toString());
                    if (response.code() == 200) {
                        String responseBody = response.body().string();
                        Log.d(TAG, responseBody);
                        //update forums list
                        ObjectMapper mapper = new ObjectMapper();
                        articles = Arrays.asList(mapper.readValue(responseBody, Article[].class));
                        runOnUiThread(new Runnable() {
                            // ChatGPT usage: No.
                            @Override
                            public void run() {
                                articleView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                                articleView.setAdapter(new ArticlesViewAdapter(context, articles));
                            }
                        });
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}