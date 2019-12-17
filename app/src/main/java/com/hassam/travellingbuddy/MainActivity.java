package com.hassam.travellingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if(currentUser == null){

                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                else {
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }




}

