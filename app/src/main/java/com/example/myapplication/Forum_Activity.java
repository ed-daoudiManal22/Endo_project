package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.Comment;
import com.example.myapplication.Models.Post;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Forum_Activity extends AppCompatActivity {

    private EditText postEditText, commentEditText;
    private Button postButton, commentButton;
    private ListView postListView;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference postsReference;
    private List<Post> postList;
    private ForumAdapter forumAdapter;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        postEditText = findViewById(R.id.edit_text_post);
        commentEditText = findViewById(R.id.edit_text_comment);
        postButton = findViewById(R.id.button_post);
        commentButton = findViewById(R.id.button_comment);
        postListView = findViewById(R.id.list_view_posts);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        postsReference = database.getReference("posts");
        postList = new ArrayList<>();
        forumAdapter = new ForumAdapter(this, postList);
        postListView.setAdapter(forumAdapter);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = postEditText.getText().toString().trim();
                if (!postText.isEmpty()) {
                    createPost(postText);
                } else {
                    Toast.makeText(Forum_Activity.this, "Please enter a post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentEditText.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    createComment(commentText);
                } else {
                    Toast.makeText(Forum_Activity.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    currentUserId = firebaseAuth.getCurrentUser().getUid();
                } else {
                    currentUserId = null;
                }
            }
        });

        postsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);
                forumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
                // Handle post updates if necessary
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle post deletions if necessary
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
                // Handle post movement if necessary
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });
    }

    private void createPost(String postText) {
        String postId = postsReference.push().getKey();
        Post post = new Post(postId, postText, currentUserId);
        postsReference.child(postId).setValue(post);
        postEditText.setText("");
    }

    private void createComment(String commentText) {
        int selectedPosition = postListView.getCheckedItemPosition();
        if (selectedPosition != ListView.INVALID_POSITION) {
            Post selectedPost = postList.get(selectedPosition);
            String commentId = postsReference.child(selectedPost.getPostId()).child("comments").push().getKey();
            Comment comment = new Comment(commentId, commentText, currentUserId);
            postsReference.child(selectedPost.getPostId()).child("comments").child(commentId).setValue(comment);
            commentEditText.setText("");
            postListView.clearChoices();
            postListView.requestLayout();
        } else {
            Toast.makeText(this, "Please select a post to comment on", Toast.LENGTH_SHORT).show();
        }
    }
}
