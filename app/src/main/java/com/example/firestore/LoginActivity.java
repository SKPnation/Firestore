package com.example.firestore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //views
    EditText mEmailEt, mPasswordEt;
    Button mLoginBtn;
    TextView mRecoverPassTv, notHaveAcctTv;

    ProgressDialog pd;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Now the listener will be actively listening for changes in the authentication state
        setupFirebaseAuth();

        pd = new ProgressDialog(this);

        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRecoverPassTv = findViewById(R.id.recoverPassTv);
        notHaveAcctTv = findViewById(R.id.notHave_accountTv);

        notHaveAcctTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showRecoverPasswordDialog();
            }
        });

       /* mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });*/
       mLoginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pd.setMessage("Logging User...");
               pd.show();

               //check for null valued editText fields
               if (!TextUtils.isEmpty( mEmailEt.getText().toString() ) &&
                       !TextUtils.isEmpty( mPasswordEt.getText().toString() ))
               {
                   //Initiate Firebase Auth
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailEt.getText().toString(),
                            mPasswordEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pd.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText( LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT ).show();
                        }
                    });
               }
               else {
                   pd.dismiss();
                   mEmailEt.setError( "Both fields are required" );
                   mPasswordEt.setError( "Both fields are required" );
                   mEmailEt.requestFocus();
                   mPasswordEt.requestFocus();
                   return;
               }
           }
       });
    }

    private void setupFirebaseAuth()
    {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());
                    Toast.makeText( LoginActivity.this, "Authenticated with: "+ user.getEmail(),
                            Toast.LENGTH_SHORT ).show();

                    Intent intent = new Intent( LoginActivity.this, DashboardActivity.class );
                    startActivity( intent );
                    finish();

                } else {
                    Log.d( TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }

    //Everything you need to use the authStateListener Object
    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }
}
