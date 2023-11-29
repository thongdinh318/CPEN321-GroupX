package com.groupx.quicknews;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.databinding.ActivityForumBinding;
import com.groupx.quicknews.helpers.ForumSocket;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.forum.Comment;
import com.groupx.quicknews.ui.forum.CommentsViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;


public class ForumActivity extends AppCompatActivity {

    private ActivityForumBinding binding;
    private RecyclerView forumView;
    private EditText commentText;
    private Button postButton;
    private String forumID;
    private List<Comment> comments;
    private ForumSocket webSocket;
    final static String TAG = "ForumActivity";

    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        forumID = getIntent().getStringExtra("forumID");
        webSocket = new ForumSocket();

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("forumName"));

        forumView = findViewById(R.id.view_comment);
        commentText = findViewById(R.id.edit_post);

        postButton = findViewById(R.id.button_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment(commentText.getText().toString());
                commentText.getText().clear();
            }
        });
    }

    // ChatGPT usage: No.
    @Override
    protected void onStart() {
        super.onStart();
        getComments();
        webSocket.openWebSocket();
    }

    // ChatGPT usage: No.
    private void getComments () {
        String url = getString(R.string.server_dns) + "forums/" + forumID;
        try {
        HttpClient.getRequest(url, new HttpClient.ApiCallback(){
            // ChatGPT usage: No.
            @Override
            public void onResponse(Response response) throws IOException {
                int statusCode = response.code();
                if (statusCode == 200){
                    String responseBody = response.body().string();
                    Log.d(TAG, responseBody);
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        JSONObject json = jsonArray.getJSONObject(0);
                        JSONArray jsonComments = json.getJSONArray("comments");

                        //update forum comment view
                        ObjectMapper mapper = new ObjectMapper();
                        comments = Arrays.asList(mapper.readValue(jsonComments.toString(), Comment[].class));
                        comments = new ArrayList<>(comments); //jacksons creates immutable list
                        runOnUiThread(new Runnable() {
                            // ChatGPT usage: No.
                            @Override
                            public void run() {
                                forumView.setLayoutManager(new LinearLayoutManager(ForumActivity.this));
                                forumView.setAdapter(new CommentsViewAdapter(getApplicationContext(), comments));
                            }
                        });
                    } catch (JSONException e) {
                        throw new IOException(e);
                    }
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

    // ChatGPT usage: No.
    private void postComment(String comment) {
        String url = getString(R.string.server_dns) + "addComment/" + forumID;
        try {
            JSONObject json = new JSONObject();
            json.put("userId", LoginActivity.getUserId());
            json.put("commentData", comment);
            HttpClient.postRequest(url, json.toString(), new HttpClient.ApiCallback(){
                @Override
                public void onResponse(Response response) throws IOException{

                    String responseBody = response.body().string();
                    int statusCode = response.code();
                    //TODO: update statusCodes so they convey more information
                    if (statusCode == 200){
                        Comment postedComment = new Comment(LoginActivity.getAccount().getDisplayName(), comment);

                        runOnUiThread(new Runnable() {
                            // ChatGPT usage: No.
                            @Override
                            public void run() {
                                comments.add(postedComment);
                                forumView.getAdapter().notifyItemInserted(comments.size()-1);
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

    //https://dev.to/ahmmedrejowan/hide-the-soft-keyboard-and-remove-focus-from-edittext-in-android-ehp
    // ChatGPT usage: No.
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}