package com.groupx.quicknews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticlesViewAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    public ArticlesViewAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    Context context;
    List<Article> articles;
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.article_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.articleTitle.setText(articles.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
}

