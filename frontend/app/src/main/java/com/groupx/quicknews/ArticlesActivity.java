package com.groupx.quicknews;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.databinding.ActivityArticlesBinding;
import com.groupx.quicknews.ui.articles.Article;
import com.groupx.quicknews.ui.articles.ArticlesViewAdapter;

import java.util.List;

public class ArticlesActivity extends AppCompatActivity {



    
    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView articleView;
        ActivityArticlesBinding binding;
        super.onCreate(savedInstanceState);
        binding = ActivityArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        List<Article> articlesList = MainActivity.getArticleList();
        articleView = findViewById(R.id.view_article);
        articleView.setLayoutManager(new LinearLayoutManager(this));
        articleView.setAdapter(new ArticlesViewAdapter(getApplicationContext(), articlesList));

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
            Intent intent = new Intent(ArticlesActivity.this, SubscriptionActivity.class);
            startActivity(intent);
            return true;
        }
        else if( itemId == R.id.action_view_history ) {
            // Code to be executed when the add button is clicked
            Intent intent = new Intent(ArticlesActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: need someway to refresh articles. possibly on pulldown
}