package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class SubscriptionActivity extends AppCompatActivity {

    final static String TAG = "SubscriptionActivity";
    private List<String> subscriptionList;
    private String userId = LoginActivity.getUserId();
    private Switch cbc_sub;
    private Switch cnn_sub;

    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Button confirm;
        TextView cbc;
        TextView cnn;

        subscriptionList = new ArrayList<>();

        cbc = findViewById(R.id.news_publisher_1);
        cbc.setText("CBC");
        cnn = findViewById(R.id.news_publisher_2);
        cnn.setText("CNN");

        cbc_sub = findViewById(R.id.sub_button_1);
        cbc_sub.setChecked(false);
        cnn_sub = findViewById(R.id.sub_button_2);
        cnn_sub.setChecked(false);

        cbc_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // ChatGPT usage: No.
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
            // ChatGPT usage: No.
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
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                updateSubscriptionList();
                finish();
            }
        });
    }

    // ChatGPT usage: No.
    @Override
    protected void onStart() {
        super.onStart();
        subscriptionList.clear();
        getSubscriptionList ();
    }

    // ChatGPT usage: No.
    private void getSubscriptionList () {
        //GET REQUEST GET THE SUB LIST
        String getUrl = getString(R.string.server_dns) +"profile/"+ userId+"/subscriptions";
        HttpClient.getRequestWithJWT(getUrl, new HttpClient.ApiCallback() {
            @Override
            public void onResponse(Response response) {
                try {
                    if (response.code() == 200) {
                        String responseBody = response.body().string();
                        JSONArray userList = new JSONArray(responseBody);
                        Log.d(TAG, responseBody);
                        setSwitchValues(userList);
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

    private void setSwitchValues(JSONArray userList) {
        if (userList.length() <= 0) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < userList.length(); i++) {
//                                    String sub = "";
                    try {
                        String sub = userList.getString(i);
                        if (sub.contains("cbc")){
                            cbc_sub.setChecked(true);
                            cbc_sub.jumpDrawablesToCurrentState();
                        } else if (sub.contains("cnn")) {
                            cnn_sub.setChecked(true);
                            cnn_sub.jumpDrawablesToCurrentState();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    // ChatGPT usage: No.
    private void updateSubscriptionList () {
        JSONArray newSubArr = new JSONArray();

        for (int i = 0; i<subscriptionList.size(); ++i){
            newSubArr.put(subscriptionList.get(i));
        }

        String putUrl = getString(R.string.server_dns)+"profile/"+userId;
        JSONObject json = new JSONObject();
        try {
            json.put("subscriptionList", newSubArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpClient.putRequestWithJWT(putUrl, json.toString(), new HttpClient.ApiCallback() {
            @Override
            public void onResponse(Response response) {
                if (response.code() == 200){
                    try {
                        String result = response.body().string();
                        if ("true".equals(result)){
                            Log.d(TAG,result);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        }
}