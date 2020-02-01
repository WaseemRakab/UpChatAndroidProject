package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.media.CamcorderProfile.get;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseMessages;

    private ProgressDialog mUsersProgressShowing;//for progress showing

    private ArrayList<Users> userList;
    private HashMap<Integer, String> mHashID;

    private ListView listUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listUsers = (ListView) findViewById(R.id.usersList);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseUsers = mDatabase.getReference("Users");

        mDatabaseMessages = mDatabase.getReference("Messages");
        userList = new ArrayList<>();
        mHashID = new HashMap<>();

        mUsersProgressShowing = new ProgressDialog(this);
        mUsersProgressShowing.setTitle("Loading Users");
        mUsersProgressShowing.setMessage("Please wait.");
        mUsersProgressShowing.setCanceledOnTouchOutside(false);
        if (isConnected()) {
            mUsersProgressShowing.show();
            onStart();
        } else {
            Toast.makeText(MainActivity.this, "Please Check Your Connection", Toast.LENGTH_SHORT).show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private void getUsersDetails() {
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                Integer count = 0;//this is for list view to know which ID i have pressed based on it's name
                for (DataSnapshot usersSnapChat : dataSnapshot.getChildren()) {
                    String id = usersSnapChat.getKey();
                    if (!id.equals(mCurrentUser.getUid())) {//i wont add my self as a user in the list if im online (don't wanna talk to myself)
                        Users user = dataSnapshot.child(id).getValue(Users.class);
                        userList.add(user);
                        mHashID.put(count++, id);
                    }
                }
                listUsers.setAdapter(new UsersAdapter());
                mUsersProgressShowing.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mCurrentUser == null) {
            Intent intentSignin = new Intent(MainActivity.this, SignInPage.class);
            startActivity(intentSignin);
            finish();
        } else {
            getUsersDetails();
        }
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
            Intent signIn = new Intent(MainActivity.this, SignInPage.class);
            startActivity(signIn);
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
            Intent about = new Intent(MainActivity.this, About.class);
            startActivity(about);
            finish();
        } else if (id == R.id.nav_contact) {
            Intent contact = new Intent(MainActivity.this, Contact.class);
            startActivity(contact);
            finish();
        } else if (id == R.id.nav_profile) {
            Intent settings = new Intent(MainActivity.this, Profile.class);
            startActivity(settings);
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

    class UsersAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater ly = getLayoutInflater();

            View view = ly.inflate(R.layout.list_view_details, null);

            TextView name = view.findViewById(R.id.fullName);
            TextView email = view.findViewById(R.id.emailList);


            name.setText(userList.get(position).getName());
            email.setText(userList.get(position).getEmail());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent conversation = new Intent(MainActivity.this, Conversation.class);
                    conversation.putExtra("Receiver Name", userList.get(position).getName());
                    conversation.putExtra("Receiver ID", mHashID.get(position));
                    startActivity(conversation);
                    finish();
                }

            });
            return view;
        }
    }

}

