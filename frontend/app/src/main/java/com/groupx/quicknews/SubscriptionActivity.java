package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.groupx.quicknews.helpers.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public class SubscriptionActivity extends AppCompatActivity {

    final static String TAG = "SubscriptionActivity";
    private List<String> subscriptionList;
    private String userId;
    private TextView cbc, cnn;
    private Switch cbc_sub, cnn_sub;
    private Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            userId = extras.getString("USER_ID");
        }

        //GET REQUEST GET THE SUB LIST
        String getUrl = getString(R.string.server_client_id) +"profile/"+ userId+"/subscriptions";
        HttpClient.getRequest(getUrl, new HttpClient.ApiCallback() {
            @Override
            public void onResponse(Response response) {
                try {
                    if (response.code() == 200) {
                        JSONArray userList = new JSONArray(response.body().toString());
                        if (userList.length() > 0) {
                            for (int i = 0; i < userList.length(); i++) {
                                subscriptionList.add(userList.getString(i));
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
        cbc = findViewById(R.id.news_publisher_1);
        cbc.setText("CBC");
        cnn = findViewById(R.id.news_publisher_2);
        cnn.setText("CNN");

        cbc_sub = findViewById(R.id.sub_button_1);
        cbc_sub.setChecked(false);
        cnn_sub = findViewById(R.id.sub_button_2);
        cnn_sub.setChecked(false);
        for (int i = 0; i < subscriptionList.size(); ++i){
            if (subscriptionList.get(i).contains("cbc")){
                cbc_sub.setChecked(true);
            } else if (subscriptionList.get(i).contains("cnn")) {
                cnn_sub.setChecked(true);
            }
        }
        cbc_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!checked){
                    subscriptionList.remove("cbc");
                    Log.d(TAG, subscriptionList.toString());
                }
                else{
                    subscriptionList.add("cbc");
                    Log.d(TAG, subscriptionList.toString());
                }
            }
        });

        cnn_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!checked){
                    subscriptionList.remove("cnn");
                    Log.d(TAG, subscriptionList.toString());
                }
                else{
                    subscriptionList.add("cnn");
                    Log.d(TAG, subscriptionList.toString());
                }
            }
        });

        confirm = findViewById(R.id.sub_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray newSubArr = new JSONArray();

                for (int i = 0; i<subscriptionList.size(); ++i){
                    newSubArr.put(subscriptionList.get(i));
                }

                String putUrl = getString(R.string.server_client_id)+"profile/"+userId;
                try {
                    JSONObject json = new JSONObject();
                    json.put("subscriptionList", newSubArr);
                    HttpClient.putRequest(putUrl, json.toString(), new HttpClient.ApiCallback() {
                        @Override
                        public void onResponse(Response response) {
                            if (response.code() == 200){
                                String result = response.body().toString();
                                if (result == "true"){
                                    //TODO: Switch views here
                                    Log.d(TAG,result);
                                    //Return to previous view
                                    Intent profileIntent = new Intent(SubscriptionActivity.this, ProfileActivity.class);
                                    startActivity(profileIntent);
                                }
                            }
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.d(TAG,e.getMessage());
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}