package com.groupx.quicknews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

public class SearchArticlesFragment extends Fragment {
    private EditText category;
    private Button fromButton;
    private Button toButton;
    private DatePickerDialog datePickerDialogFrom;
    private DatePickerDialog datePickerDialogTo;
    private Button filterSearchButton;
    private SearchView searchView;
    private Button recommendedArticlesButton;
    private Spinner publisher;
    private static List<Article> articleList = new ArrayList<>();
    final static String TAG = "MainActivity";
    final static int DATEPICKER_TO = 1;
    final static int DATEPICKER_FROM = 2;
    // ChatGPT usage: No.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_articles, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDatePicker(DATEPICKER_FROM);
        initDatePicker(DATEPICKER_TO);

        fromButton = view.findViewById(R.id.date_picker_from);
        toButton = view.findViewById(R.id.date_picker_to);
        filterSearchButton = view.findViewById(R.id.filter_search_button);
        searchView = view.findViewById(R.id.searchView);
        publisher = view.findViewById(R.id.publisher_input);
        category = view.findViewById(R.id.category_input);
        recommendedArticlesButton = view.findViewById(R.id.article_button);

        category.setText("");
        fromButton.setText(getTodayDate());
        toButton.setText(getTodayDate());


        //can replace with list from server?
        String[] items = new String[]{"search all", "cbc", "cnn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        publisher.setAdapter(adapter);

        //initNavigationBar();

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

        filterSearchButton.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                String query = buildQuery();
                String url = getString(R.string.server_dns) + "article/filter/search?"+query;
                Log.d(TAG,url);
                getArticlesAndSwitchViews(url, ArticlesActivity.class);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // ChatGPT usage: No.
            @Override
            public boolean onQueryTextSubmit(String query) {
                String queryFilters = buildQuery();
                String url = getString(R.string.server_dns) + "article/filter/search?"+queryFilters;
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

    private String buildQuery() {
        String publisherName = publisher.getSelectedItem().toString();
        String categoryName = category.getText().toString();
        String keywordSearch = searchView.getQuery().toString();
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

        if ("".equals(keywordSearch)){
            query +=  "&kw=";
        }
        else {
            query += "&kw="+keywordSearch;
        }

        query += "&before="+toDate.replace(" ", "-");
        query+= "&after="+fromDate.replace(" ", "-");
        return query;
    }

    //chatGPT usage: no
    private void getArticlesAndSwitchViews(String url,  Class<?> targetActivity){
        HttpClient.getRequestWithJWT(url, new HttpClient.ApiCallback() {
            // ChatGPT usage: No.
            @Override
            public void onResponse(Response response) {
                handleSearchResponse(response, targetActivity);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    //chatGPT usage: no
    private void handleSearchResponse(Response response, Class<?> targetActivity) {
        String json = null;
        try {
            Log.d(TAG, "get articles:" +String.valueOf(response.code()));
            // Status code check
            if (response.code() == 400){
                String msg = response.body().string();
                Log.d(TAG, msg);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });

            }
            else {
                json = response.body().string();
                Log.d(TAG,json);
                JSONArray res = new JSONArray(json);
                //Matched articles check
                if (res.length() == 0){
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    ObjectMapper mapper = new ObjectMapper();
                    articleList = Arrays.asList(mapper.readValue(res.toString(), Article[].class));

                    Intent articleIntent = new Intent(getActivity().getApplicationContext(), targetActivity);;
                    startActivity(articleIntent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            datePickerDialogFrom = new DatePickerDialog(requireActivity(), dateSetListener,
                    calYear, calMonth, calDay);
            datePickerDialogFrom.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else if (curDatePicker == DATEPICKER_TO) {
            datePickerDialogTo = new DatePickerDialog(requireActivity(), dateSetListener,
                    calYear, calMonth, calDay);
            datePickerDialogTo.getDatePicker().setMaxDate(System.currentTimeMillis());
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

    // ChatGPT usage: No.
    public static List<Article> getArticleList(){
        return articleList;
    }
}