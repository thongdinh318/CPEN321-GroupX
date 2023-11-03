package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.TextView;
import android.widget.Toast;

import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button recommendedArticlesButton,  forumButton, filterSearchButton ;
    private SearchView searchView;
    private EditText publisher, category;
    private Button fromButton, toButton;
    private DatePickerDialog datePickerDialogFrom, datePickerDialogTo;
    private static List<Article> articleList = new ArrayList<>();
    final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatePickerFrom();
        initDatePickerTo();
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

        fromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view, datePickerDialogFrom);
            }
        });

        toButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view, datePickerDialogTo);
            }
        });

        category = findViewById(R.id.category_input);
        category.setText("");

        filterSearchButton = findViewById(R.id.filter_search_button);
        filterSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publisherName = publisher.getSelectedItem().toString();
                String categoryName = category.getText().toString();
                String fromDate = fromButton.getText().toString();
                String toDate = toButton.getText().toString();
//                Log.d(TAG, publisherName);
//                Log.d(TAG, categoryName);
//                Log.d(TAG, fromDate);
//                Log.d(TAG, toDate);
                String query = "";
                if (publisherName == "search all"){
                    query +=  "publisher=";
                }
                else {
                    query += "publisher="+publisherName;
                }

                if (categoryName == ""){
                    query +=  "&categories=";
                }
                else {
                    query += "&categories="+categoryName;
                }

                query += "&before="+toDate.replace(" ", "-");
                query+= "&after="+fromDate.replace(" ", "-");
                String url = getString(R.string.server_dns) + "article/filter/search?"+query;
//                Log.d(TAG,url);

                HttpClient.getRequest(url, new HttpClient.ApiCallback() {
                    @Override
                    public void onResponse(Response response) {
                        String json = null;
                        try {
                            //Check if error occured on the server
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
//                            Log.d(TAG, json);
                                JSONArray res = new JSONArray(json);
                                //Check for any matched articles, length == 0 means no match
                                if (res.length() == 0) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "No Articles Found", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    articleList = new ArrayList<Article>(); //clear the articleList to refresh ArticleActivity when switching
                                    for (int i = 0; i < res.length(); i++) {
                                        JSONObject articleJson = res.getJSONObject(i);
                                        Article article = new Article(articleJson.getString("title"),
                                                articleJson.getString("url"),
                                                articleJson.getString("content"),
                                                articleJson.getInt("articleId"));
                                        articleList.add(article);
                                    }
                                    Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);
                                    startActivity(articleIntent);
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });
        forumButton = findViewById(R.id.forum_button);
        recommendedArticlesButton = findViewById(R.id.article_button);
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = getString(R.string.server_dns) + "article/kwsearch/search?keyWord="+query;
                Log.d(TAG,url);
                HttpClient.getRequest(url, new HttpClient.ApiCallback() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            //Status code check
                            if (response.code() == 400){
                                String msg = response.body().string();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            else{
                                String json = response.body().string();
                                Log.d(TAG, json);
                                JSONArray res = new JSONArray(json);
                                //Check if matched articles found
                                if (res.length() == 0){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "No Articles Found", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                                else{
                                    articleList = new ArrayList<Article>();
                                    for (int i = 0; i < res.length(); i++){
                                        JSONObject articleJson = res.getJSONObject(i);
                                        Article article = new Article(articleJson.getString("title"),
                                                articleJson.getString("url"),
                                                articleJson.getString("content"),
                                                articleJson.getInt("articleId"));
                                        articleList.add(article);
                                    }
                                    Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);;
                                    startActivity(articleIntent);
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        recommendedArticlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Trying to open articles view");
                //TODO:Get User Id here
                String userId = LoginActivity.getUserId();
                String url = getString(R.string.server_dns) + "recommend/article/"+userId;
                HttpClient.getRequest(url, new HttpClient.ApiCallback() {
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
                                            Toast.makeText(MainActivity.this, "No Recommendation", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else {
                                    articleList = new ArrayList<Article>();
                                    for (int i = 0; i < res.length(); i++) {
                                        JSONObject articleJson = res.getJSONObject(i);
                                        Article article = new Article(articleJson.getString("title"),
                                                articleJson.getString("url"),
                                                articleJson.getString("content"),
                                                articleJson.getInt("articleId"));
                                        articleList.add(article);
                                    }
                                Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);;
                                startActivity(articleIntent);
                                }
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            }
        });

        forumButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Trying to open forum view");
                Intent forumIntent = new Intent(MainActivity.this, ForumsListActivity.class);
                startActivity(forumIntent);
            }
        });
    }
    //Helper functions to create date pickers --->
    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    private void initDatePickerTo() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                toButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialogTo = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener,
                year, month, day);
//        datePickerDialogTo.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    private void initDatePickerFrom() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                fromButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialogFrom = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener,
                                                year, month, day);
        datePickerDialogFrom.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

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

    private void openDatePicker(View view, DatePickerDialog datePickerDialog){
        datePickerDialog.show();
    }

    //<--- Helper functions

    //https://dev.to/ahmmedrejowan/hide-the-soft-keyboard-and-remove-focus-from-edittext-in-android-ehp
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_articles, menu);
        return true;
    }
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

    public static List<Article> getArticleList(){
        return articleList;
    }
    //TODO: push notifications for when someone posts to a followed forum
}