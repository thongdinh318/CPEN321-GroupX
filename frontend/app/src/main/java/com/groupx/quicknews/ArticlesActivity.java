package com.groupx.quicknews;

import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import com.groupx.quicknews.databinding.ActivityArticlesBinding;

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

        List<Article> palceholderArticles = new ArrayList<Article>();
        palceholderArticles.add(new Article("Article1", "https://www.cbc.ca/player/play/2277156419849", "Lorem Ipsum"));
        palceholderArticles.add(new Article("Article2", "https://www.cbc.ca/news/world/israel-ground-raid-gaza-city-us-strikes-syria-un-vote-eu-members-ceasefire-1.7010037", "Lorem Ipsum"));

        articleView = findViewById(R.id.article_view);
        articleView.setLayoutManager(new LinearLayoutManager(this));
        articleView.setAdapter(new ArticlesViewAdapter(getApplicationContext(), palceholderArticles));

    }
}