package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.databinding.ActivityHistoryBinding;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;
import com.groupx.quicknews.ui.articles.ArticlesViewAdapter;

import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {


    private List<Article> articles;
    private RecyclerView articleView;
    private final String TAG = "HistoryActivity";
    Context context = this;
    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActivityHistoryBinding binding;

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Viewed Articles");
        articleView = findViewById(R.id.view_article);

        getArticleHistory();
    }
    // ChatGPT usage: No.
    private void getArticleHistory() {
        // Make a get request to get the history of this user
        String getUrl = getString(R.string.server_dns) +"profile/"+ LoginActivity.getUserId() +"/history";
        HttpClient.getRequestWithJWT(getUrl, new HttpClient.ApiCallback() {
            // ChatGPT usage: No.
            @Override
            public void onResponse(Response response) {
                handleGetArticleResponse(response);
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void handleGetArticleResponse(Response response) {
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
            e.printStackTrace();
        }
    }
}