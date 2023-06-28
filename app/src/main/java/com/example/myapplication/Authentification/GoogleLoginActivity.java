package com.example.myapplication.Authentification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.WelcomeActivity;
import com.google.android.gms.common.SignInButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import javax.annotation.Nullable;

public class GoogleLoginActivity extends AppCompatActivity {
    private Button regularLoginButton;
    private SignInButton googleSignInButton;
    private GoogleSignInClient client;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_with_google);

        googleSignInButton = findViewById(R.id.googleSignInButton);
        regularLoginButton = findViewById(R.id.regularLoginButton);
        auth = FirebaseAuth.getInstance();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 123){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            Users user1 = new Users();
                            assert user != null;
                            user1.setUserId(user.getUid());
                            user1.setUserName(user.getDisplayName());
                            database.getReference().child("Users").child(user.getUid()).setValue(user1);
                            Intent intent = new Intent(GoogleLoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(GoogleLoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (ApiException e){
                e.printStackTrace();
            }
        }
    }
}
