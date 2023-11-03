package com.groupx.quicknews.ui.forumlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.ForumActivity;
import com.groupx.quicknews.ForumsListActivity;
import com.groupx.quicknews.R;

import java.util.List;

public class ForumsViewAdapter extends RecyclerView.Adapter<ForumViewHolder> {

    private Context context;
    private List<Forum> forums;
    // ChatGPT usage: No.
    public ForumsViewAdapter(Context context, List<Forum> forums) {
        this.context = context;
        this.forums = forums;
    }
    // ChatGPT usage: No.
    @NonNull
    @Override
    public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ForumViewHolder holder = new ForumViewHolder(LayoutInflater.from(context).inflate(R.layout.view_forum, parent, false));

        //navigate to specific forum when container is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ForumActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("forumID", forums.get(holder.getAbsoluteAdapterPosition()).getForumID());
                intent.putExtra("forumName", forums.get(holder.getAbsoluteAdapterPosition()).getForumName());
                context.startActivity(intent);
            }
        });
        return holder;
    }
    // ChatGPT usage: No.
    @Override
    public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
        holder.forumName.setText(forums.get(position).getForumName());
    }
    // ChatGPT usage: No.
    @Override
    public int getItemCount() {
        return forums.size();
    }
}
