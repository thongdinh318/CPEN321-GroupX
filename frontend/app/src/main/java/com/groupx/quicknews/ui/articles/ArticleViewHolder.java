package com.groupx.quicknews.ui.articles;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    public TextView articleTitle;
    public TextView articleSynopsis;
    public TextView articlePublisher;
    public TextView articleDatePublished;
    public ImageView articlePreviewImage;
    public LinearLayout hiddenView;
    public CardView cardView;
    public Button redirectButton;
    // ChatGPT usage: No.
    public ArticleViewHolder(@NonNull View view) {
        super(view);
        articleTitle = itemView.findViewById(R.id.text_article_title);
        articleSynopsis = itemView.findViewById(R.id.text_article_synopsis);
        articlePublisher = itemView.findViewById(R.id.text_publisher);
        articleDatePublished = itemView.findViewById(R.id.text_date_published);
        articlePreviewImage = itemView.findViewById(R.id.image_article_thumbnail);
        hiddenView = itemView.findViewById(R.id.layout_hidden_view);
        cardView = itemView.findViewById(R.id.base_cardview);
        redirectButton = itemView.findViewById(R.id.button_redirect_article);
    }

}
