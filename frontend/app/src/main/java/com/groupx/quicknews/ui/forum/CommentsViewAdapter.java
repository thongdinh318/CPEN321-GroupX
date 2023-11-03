package com.groupx.quicknews.ui.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groupx.quicknews.R;

import java.util.List;

public class CommentsViewAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    private Context context;
    private List<Comment> comments;

    final static String TAG = "ArticleView";
    public CommentsViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentViewHolder holder = new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.view_comment, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.user.setText(comments.get(position).getUser());
        holder.comment.setText(comments.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
