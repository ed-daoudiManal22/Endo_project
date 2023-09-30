package com.spmenais.paincare.Community;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spmenais.paincare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private final Context context;
    private final List<Comment> comments;
    private final String ownerId;

    public CommentsAdapter(Context context, List<Comment> comments,String ownerId) {
        this.context = context;
        this.comments = comments;
        this.ownerId = ownerId;
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
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentText;
        TextView comment_username;
        TextView comment_date;
        ImageView user_img ;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_content);
            user_img = itemView.findViewById(R.id.comment_user_img);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_date = itemView.findViewById(R.id.comment_date);
            itemView.setOnLongClickListener(v -> {
                Comment comment = comments.get(getAdapterPosition());
                Log.d("Comment__Holder", "Comment UserID: " + comment.getUserId());
                Log.d("Comment__Holder", "Owner ID: " + ownerId);
                if (isCurrentUserOrOwner(comment.getUserId(), ownerId)) {
                    showDeleteDialog(getAdapterPosition());
                }
                return true;
            });
        }
    }
    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        return DateFormat.format("dd/MM hh:mm", calendar).toString();
    }
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.confirm_delete_comment))
                .setPositiveButton(context.getString(R.string.delete), (dialog, which) -> {
                    if (context instanceof BlogDetail) {
                        Comment commentToDelete = comments.get(position);
                        ((BlogDetail) context).deleteCommentInFirestore(commentToDelete.getCommentId());
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), null)
                .create()
                .show();
    }
    private boolean isCurrentUserOrOwner(String userId, String ownerId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String currentUserId = null;
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        if (currentUserId != null) {
            return currentUserId.equals(userId) || currentUserId.equals(ownerId);
        }
        return false;
    }

}

