package com.groupx.quicknews;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ForumActivity extends AppCompatActivity {

    private RecyclerView forumView;
    private EditText commentText;

    private String forumID;
    private List<Comment> comments;
    private Socket socket;
    final static String TAG = "ForumActivity";

    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityForumBinding binding;
        Button postButton;
        Log.d(TAG, "Enter forum activity");
        binding = ActivityForumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        forumID = getIntent().getStringExtra("forumID");
        setSocket();

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("forumName"));

        forumView = findViewById(R.id.view_comment);
        commentText = findViewById(R.id.edit_post);

        postButton = findViewById(R.id.button_post);
        postButton.setEnabled(false);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emitComment(commentText.getText().toString());
                commentText.getText().clear();
            }
        });

        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                postButton.setEnabled(!charSequence.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    // ChatGPT usage: Yes
    private void setSocket(){
        try {
            Log.d(TAG,"setting socket");
            socket = IO.socket(getString(R.string.server_socket));
        } catch (URISyntaxException e) {
            Log.d(TAG, "Error connecting to socket:" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ChatGPT usage: No.
    @Override
    protected void onStart() {
        super.onStart();
        getComments();
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on("new_message", onNewMessage);
    }

    // ChatGPT Usage: No
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Left forum");
        socket.disconnect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Socket connected");
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //TODO: do something when a new message is added to the forum
            Log.d(TAG,"New message, update");
            getComments();
        }
    };

    //ChatGPT usage: Yes
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Exception e = (Exception) args[0];
            Log.d(TAG, "Socket.IO connection error: " + e.getMessage());
            e.printStackTrace();
        }
    };

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
                        Log.d(TAG,jsonComments.toString());
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
                        e.printStackTrace();
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
                    int statusCode = response.code();
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
                    e.printStackTrace();
                }
            });
        }
        catch(Exception e) {
            Log.e(TAG, "exception", e);
        }
    }

    private void emitComment(String comment){
        //Posting comment to the server
        JSONObject json = new JSONObject();
        try {
            json.put("userId", LoginActivity.getUserId());
            json.put("commentData", comment);
            json.put("forum_id", forumID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        socket.emit("message", json.toString());
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