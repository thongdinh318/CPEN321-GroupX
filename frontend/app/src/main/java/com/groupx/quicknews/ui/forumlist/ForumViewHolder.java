package com.groupx.quicknews.ui.forumlist;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.groupx.quicknews.R;


public class ForumViewHolder extends RecyclerView.ViewHolder {
    public TextView forumName;

    public ForumViewHolder(@NonNull View view) {
        super(view);
        forumName = itemView.findViewById(R.id.text_forum_name);
    }
}
