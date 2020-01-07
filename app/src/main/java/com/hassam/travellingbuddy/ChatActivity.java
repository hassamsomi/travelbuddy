package com.hassam.travellingbuddy;

import
        androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import
        androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private TextView mDisplayUserName, mLastSeenView;
    private DatabaseReference mRef;
    private CircleImageView mProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);






        mRef = FirebaseDatabase.getInstance().getReference();

        mChatUser = getIntent().getStringExtra("chatScreen");



//----------------------Custom Action Bar Items-------------------
        mDisplayUserName = findViewById(R.id.custom_profile_name);
        mLastSeenView = findViewById(R.id.custom_user_last_seen);
        mProfileImage = findViewById(R.id.custom_profile_image);


        mRef.child("UserInfo").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String userName = dataSnapshot.child("name").getValue().toString();

                mDisplayUserName.setText(userName);
                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(mProfileImage);

                if(online.equals("true")){

                    mLastSeenView.setText("Online");

                }
                else{

                    mLastSeenView.setText(online);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
