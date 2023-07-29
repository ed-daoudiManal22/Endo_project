package com.example.myapplication.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.R;
import com.google.android.gms.common.SignInButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GoogleLoginActivity extends AppCompatActivity {
    private Button regularLoginButton;
    private SignInButton googleSignInButton;
    private GoogleSignInClient client;
    private FirebaseFirestore firestore;
    private static final String DEFAULT_PROFILE_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/endo-project-1acae.appspot.com/o/profile_images%2Funknown_pic.jpg?alt=media&token=41f82f66-f50e-44d3-b020-07487bedeba7";
    FirebaseAuth auth;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_google);

        googleSignInButton = findViewById(R.id.googleSignInButton);
        regularLoginButton = findViewById(R.id.regularLoginButton);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance("https://endo-project-1acae-default-rtdb.firebaseio.com/");

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, options);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i, 123);
            }
        });

        regularLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to regular login activity
                Intent intent = new Intent(GoogleLoginActivity.this, logInActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Check if the user is signing in for the first time
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                // If it's the first time, create a new Users object and store the data
                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("name",user.getDisplayName());
                                newUser.put("email",user.getEmail());
                                newUser.put("imageUrl",DEFAULT_PROFILE_IMAGE_URL);

                                // Save the user data in Firestore
                                firestore.collection("Users").document(user.getUid()).set(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Proceed to the UserPage_Activity
                                                Intent intent = new Intent(GoogleLoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GoogleLoginActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Proceed to the HomeActivity
                                Intent intent = new Intent(GoogleLoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(GoogleLoginActivity.this, "User is null.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

}
