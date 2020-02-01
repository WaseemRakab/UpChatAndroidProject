package com.example.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Contact extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //FireBase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mMessage;
    private EditText mFirstName;
    private EditText mLastName;
    private Button mSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mSend = (Button) findViewById(R.id.send);
        mMessage = (EditText) findViewById(R.id.msg);
        mFirstName = (EditText) findViewById(R.id.fName);
        mLastName = (EditText) findViewById(R.id.lName);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentUser != null) {
                    if (!mMessage.getText().toString().isEmpty() && !mFirstName.getText().toString().isEmpty()
                            && !mLastName.getText().toString().isEmpty()) {
                        String name = mFirstName.getText().toString() + " " + mLastName.getText().toString();
                        String message = mMessage.getText().toString();
                        mMessage.setText("");
                        mFirstName.setText("");
                        mLastName.setText("");

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(//sending email via Gmail App
                                "mailto", "sc12@hotmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feed Back");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "From " + name + "\n" + message);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));

                        Toast.makeText(Contact.this, "You are Redirected to your Default Email App", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Contact.this, "All Values Are Required !", Toast.LENGTH_SHORT).show();
                    }
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
            mAuth.signOut();
            startActivity(new Intent(Contact.this, SignInPage.class));
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

        if (id == R.id.nav_chat) {
            startActivity(new Intent(Contact.this, MainActivity.class));
            finish();
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(Contact.this, About.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(Contact.this, Profile.class));
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
}
