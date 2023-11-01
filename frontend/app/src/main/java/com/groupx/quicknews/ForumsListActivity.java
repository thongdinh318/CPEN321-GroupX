package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.groupx.quicknews.databinding.ActivityForumBinding;
import com.groupx.quicknews.databinding.ActivityForumsListBinding;
import com.groupx.quicknews.ui.forum.Comment;
import com.groupx.quicknews.ui.forum.CommentsViewAdapter;
import com.groupx.quicknews.ui.forumlist.Forum;
import com.groupx.quicknews.ui.forumlist.ForumsViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ForumsListActivity extends AppCompatActivity {

    private ActivityForumsListBinding binding;
    private RecyclerView viewForum;
    final static String TAG = "ForumsListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_list);

        binding = ActivityForumsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        List<Forum> placeHolderForums = new ArrayList<Forum>();
        placeHolderForums.add(new Forum("111", "economics"));
        placeHolderForums.add(new Forum("222", "sports"));

        viewForum = findViewById(R.id.view_forum);
        viewForum.setLayoutManager(new LinearLayoutManager(this));
        viewForum.setAdapter(new ForumsViewAdapter(getApplicationContext(), placeHolderForums));
    }

    //TODO: get list of forums
}