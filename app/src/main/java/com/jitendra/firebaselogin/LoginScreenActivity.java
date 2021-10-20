package com.jitendra.firebaselogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.DataCollectionDefaultChange;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class LoginScreenActivity extends AppCompatActivity {

    private static final String TAG = "LoginScreenActivity";

    private static final int RC_SIGN_IN = 456;

    TextInputEditText userMail, userPassword;
    Button loginBtn;
    TextView registerScreen;
    ImageView googe_sign_in_Img, facebook_sign_in_Img;

    DatabaseReference reference;

    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Log.d(TAG, "onCreate: clicked login screen");

        userMail = findViewById(R.id.user_email_input_et);
        userPassword = findViewById(R.id.user_password_input_et);
        loginBtn = findViewById(R.id.login_btn);
        registerScreen = findViewById(R.id.link_to_signup_page);
        googe_sign_in_Img = findViewById(R.id.googel_sign_in);
        facebook_sign_in_Img = findViewById(R.id.facebook_sign_in);

        configureGoogleClient();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(LoginScreenActivity.this);
                pd.setMessage("Loading...");
                pd.show();

                String str_mail = userMail.getText().toString();
                String str_password = userPassword.getText().toString();

                if (TextUtils.isEmpty(str_mail) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(LoginScreenActivity.this, "All fields required!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {
                    auth.signInWithEmailAndPassword(str_mail,str_password)
                            .addOnCompleteListener(LoginScreenActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        reference = FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Users")
                                                .child(Objects.requireNonNull(auth.getCurrentUser()).getUid());

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pd.dismiss();
                                                Intent intent = new Intent(LoginScreenActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                pd.dismiss();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(LoginScreenActivity.this, "Unable to Login!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        registerScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreenActivity.this, RegisterScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        googe_sign_in_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void configureGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.revokeAccess();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "currently signed in as: "+ currentUser.getEmail());
            Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d(TAG, "Google Sign in failed "+ e.getStatusCode());
                Toast.makeText(LoginScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account ) {

            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = auth.getCurrentUser();
                                Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());
                                Toast.makeText(LoginScreenActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginScreenActivity.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(LoginScreenActivity.this, "Authentication failed:" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }
}