package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInPage extends AppCompatActivity {
    private Button mRegBtn;
    private Button mSignBtn;
    private FirebaseAuth mAuth;
    private EditText mEmail;
    private TextInputLayout mPassword;

    private ProgressDialog mLoginProgress;//for progress showing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (TextInputLayout) findViewById(R.id.password);

        mPassword.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signIn();
                    return true;
                }
                return false;
            }
        });

        /*mPassword.getEditText().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            signIn();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });*/

        mSignBtn = (Button) findViewById(R.id.signIn);
        mSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mRegBtn = (Button) findViewById(R.id.register);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(SignInPage.this, Register.class);
                startActivity(reg);
                finish();
            }
        });

    }

    private void signIn() {
        if (isConnected()) {
            String email = mEmail.getText().toString();
            String password = mPassword.getEditText().getText().toString();
            if (!email.isEmpty() && password != null && !password.isEmpty()) {
                mLoginProgress.setTitle("Logging In");
                mLoginProgress.setMessage("Please wait while we check your credentials.");
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mLoginProgress.dismiss();
                            Intent mainIntenet = new Intent(SignInPage.this, MainActivity.class);
                            mainIntenet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntenet);
                            finish();
                        } else {
                            mLoginProgress.hide();
                            Toast.makeText(SignInPage.this, "Email Or Password Not Valid!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(SignInPage.this, "One or more of the Values are empty!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignInPage.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() &&
                networkInfo.isConnected();
        return connected;
    }
}
