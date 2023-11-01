package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.groupx.quicknews.helpers.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {

    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


        Bundle extras = getIntent().getExtras();

        if (extras != null){
            userId = extras.getString("USER_ID");
        }
        // Make a get request to get the history of this user
        String getUrl = getString(R.string.server_client_id) +"profile/"+ userId +"/history";
        HashMap<Integer,String> readingList = new HashMap<>();
        HttpClient.getRequest(getUrl, new HttpClient.ApiCallback() {
            @Override
            public void onResponse(Response response) {
                try {
                    if (response.code() == 200) {
                        JSONArray userHistory = new JSONArray(response.body().toString());
                        if (userHistory.length() > 0) {
                            for (int i = 0; i < userHistory.length(); i++) {
                                JSONObject article = userHistory.getJSONObject(i);
                                readingList.put(article.getInt("articleId"), article.getString("title"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(Exception e) {
            }
        });

        //Display the list of article for the user here
    }
}