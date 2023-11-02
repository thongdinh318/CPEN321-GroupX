package com.groupx.quicknews;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button recommendedArticlesButton,  forumButton, filterSearchButton ;
    private SearchView searchView;
    private EditText publisher, category;

    private Button fromButton, toButton;
    private DatePickerDialog datePickerDialogFrom, datePickerDialogTo;
    private static List<Article> articleList;
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

        publisher = findViewById(R.id.publisher_input);
        publisher.setText("");
        category = findViewById(R.id.category_input);
        category.setText("");

        filterSearchButton = findViewById(R.id.filter_search_button);
        filterSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publisherName = publisher.getText().toString();
                String categoryName = category.getText().toString();
                String fromDate = fromButton.getText().toString();
                String toDate = toButton.getText().toString();
//                Log.d(TAG, publisherName);
//                Log.d(TAG, categoryName);
//                Log.d(TAG, fromDate);
//                Log.d(TAG, toDate);
                String query = "";
                if (publisherName == ""){
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
                            json = response.body().string();
                            JSONArray res = new JSONArray(json);
                            if (res.length() == 0){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "No Articles Found", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else{
                                for (int i = 0; i < res.length(); i++){
                                    JSONObject articleJson = res.getJSONObject(i);
                                    Article article = new Article(articleJson.getString("title"),
                                            articleJson.getString("url"),
                                            articleJson.getString("content"));
                                    articleList.add(article);
                                }
                                Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);;
                                startActivity(articleIntent);
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
                HttpClient.getRequest(url, new HttpClient.ApiCallback() {
                    @Override
                    public void onResponse(Response response) {
                        try {
                            String json = response.body().string();
                            JSONArray res = new JSONArray(json);
                            if (res.length() == 0){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "No Articles Found", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            else{
                                for (int i = 0; i < res.length(); i++){
                                    JSONObject articleJson = res.getJSONObject(i);
                                    Article article = new Article(articleJson.getString("title"),
                                                                    articleJson.getString("url"),
                                                                    articleJson.getString("content"));
                                    articleList.add(article);
                                }
                                Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);;
                                startActivity(articleIntent);

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
                Intent articleIntent = new Intent(MainActivity.this, ArticlesActivity.class);
                startActivity(articleIntent);
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
        datePickerDialogTo.getDatePicker().setMaxDate(System.currentTimeMillis());

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

    public void openDatePicker(View view, DatePickerDialog datePickerDialog){
        datePickerDialog.show();
    }
    public static List<Article> getArticleList(){
        return articleList;
    }
    //TODO: push notifications for when someone posts to a followed forum
}