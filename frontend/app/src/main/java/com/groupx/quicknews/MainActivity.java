package com.groupx.quicknews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button forumButton  ;
    private Button articlesButton;
    private SearchView searchView;
    private static List<Article> articleList;
    final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forumButton = findViewById(R.id.forum_button);
        articlesButton = findViewById(R.id.article_button);
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

        articlesButton.setOnClickListener(new View.OnClickListener() {
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

    public static List<Article> getArticleList(){
        return articleList;
    }
    //TODO: push notifications for when someone posts to a followed forum
}