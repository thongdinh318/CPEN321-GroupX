package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView username_view, dob_view, user_email_view;
    private Button sub_list_button, history_button, confirm_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username_view = findViewById(R.id.username_view);
        user_email_view = findViewById(R.id.user_email_view);
        dob_view = findViewById(R.id.dob_view);
        sub_list_button = findViewById(R.id.sub_list_button);
        history_button = findViewById(R.id.reading_history_button);
        int userId = 0; //placeholder for testing
        JSONObject res = getProfile(userId); // Get user profile from the server
        try {
            String username = res.getJSONObject("username").toString();
            username_view.setText("Username: "+ username);
            String dob = res.getJSONObject("dob").toString();
            dob_view.setText("Date of Birth: " + dob);
            String user_email = res.getJSONObject("user_email").toString();
            user_email_view.setText("User email: "+ user_email);
            JSONArray sub_list = res.getJSONArray("sub_list");
            String[] subscriptionList = jsonArrToStringArr(sub_list);
            sub_list_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Switch to display sub list
                    Intent subListIntent = new Intent(ProfileActivity.this, SubscriptionActivity.class);
                    subListIntent.putExtra("USER_ID", userId);
                    startActivity(subListIntent);

                }
            });
//            JSONArray reading_history = res.getJSONArray("history");
            history_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent historyIntent = new Intent(ProfileActivity.this, HistoryActivity.class);
                    historyIntent.putExtra("USER_ID", userId);
                    startActivity(historyIntent);
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private JSONObject getProfile(int userId){
        //Made HTTP request here
        return null;
    }

    private String[] jsonArrToStringArr(JSONArray jsonArr){
        String[] strArr = new String[jsonArr.length()];
        for (int i = 0; i < jsonArr.length(); ++i){
            strArr[i] = jsonArr.optString(i);
        }
        return strArr;
    }
}