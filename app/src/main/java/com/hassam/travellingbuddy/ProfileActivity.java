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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String mCurrentState;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mFriendReqDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Please wait we are loading user's list");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        final String current_userID = getIntent().getStringExtra("current_userID");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mCurrentState = "not_friends";



        mProfileImage = findViewById(R.id.profileimage);
        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriends = findViewById(R.id.profile_totalFriends);
        mBtnSendReq = findViewById(R.id.btnSendReq);





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


        mBtnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mCurrentState.equals("not_friends")){

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(current_userID).child("request_type")
                            .setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mFriendReqDatabase.child(current_userID).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(ProfileActivity.this,"Request Sent Successfully.",Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this,"Failed Sending Request.",Toast.LENGTH_LONG).show();


                            }

                        }
                    });

                }

            }
        });
    }
}