package com.example.myapplication.Community;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Comment;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityBlogDeatailBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlogDetail extends AppCompatActivity {
    ActivityBlogDeatailBinding binding;
    private CommentsAdapter commentsAdapter;
    String id;
    String  title, desc,count;
    int n_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlogDeatailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showdata();

        binding.postDetailAddCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = binding.postDetailComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    addCommentToFirestore(commentText);
                }
            }
        });
    }

    private void showdata() {
        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Comment> commentsList = new ArrayList<>();
        // Initialize the commentsAdapter
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsAdapter);

        id = getIntent().getStringExtra("id");
        FirebaseFirestore.getInstance().collection("Blogs").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Glide.with(getApplicationContext()).load(value.getString("img")).into(binding.imageView3);
                binding.textView4.setText(Html.fromHtml("<font color='B7B7B7'>By </font> <font color='#000000'>"+value.getString("author")));
                binding.textView5.setText(value.getString("tittle"));
                binding.textView6.setText(value.getString("desc"));
                title= value.getString("tittle");
                desc= value.getString("desc");
                count= value.getString("share_count");

                int i_count=Integer.parseInt(count);
                n_count=i_count+1;
            }
        });
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareBody = desc;
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, title);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent,"Share Using"));

                HashMap<String,Object> map = new HashMap<>();
                map.put("share_count", String.valueOf(n_count));
                FirebaseFirestore.getInstance().collection("Blogs").document(id).update(map);
            }
        });
        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Fetch the comments from the subcollection
        CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                .collection("Blogs")
                .document(id)
                .collection("Comments");

        commentsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    return;
                }

                // Clear the previous comments and add the new ones
                commentsList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Comment comment = doc.toObject(Comment.class);
                    commentsList.add(comment);
                }

                // Notify the adapter that the data has changed
                commentsAdapter.notifyDataSetChanged();  // Use your CommentsAdapter instance here
            }
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
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");
                            String userImage = documentSnapshot.getString("imageUrl");

                            // Get a reference to the "Comments" subcollection of the current blog post
                            CollectionReference commentsCollection = FirebaseFirestore.getInstance()
                                    .collection("Blogs")
                                    .document(id)
                                    .collection("Comments");

                            // Create a new comment document with the user's comment and user information
                            commentsCollection.add(new Comment(commentText, userId, userName, userImage, Timestamp.now()))
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Comment added successfully, you can show a toast or perform other actions
                                            Toast.makeText(BlogDetail.this, "Comment added!", Toast.LENGTH_SHORT).show();
                                            // Clear the comment text field
                                            binding.postDetailComment.setText("");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure, show a toast or perform other error handling
                                            Toast.makeText(BlogDetail.this, "Failed to add comment.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

}
