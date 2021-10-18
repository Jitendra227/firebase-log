package com.jitendra.firebaselogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterScreenActivity extends AppCompatActivity {

    private static final String TAG = "RegisterScreenActivity";

    EditText userNameEditText, emailEditText, passwordEditText, rePasswordEditText;
    private Button registerBtn;
    String str_userName, str_userMail, str_password, str_rePassword;

    private FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        Log.d(TAG, "onCreate: register screen entered.....");

        userNameEditText = findViewById(R.id.user_name_input);
        emailEditText = findViewById(R.id.user_email_input);
        passwordEditText = findViewById(R.id.user_password_input);
        registerBtn = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
        
        registerBtn.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(RegisterScreenActivity.this);
            progressDialog.setMessage("Please wait....");
            progressDialog.show();

            str_userName = userNameEditText.getText().toString();
            str_userMail = emailEditText.getText().toString();
            str_password = passwordEditText.getText().toString();


            RegisterScreenActivity.this.userAuthcheck();
        });

    }

    private void userAuthcheck() {
        if (TextUtils.isEmpty(str_userName) || TextUtils.isEmpty(str_userMail) ||
                TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_rePassword)) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
        } else if (str_password.length()<8) {
            progressDialog.dismiss();
            Toast.makeText(this, "password should be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(str_userName, str_userMail, str_password, str_rePassword);
        }
    }

    private void registerUser(final String userName, final String userMail, final String password, final String rePassword) {

        mAuth.createUserWithEmailAndPassword(userMail, password)
                .addOnCompleteListener(RegisterScreenActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getEmail();
                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userId);
                        hashMap.put("userName", userName.toLowerCase());
                        hashMap.put("email", userMail);
                        progressDialog.dismiss();

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();

                                    Intent intent = new Intent(RegisterScreenActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterScreenActivity.this, "Failure!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "User account already exist!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
