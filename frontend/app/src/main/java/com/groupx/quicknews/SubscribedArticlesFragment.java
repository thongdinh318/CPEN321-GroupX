package com.groupx.quicknews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.articles.Article;
import com.groupx.quicknews.ui.articles.ArticlesViewAdapter;

import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public class SubscribedArticlesFragment extends Fragment {

    private List<Article> articles;
    private RecyclerView articleView;

    final static String TAG = "SubscribedFragment";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subscribed_articles, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        articleView = view.findViewById(R.id.view_article);
        getSubscribedArticles();
    }

    public void getSubscribedArticles() {
        String getUrl = getString(R.string.server_dns) +"article/subscribed/"+ LoginActivity.getUserId();
        HttpClient.getRequestWithJWT(getUrl, new HttpClient.ApiCallback() {
            // ChatGPT usage: No.
            @Override
            public void onResponse(Response response) {
                handleSubscribedArticleResponse(response);
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleSubscribedArticleResponse(Response response) {
        try {
            Log.d(TAG, response.toString());
            if (response.code() == 200) {
                String responseBody = response.body().string();
                Log.d(TAG, responseBody);
                //update forums list
                ObjectMapper mapper = new ObjectMapper();
                articles = Arrays.asList(mapper.readValue(responseBody, Article[].class));
                getActivity().runOnUiThread(new Runnable() {
                    // ChatGPT usage: No.
                    @Override
                    public void run() {
                        Log.d(TAG, articles.toString());
                        articleView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        articleView.setAdapter(new ArticlesViewAdapter(getActivity(), articles));
                    }
                });
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getString(R.string.toast_no_sub), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}