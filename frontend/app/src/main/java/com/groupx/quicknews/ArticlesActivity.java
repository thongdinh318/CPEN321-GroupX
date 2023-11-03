package com.groupx.quicknews;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.databinding.ActivityArticlesBinding;
import com.groupx.quicknews.ui.articles.Article;
import com.groupx.quicknews.ui.articles.ArticlesViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArticlesActivity extends AppCompatActivity {

    private ActivityArticlesBinding binding;
    private RecyclerView articleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticlesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        //CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        //toolBarLayout.setTitle(getTitle());
        List<Article> articlesList = MainActivity.getArticleList();
        articleView = findViewById(R.id.view_article);
        articleView.setLayoutManager(new LinearLayoutManager(this));
        articleView.setAdapter(new ArticlesViewAdapter(getApplicationContext(), articlesList));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_articles, menu);

        return true;
    }

    //TODO: get articles from server oncreate
    //TODO: need someway to refresh articles. possibly on pulldown
    //TODO: get recommended articles and create new adapter when selected
}