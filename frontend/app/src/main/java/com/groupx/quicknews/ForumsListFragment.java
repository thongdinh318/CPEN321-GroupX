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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupx.quicknews.helpers.HttpClient;
import com.groupx.quicknews.ui.forumlist.Forum;
import com.groupx.quicknews.ui.forumlist.ForumsViewAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Response;

public class ForumsListFragment extends Fragment {


    private RecyclerView viewForum;
    private List<Forum> forums;
    final static String TAG = "ForumsListActivity";

    // ChatGPT usage: No.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forums_list, container, false);

        return rootView;
    }
    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewForum = view.findViewById(R.id.view_forum);
        getForums();
    }

    // ChatGPT usage: No.
    private void getForums () {
        String url = getString(R.string.server_dns) + "forums";
        try {
            HttpClient.getRequest(url, new HttpClient.ApiCallback(){
                @Override
                public void onResponse(Response response) throws IOException {
                    handleForumResponse(response);
                }
                // ChatGPT usage: No.
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "exception", e);
                }
            });
        }
        catch(Exception e) {
            Log.e(TAG, "exception", e);
        }
    }

    //ChatGPT usage: No
    private void handleForumResponse(Response response) throws IOException {
        int statusCode = response.code();
        if (statusCode == 200) {
            String responseBody = response.body().string();
            Log.d(TAG, responseBody);
            updateForumsList(responseBody);
        }
    }

    //ChatGPT usage: No
    private void updateForumsList(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        forums = Arrays.asList(mapper.readValue(responseBody, Forum[].class));
        getActivity().runOnUiThread(() -> {
            viewForum.setLayoutManager(new LinearLayoutManager(getActivity()));
            viewForum.setAdapter(new ForumsViewAdapter(getActivity().getApplicationContext(), forums));
        });
    }
}