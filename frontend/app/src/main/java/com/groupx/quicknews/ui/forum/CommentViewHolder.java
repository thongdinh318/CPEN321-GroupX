package com.groupx.quicknews.ui.forum;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.R;

// ChatGPT usage: No.
public class CommentViewHolder extends RecyclerView.ViewHolder {
    public TextView user;
    public TextView comment;
    public CommentViewHolder(@NonNull View view) {
        super(view);
        user = itemView.findViewById(R.id.text_user);
        comment = itemView.findViewById(R.id.text_comment);
    }

}
