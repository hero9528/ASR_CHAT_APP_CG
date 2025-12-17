package com.funo.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcome_text);
        firebaseManager = FirebaseManager.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            firebaseManager.getUser(currentUser.getUid(), new FirebaseManager.OnUserRetrievedListener() {
                @Override
                public void onUserRetrieved(UserModel userModel) {
                    if (userModel != null) {
                        welcomeText.setText("Welcome, " + userModel.getUsername() + "!");
                    }
                }

                @Override
                public void onUserNotFound() {
                    welcomeText.setText("Welcome!");
                }

                @Override
                public void onError(String message) {
                    welcomeText.setText("Welcome!");
                }
            });
        } else {
            welcomeText.setText("Welcome!");
        }
    }
}