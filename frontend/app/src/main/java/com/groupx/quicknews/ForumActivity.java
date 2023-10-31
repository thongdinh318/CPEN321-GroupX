package com.groupx.quicknews;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.databinding.ActivityForumBinding;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private ActivityForumBinding binding;
    private RecyclerView forumView;

    //TODO: pass in forum details so correct endpoint is used when adding new messages
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        List<Comment> placeHolderComments = new ArrayList<Comment>();
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));
        placeHolderComments.add(new Comment("User1", "You're opinions are terrible and you should feel bad about them"));
        placeHolderComments.add(new Comment("User2", "*your. at least learn to spell"));

        forumView = findViewById(R.id.comment_view);
        forumView.setLayoutManager(new LinearLayoutManager(this));
        forumView.setAdapter(new CommentsViewAdapter(getApplicationContext(), placeHolderComments));
    }

    //TODO: get comments from server, endpoint details need to be passed in
}