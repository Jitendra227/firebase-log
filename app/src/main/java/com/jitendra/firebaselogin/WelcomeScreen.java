package com.jitendra.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeScreen extends AppCompatActivity {

    private static final String TAG = "WelcomeScreen clicked";

    FirebaseUser firebaseUser;

    Button joinNowButton, signInButton;

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: welcome: onStart func.....");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Log.d(TAG, "onCreate: welcome clicked");

        joinNowButton = findViewById(R.id.join_btn);
        signInButton = findViewById(R.id.sign_in_button);


        //button calls.................................
        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinNowIntent = new Intent(WelcomeScreen.this, RegisterScreenActivity.class);
                startActivity(joinNowIntent);
                finish();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(WelcomeScreen.this, LoginScreenActivity.class);
                startActivity(signInIntent);
                finish();
            }
        });

    }
}
