package com.example.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try{
                    sleep(3000);
                }
                catch(Exception e){

                }
                finally {
                    Intent main = new Intent(Splash.this,MainActivity.class);
                    startActivity(main);
                    finish();
                }
            }
        };
        thread.start();
    }
}
