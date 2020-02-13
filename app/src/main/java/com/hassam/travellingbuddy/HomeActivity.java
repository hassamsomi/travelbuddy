package com.hassam.travellingbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView appBarTitle;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ChatFragment()).commit();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navlistener);

        ImageButton settings = findViewById(R.id.btn_Settings);
        ImageButton sign_out = findViewById(R.id.logout_btn);
        ImageButton btnUsers = findViewById(R.id.btn_users);
        appBarTitle = findViewById(R.id.textaccount);
        mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            }
        });
    }
    public void changetitle(String title){
        appBarTitle.setText(title);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment();
                            break;
                        case R.id.nav_friend:
                            selectedFragment = new FriendsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };
}
