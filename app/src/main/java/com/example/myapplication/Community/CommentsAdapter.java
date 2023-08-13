package com.example.myapplication.Community;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> comments;
    private String blogId;

    public CommentsAdapter(Context context, List<Comment> comments,String blogId) {
        this.context = context;
        this.comments = comments;
        this.blogId = blogId;
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

        TextView commentText, comment_username, comment_date;
        ImageView user_img ;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_content);
            user_img = itemView.findViewById(R.id.comment_user_img);
            comment_username = itemView.findViewById(R.id.comment_username);
            comment_date = itemView.findViewById(R.id.comment_date);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteDialog(getAdapterPosition());
                    return true;
                }
            });
        }
    }
    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM hh:mm", calendar).toString();
        return date;
    }
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete this comment?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context instanceof BlogDetail) {
                            Comment commentToDelete = comments.get(position);
                            ((BlogDetail) context).deleteCommentInFirestore(commentToDelete.getCommentId());
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void deleteComment(String commentId) {
        // Get a reference to the "Comments" subcollection of the current blog post
        CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                .collection("Blogs")
                .document(blogId) // Use the stored blogId here
                .collection("Comments");

        // Delete the comment document from Firestore
        commentsCollection.document(commentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Comment deleted successfully from Firestore
                        // You may need to refresh the comments by re-fetching them from Firestore
                        refreshComments();
                        Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure, show a toast or perform other error handling
                        Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void refreshComments() {
        // Get a reference to the "Comments" subcollection of the current blog post
        CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                .collection("Blogs")
                .document(blogId) // Use the stored blogId here
                .collection("Comments");

        commentsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Clear the previous comments and add the new ones
                        comments.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String commentId = doc.getId(); // Get the comment ID from Firestore
                            Comment comment = doc.toObject(Comment.class);
                            comment.setCommentId(commentId); // Set the comment ID in the Comment object
                            comments.add(comment);
                        }

                        // Notify the adapter that the data has changed
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure, show a toast or perform other error handling
                        Toast.makeText(context, "Failed to refresh comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

