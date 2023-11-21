package com.groupx.quicknews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.groupx.quicknews.databinding.ActivityMainBinding;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private EditText category;
    private Button fromButton;
    private Button toButton;
    private DatePickerDialog datePickerDialogFrom;
    private DatePickerDialog datePickerDialogTo;
    private static List<Article> articleList = new ArrayList<>();
    final static String TAG = "MainActivity";
    final static int DATEPICKER_TO = 1;
    final static int DATEPICKER_FROM = 2;
    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button filterSearchButton;
        SearchView searchView;
        Button recommendedArticlesButton;

        initDatePicker(DATEPICKER_FROM);
        initDatePicker(DATEPICKER_TO);

        fromButton = findViewById(R.id.date_picker_from);
        toButton = findViewById(R.id.date_picker_to);

        fromButton.setText(getTodayDate());
        toButton.setText(getTodayDate());

        Spinner publisher = findViewById(R.id.publisher_input);
        //can replace with list from server?
        String[] items = new String[]{"search all", "cbc", "cnn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        publisher.setAdapter(adapter);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.findViewById(R.id.action_home).setEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if (itemID == R.id.action_home) {
                return true;
            }
            else if (itemID == R.id.action_forums) {
                Log.d(TAG, "Trying to open forum view");
                Intent forumIntent = new Intent(MainActivity.this, ForumsListActivity.class);
                startActivity(forumIntent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });


        fromButton.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                openDatePicker(datePickerDialogFrom);
            }
        });

        toButton.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                openDatePicker(datePickerDialogTo);
            }
        });

        category = findViewById(R.id.category_input);
        category.setText("");

        filterSearchButton = findViewById(R.id.filter_search_button);
        filterSearchButton.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                String publisherName = publisher.getSelectedItem().toString();
                String categoryName = category.getText().toString();
                String fromDate = fromButton.getText().toString();
                String toDate = toButton.getText().toString();
                String query = "";
                if ("search all".equals(publisherName )){
                    query +=  "publisher=";
                }
                else {
                    query += "publisher="+publisherName;
                }

                if ("".equals(categoryName)){
                    query +=  "&categories=";
                }
                else {
                    query += "&categories="+categoryName;
                }

                query += "&before="+toDate.replace(" ", "-");
                query+= "&after="+fromDate.replace(" ", "-");
                String url = getString(R.string.server_dns) + "article/filter/search?"+query;
                getArticlesAndSwitchViews(url, ArticlesActivity.class);
            }
        });
        recommendedArticlesButton = findViewById(R.id.article_button);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // ChatGPT usage: No.
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = getString(R.string.server_dns) + "article/kwsearch/search?keyWord="+query;
                Log.d(TAG,url);
                getArticlesAndSwitchViews(url, ArticlesActivity.class);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        recommendedArticlesButton.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Trying to open articles view");
                String userId = LoginActivity.getUserId();
                String url = getString(R.string.server_dns) + "recommend/article/"+userId;
                getArticlesAndSwitchViews(url, ArticlesActivity.class);
            }
        });
    }

    private void getArticlesAndSwitchViews(String url, Class<?> targetActivity){
        HttpClient.getRequest(url, new HttpClient.ApiCallback() {
            // ChatGPT usage: No.
            @Override
            public void onResponse(Response response) {
                String json = null;
                try {
                    Log.d(TAG, String.valueOf(response.code()));
                    // Status code check
                    if (response.code() == 400){
                        String msg = response.body().string();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else {
                        json = response.body().string();
                        Log.d(TAG,json);
                        JSONArray res = new JSONArray(json);
                        //Matched articles check
                        if (res.length() == 0){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            ObjectMapper mapper = new ObjectMapper();
                            articleList = Arrays.asList(mapper.readValue(res.toString(), Article[].class));
                            articleList = new ArrayList<>(articleList); //jacksons creates immutable list

                            Intent articleIntent = new Intent(MainActivity.this, targetActivity);;
                            startActivity(articleIntent);
                        }
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }


    //Helper functions to create date pickers --->
    // ChatGPT usage: No.
    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    // ChatGPT usage: No.
    private void initDatePicker( int curDatePicker ) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                int realMonth = month + 1;
                String date = makeDateString(day, realMonth, year);
                if (curDatePicker == DATEPICKER_FROM) {
                    fromButton.setText(date);
                }
                else if (curDatePicker == DATEPICKER_TO) {
                    toButton.setText(date);
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int calYear = cal.get(Calendar.YEAR);
        int calMonth = cal.get(Calendar.MONTH);
        int calDay = cal.get(Calendar.DAY_OF_MONTH);

        if (curDatePicker == DATEPICKER_FROM) {
            datePickerDialogFrom = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener,
                    calYear, calMonth, calDay);
            datePickerDialogFrom.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else if (curDatePicker == DATEPICKER_TO) {
            datePickerDialogTo = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener,
                    calYear, calMonth, calDay);
        }
    }

    // ChatGPT usage: No.
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    // ChatGPT usage: No.
    private String getMonthFormat(int month) {
        String monthString;
        switch (month){
            case 1:
                monthString = "JAN";
                break;
            case 2:
                monthString = "FEB";
                break;
            case 3:
                monthString = "MAR";
                break;
            case 4:
                monthString = "APR";
                break;
            case 5:
                monthString = "MAY";
                break;
            case 6:
                monthString = "JUN";
                break;
            case 7:
                monthString = "JUL";
                break;
            case 8:
                monthString = "AUG";
                break;
            case 9:
                monthString = "SEP";
                break;
            case 10:
                monthString = "OCT";
                break;
            case 11:
                monthString = "NOV";
                break;
            case 12:
                monthString = "DEC";
                break;
            default: monthString = "";
        }
        return monthString;
    }
    // ChatGPT usage: No.
    private void openDatePicker(DatePickerDialog datePickerDialog){
        datePickerDialog.show();
    }

    //<--- Helper functions

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

    // ChatGPT usage: No.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_articles, menu);
        return true;
    }
    // ChatGPT usage: No.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switching on the item id of the menu item
        int itemId = item.getItemId();
        if( itemId == R.id.action_manage_subscriptions ) {
            // Code to be executed when the add button is clicked
            Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
            startActivity(intent);
            return true;
        }
        else if( itemId == R.id.action_view_history ) {
            // Code to be executed when the add button is clicked
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ChatGPT usage: No.
    public static List<Article> getArticleList(){
        return articleList;
    }
}