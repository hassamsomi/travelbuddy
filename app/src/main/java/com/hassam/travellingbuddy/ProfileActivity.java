package com.hassam.travellingbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriends;
    private Button mBtnSendReq;
    private DatabaseReference mUsersDatabase;
    private ProgressDialog mProgressDialog;
    private int mCurrentState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String current_userID = getIntent().getStringExtra("current_userID");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID);

        mCurrentState = 0;


        mProfileImage = findViewById(R.id.profileimage);
        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriends = findViewById(R.id.profile_totalFriends);
        mBtnSendReq = findViewById(R.id.btnSendReq);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String profile_name = dataSnapshot.child("name").getValue().toString();
                String profile_image = dataSnapshot.child("image").getValue().toString();
                String user_status = dataSnapshot.child("aboutMe").getValue().toString();

                mProfileName.setText(profile_name);
                Picasso.get().load(profile_image).placeholder(R.drawable.profile_image).into(mProfileImage);
                mProfileStatus.setText(user_status);
                mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        mBtnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(mCurrentState == 0){

                }



            }
        });



    }
}
