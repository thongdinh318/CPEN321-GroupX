package com.groupx.quicknews.ui.articles;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.HistoryActivity;
import com.groupx.quicknews.LoginActivity;
import com.groupx.quicknews.R;
import com.groupx.quicknews.helpers.HttpClient;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Response;

public class ArticlesViewAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private Context context;
    private List<Article> articles;
    final static String TAG = "ArticleView";
    public ArticlesViewAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }
    // ChatGPT usage: No.
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArticleViewHolder holder = new ArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.view_article, parent, false));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                Log.d(TAG, holder.articleTitle.getText().toString());
                Article article = articles.get(holder.getAbsoluteAdapterPosition());
                if (!article.getArticleRead() && !(context instanceof HistoryActivity)) {
                    addArticleToHistory(article);
                }
                if (holder.hiddenView.getVisibility() == View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.hiddenView.setVisibility(View.GONE);
                }
                else {
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.hiddenView.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.redirectButton.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(articles.get(holder.getAbsoluteAdapterPosition()).getUrl()); // missing 'http:' will crash
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    // ChatGPT usage: No.
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.articleTitle.setText(articles.get(position).getTitle());
        holder.articlePublisher.setText(articles.get(position).getPublisher());
        holder.articleSynopsis.setText(articles.get(position).getSynopsis());

        String date = new SimpleDateFormat("dd/MM/yyyy").format(articles.get(position).getDatePublished());
        holder.articleDatePublished.setText(date);
    }

    // ChatGPT usage: No.
    @Override
    public int getItemCount() {
        return articles.size();
    }
    // ChatGPT usage: No.
    private void addArticleToHistory(Article article) {
        String url =  context.getString(R.string.server_dns) + "profile/"+ LoginActivity.getUserId() + "/history";
        try {
            JSONObject json = new JSONObject();
            json.put("articleId", article.getArticleId());
            HttpClient.putRequest(url, json.toString(), new HttpClient.ApiCallback(){
                // ChatGPT usage: No.
                @Override
                public void onResponse(Response response) {
                    try{
                        Log.d(TAG, response.toString());
                        Log.d(TAG, response.body().string());

                        int statusCode = response.code();
                        if (statusCode == 200){
                            article.setArticleRead(true);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

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
}

