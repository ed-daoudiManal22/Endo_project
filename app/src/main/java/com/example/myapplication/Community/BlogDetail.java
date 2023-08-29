package com.example.myapplication.Community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityBlogDeatailBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlogDetail extends AppCompatActivity {
    ActivityBlogDeatailBinding binding;
    private CommentsAdapter commentsAdapter;
    String id, ownerId;
    String title, desc, count;
    int n_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlogDeatailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showdata();

        binding.postDetailAddCommentBtn.setOnClickListener(v -> {
            String commentText = binding.postDetailComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addCommentToFirestore(commentText);
            }
        });

        binding.imageView3.setOnClickListener(v -> showImagePreviewDialog());
    }

    private void showdata() {
        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Comment> commentsList = new ArrayList<>();
        // Initialize the commentsAdapter
        commentsAdapter = new CommentsAdapter(this, commentsList, id);
        commentsRecyclerView.setAdapter(commentsAdapter);


        id = getIntent().getStringExtra("id");
        FirebaseFirestore.getInstance().collection("Blogs").document(id).addSnapshotListener((value, error) -> {
            Glide.with(getApplicationContext()).load(value.getString("img")).into(binding.imageView3);
            binding.textView4.setText(Html.fromHtml("<font color='B7B7B7'>By </font> <font color='#000000'>" + value.getString("author")));
            binding.textView5.setText(value.getString("tittle"));
            binding.textView6.setText(value.getString("desc"));
            title = value.getString("tittle");
            desc = value.getString("desc");
            count = value.getString("share_count");
            ownerId = value.getString("ownerId");
            int i_count = Integer.parseInt(count);
            n_count = i_count + 1;
        });
        binding.floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String shareBody = desc;
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent, "Share Using"));

            HashMap<String, Object> map = new HashMap<>();
            map.put("share_count", String.valueOf(n_count));
            FirebaseFirestore.getInstance().collection("Blogs").document(id).update(map);
        });
        binding.imageView4.setOnClickListener(v -> onBackPressed());
        // Fetch the comments from the subcollection, sorted by timestamp
        CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                .collection("Blogs")
                .document(id)
                .collection("Comments");

        commentsCollection.orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp in descending order
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle the error
                        return;
                    }

                    // Clear the previous comments and add the new ones
                    commentsList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        String commentId = doc.getId(); // Get the comment ID from Firestore
                        Comment comment = doc.toObject(Comment.class);
                        comment.setCommentId(commentId); // Set the comment ID in the Comment object
                        commentsList.add(comment);
                    }


                    // Notify the adapter that the data has changed
                    commentsAdapter.notifyDataSetChanged();  // Use your CommentsAdapter instance here
                });
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void addCommentToFirestore(String commentText) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // Fetch user information from the "Users" collection
        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("name");
                        String userImage = documentSnapshot.getString("imageUrl");

                        // Get a reference to the "Comments" subcollection of the current blog post
                        CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                                .collection("Blogs")
                                .document(id)
                                .collection("Comments");

                        // Create a new comment document with the user's comment and user information
                        String commentId = commentsCollection.document().getId(); // Generate a new comment ID
                        commentsCollection.add(new Comment(commentId, commentText, userId, userName, userImage, Timestamp.now()))
                                .addOnSuccessListener(documentReference -> {
                                    // Comment added successfully, you can show a toast or perform other actions
                                    Toast.makeText(BlogDetail.this, "Comment added!", Toast.LENGTH_SHORT).show();
                                    // Clear the comment text field
                                    binding.postDetailComment.setText("");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure, show a toast or perform other error handling
                                    Toast.makeText(BlogDetail.this, "Failed to add comment.", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    public void deleteCommentInFirestore(String commentId) {
        // Get a reference to the "Comments" subcollection of the current blog post
        CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                .collection("Blogs")
                .document(id)
                .collection("Comments");

        // Delete the comment document from Firestore
        commentsCollection.document(commentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Comment deleted successfully from Firestore
                    Toast.makeText(BlogDetail.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure, show a toast or perform other error handling
                    Toast.makeText(BlogDetail.this, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                });
    }

    private void showImagePreviewDialog() {
        FirebaseFirestore.getInstance().collection("Blogs").document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String imageUrl = documentSnapshot.getString("img");

                    Dialog imagePreviewDialog = new Dialog(this);
                    imagePreviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    imagePreviewDialog.setContentView(R.layout.dialog_image_preview);

                    ImageView previewImageView = imagePreviewDialog.findViewById(R.id.previewImageView);

                    // Load the image using the URL fetched from the database
                    Glide.with(this).load(imageUrl).into(previewImageView);

                    // Make the dialog window full screen
                    Window window = imagePreviewDialog.getWindow();
                    if (window != null) {
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                    }

                    // Close the dialog when the user clicks anywhere outside the image
                    imagePreviewDialog.setCancelable(true);
                    imagePreviewDialog.setCanceledOnTouchOutside(true);

                    // Show the dialog
                    imagePreviewDialog.show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure, show a toast or perform other error handling
                    Toast.makeText(BlogDetail.this, "Failed to fetch image data.", Toast.LENGTH_SHORT).show();
                });
    }
}
