package com.groupx.quicknews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    TextView articleTitle;
    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        articleTitle = itemView.findViewById(R.id.article_title);
    }
}
