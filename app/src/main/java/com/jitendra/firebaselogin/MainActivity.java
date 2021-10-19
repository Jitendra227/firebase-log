package com.jitendra.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity clicked";
    private FirebaseAuth mAuth;
    private TextView emailText;
    private Button logout_btn;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            //some one logged in..
            emailText.setText(mAuth.getUid());
            Log.d(TAG, "onStart: someone logged in....");
        } else {
            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailText = findViewById(R.id.userinfo);
        logout_btn = findViewById(R.id.logout_btn);

        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "onCreate: mainactivity clicked");
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.w(TAG, "onComplete: signed out of google" );
                    }
                });
                Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
                startActivity(intent);
                finish();
            }
        });

    }

}