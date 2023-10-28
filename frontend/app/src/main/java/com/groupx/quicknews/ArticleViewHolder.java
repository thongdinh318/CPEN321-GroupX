package com.groupx.quicknews;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    public TextView articleTitle;
    public TextView articleSynopsis;
    public LinearLayout hiddenView;
    public CardView cardView;
    public ArticleViewHolder(@NonNull View view) {
        super(view);
        articleTitle = itemView.findViewById(R.id.article_title);
        articleSynopsis = itemView.findViewById(R.id.article_synopsis);
        hiddenView = itemView.findViewById(R.id.hidden_view);
        cardView = itemView.findViewById(R.id.base_cardview);
    }

}
