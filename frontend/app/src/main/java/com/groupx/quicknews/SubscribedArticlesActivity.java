package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.groupx.quicknews.databinding.ActivityArticlesBinding;
import com.groupx.quicknews.databinding.ActivitySubscribedArticlesBinding;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;
import com.groupx.quicknews.ui.articles.ArticlesViewAdapter;

import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public class SubscribedArticlesActivity extends AppCompatActivity {

    private List<Article> articles;
    private RecyclerView articleView;
    private Context context = this;

    final static String TAG = "SubscribedActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySubscribedArticlesBinding binding;
        binding = ActivitySubscribedArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        articleView = findViewById(R.id.view_article);

        initNavigationBar();
        getSubscribedArticles();


    }
    @Override
    protected void onStart() {
        super.onStart();
        getSubscribedArticles();
    }

    private void initNavigationBar() {
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_subscribed);
        bottomNavigationView.findViewById(R.id.action_subscribed).setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.action_home) {
                Log.d(TAG, "Trying to open main view");
                Intent mainIntent = new Intent(SubscribedArticlesActivity.this, MainActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(0, 0);
                return true;
            }
            else if (itemID == R.id.action_forums) {
                Log.d(TAG, "Trying to open forum view");
                Intent forumIntent = new Intent(SubscribedArticlesActivity.this, ForumsListActivity.class);
                startActivity(forumIntent);
                overridePendingTransition(0, 0);
                return true;
            }
            else if (itemID == R.id.action_subscribed) {
                return true;
            }
            return false;
        });
    }

    public void getSubscribedArticles() {
        String getUrl = getString(R.string.server_dns) +"article/subscribed/"+ LoginActivity.getUserId();
        HttpClient.getRequestWithJWT(getUrl, new HttpClient.ApiCallback() {
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
                                Log.d(TAG, articles.toString());
                                //articleView.setLayoutManager(new LinearLayoutManager(SubscribedArticlesActivity.this));
                                //articleView.setAdapter(new ArticlesViewAdapter(context, articles));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
    // ChatGPT usage: No.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_articles, menu);
        return true;
    }

    // ChatGPT usage: No.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        int itemId = item.getItemId();
        if( itemId == R.id.action_manage_subscriptions ) {
            // Code to be executed when the add button is clicked
            Intent intent = new Intent(SubscribedArticlesActivity.this, SubscriptionActivity.class);
            startActivity(intent);
            return true;
        }
        else if( itemId == R.id.action_view_history ) {
            // Code to be executed when the add button is clicked
            Intent intent = new Intent(SubscribedArticlesActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}