package com.example.myapplication.Community;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Comment;
import com.example.myapplication.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commentText.setText(comment.getCommentText());
        holder.comment_username.setText(comment.getUserName());
        Glide.with(context).load(comment.getUserImage()).into(holder.user_img);
        if (comment.getTimestamp() != null) {
            holder.comment_date.setText(timestampToString(comment.getTimestamp().getSeconds() * 1000L));
        } else {
            holder.comment_date.setText("none");
        }    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentText, comment_username, comment_date;
        ImageView user_img ;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_content);
            user_img = itemView.findViewById(R.id.comment_user_img);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_date = itemView.findViewById(R.id.comment_date);

        }
    }
    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM hh:mm", calendar).toString();
        return date;
    }
}

