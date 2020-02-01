package com.example.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Conversation extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mCurrentUser;
    private String mCurrentUserID;
    private DatabaseReference databaseMessages;

    private LinearLayout layout;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        final String chattingWithID = getIntent().getExtras().get("Receiver ID").toString();
        final String chattingWithName = getIntent().getExtras().get("Receiver Name").toString();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chatting with " + chattingWithName);

        layout = (LinearLayout) findViewById(R.id.layout1);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mCurrentUserID = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance();
        databaseMessages = mDatabase.getReference("Messages");

        readMessages(chattingWithID);

        messageArea.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendMessage(chattingWithID);
                    return true;
                }
                return false;
            }
        });

        /*messageArea.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            sendMessage(chattingWithID);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });*/

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chattingWithID);
            }
        });
    }

    private void sendMessage(String chattingWith) {
        String message = messageArea.getText().toString();
        if (!message.isEmpty()) {
            Messages messages = new Messages(chattingWith, message);
            addToDataBase(messages);
            messageArea.setText("");
        } else {
            Toast.makeText(Conversation.this, "Enter A message!", Toast.LENGTH_SHORT).show();
        }
    }

    private void readMessages(final String chattingWithID) {
        databaseMessages.child(mCurrentUserID).child(chattingWithID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        if (messages.getFrom().equals(mCurrentUserID)) {//this was my message
                            addMessageBox(messages.getMessage(), 1);
                        } else {
                            addMessageBox(messages.getMessage(), 2);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void addToDataBase(Messages messages) {
        if (mCurrentUser != null) {
            databaseMessages.child(mCurrentUserID).child(messages.getFrom()).push().setValue(messages);
            databaseMessages.child(messages.getFrom()).child(mCurrentUserID).push().setValue(messages);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent mReturn = new Intent(Conversation.this, MainActivity.class);
            startActivity(mReturn);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Conversation.this);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {//me sending the message
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        } else {//from
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        textView.setMaxWidth(500);
        textView.setLayoutParams(lp2);
        textView.setText(message);
        layout.addView(textView);
        scrollView.postDelayed(new Runnable() {//this is for chat messages to show the last in bottm
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 1000);
    }
}
