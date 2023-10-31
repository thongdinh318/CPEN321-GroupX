package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity {

    private String[] subList;
    private TextView cbc, cnn;
    private Switch cbc_sub, cnn_sub;
    private Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            subList = extras.getStringArray("SUB_LIST");
        }

        cbc = findViewById(R.id.news_publisher_1);
        cbc.setText("CBC");
        cnn = findViewById(R.id.news_publisher_2);
        cnn.setText("CNN");

        cbc_sub = findViewById(R.id.sub_button_1);
        cbc_sub.setChecked(false);
        cnn_sub = findViewById(R.id.sub_button_2);
        cnn_sub.setChecked(false);
        for (int i = 0; i < subList.length; ++i){
            if (subList[i].contains("cbc")){
                cbc_sub.setChecked(true);
            } else if (subList[i].contains("cnn")) {
                cnn_sub.setChecked(true);
            }
        }

        List<String> sub_list = new ArrayList<String>(Arrays.asList(subList));
        cbc_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!checked){
                    sub_list.remove("cbc");
                }
                else{
                    sub_list.add("cbc");
                }
            }
        });

        cnn_sub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!checked){
                    sub_list.remove("cnn");
                }
                else{
                    sub_list.add("cnn");
                }
            }
        });

        confirm = findViewById(R.id.sub_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] new_sub = new String[sub_list.size()];

                for (int i = 0; i<sub_list.size(); ++i){
                    new_sub[i] = sub_list.get(i);
                }

                //TODO: craft post request here

                //Return to previous view
                Intent profileIntent = new Intent(SubscriptionActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });
    }
}