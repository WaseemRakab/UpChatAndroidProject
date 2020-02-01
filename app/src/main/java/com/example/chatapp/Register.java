package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mConfirm;
    private Button mCreateBtn;
    private Button mReturn;

    private ProgressDialog mRegProgress;

    //FireBase Auth
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        //FireBase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        //Android Fields
        mReturn = (Button) findViewById(R.id.sign);
        mDisplayName = (EditText) findViewById(R.id.username);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (TextInputLayout) findViewById(R.id.password);
        mConfirm = (TextInputLayout) findViewById(R.id.confirm);
        mCreateBtn = (Button) findViewById(R.id.register);


        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signRet = new Intent(Register.this, SignInPage.class);
                startActivity(signRet);
                finish();
            }
        });

        mConfirm.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerAccount();
                    return true;
                }
                return false;
            }
        });
       /* mConfirm.getEditText().setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });*/

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccount();
            }
        });
    }

    private void registerAccount() {
        if (isConnected()) {
            final String user_name = mDisplayName.getText().toString();
            final String email = mEmail.getText().toString();
            String password = mPassword.getEditText().getText().toString();
            String confirm = mConfirm.getEditText().getText().toString();
            if (!user_name.isEmpty() && !email.isEmpty() && password != null && confirm != null
                    && !password.isEmpty() && !confirm.isEmpty()) {
                if (password.equals(confirm)) {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please Wait while we create your account!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mRegProgress.dismiss();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(user_name).build();
                                        mCurrentUser = mAuth.getCurrentUser();
                                        mCurrentUser.updateProfile(profileUpdates);

                                        updateDatabase(new Users(email, user_name));

                                        Intent mainIntenet = new Intent(Register.this, SignInPage.class);
                                        mainIntenet.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntenet);
                                        finish();
                                    } else {
                                        mRegProgress.hide();
                                        Toast.makeText(Register.this, "Email Not Valid Or Password Must be at least 6 Characters", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Register.this, "Password Must Match", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Register.this, "All Values are Required!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Register.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabase(Users users) {
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("Name", users.getName());
        usermap.put("Email", users.getEmail());
        mDatabase.getReference().child("Users").child(mCurrentUser.getUid()).setValue(usermap);
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
