package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static java.lang.Thread.sleep;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mUsername;
    private TextView mEmail;
    private EditText mConfirm;
    private EditText mPass;
    private EditText mCEmail;
    private EditText mCUsername;
    private Button mUpdate;

    //FireBase
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mUsername = (TextView) findViewById(R.id.displayName);
        mEmail = (TextView) findViewById(R.id.email);


        mConfirm = (EditText) findViewById(R.id.upConfirm);
        mPass = (EditText) findViewById(R.id.upPass);
        mCEmail = (EditText) findViewById(R.id.upEmail);
        mCUsername = (EditText) findViewById(R.id.upUser);
        mUpdate = (Button) findViewById(R.id.update);
        updateUI();

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mCEmail.getText().toString();
                String username = mCUsername.getText().toString();
                String pass = mPass.getText().toString();
                String conf = mConfirm.getText().toString();
                if (!email.isEmpty()) {
                    updateEmail(email);
                    mCEmail.setText("");
                }
                if (!username.isEmpty()) {
                    updateUsername(username);
                    mCUsername.setText("");
                }
                if (!pass.isEmpty() && !conf.isEmpty() && pass.equals(conf)) {
                    updatePassword(pass);
                    mPass.setText("");
                    mConfirm.setText("");
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateUI() {
        if (mCurrentUser != null) {
            mUsername.setText(mCurrentUser.getDisplayName());
            mEmail.setText(mCurrentUser.getEmail());
        }
    }

    private void updateEmail(final String Email) {
        mCurrentUser.updateEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Profile.this, "Email Has Been Updated !", Toast.LENGTH_SHORT).show();
                    updateDatabase(new Users(Email, mCurrentUser.getDisplayName()));
                } else {
                    Toast.makeText(Profile.this, "We Cannot Update Email for Various Reasons,try Again Later", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateUI();
    }

    private void updatePassword(String Password) {
        mCurrentUser.updatePassword(Password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Profile.this, "Password Has Been Updated !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Profile.this, "We Cannot Update Password for Various Reasons,try Again Later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUsername(final String Username) {
        mCurrentUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(Username).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Profile.this, "User Name Has Been Updated !", Toast.LENGTH_SHORT).show();
                    updateDatabase(new Users(mCurrentUser.getEmail(), Username));
                } else {
                    Toast.makeText(Profile.this, "We Cannot Update User Name for Various Reasons,try Again Later", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Profile.this, SignInPage.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            startActivity(new Intent(Profile.this, About.class));
            finish();
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(Profile.this, Contact.class));
            finish();
        } else if (id == R.id.nav_chat) {
            startActivity(new Intent(Profile.this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_quit) {
            finish();
        } else if (id == R.id.nav_share) {
            shareApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        String txtShare = "Up Chat - Chatting App Powered By FireBase";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, txtShare);
        startActivity(Intent.createChooser(share, "My Share"));
    }

    private void updateDatabase(Users users) {
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("Name", users.getName());
        usermap.put("Email", users.getEmail());
        mDatabase.getReference().child("Users").child(mCurrentUser.getUid()).setValue(usermap);
    }
}
