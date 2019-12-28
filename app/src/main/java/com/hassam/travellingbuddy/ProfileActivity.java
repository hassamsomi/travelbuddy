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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {


    private TextView mProfileName,mProfileStatus,mProfileFriends;
    private Button mBtnSendReq;
    private DatabaseReference mUsersDatabase;
    private String mCurrentState;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mFriendReqDatabase;
    private ImageView mProfileImage;
    private DatabaseReference mFriendDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //------------------PROGRESS DIALOG---------------
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Please wait we are loading user's list");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        final String current_userID = getIntent().getStringExtra("current_userID");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mCurrentState = "not_friends";


        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriends = findViewById(R.id.profile_totalFriends);
        mBtnSendReq = findViewById(R.id.btnSendReq);
        mProfileImage = findViewById(R.id.profile_image);








        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String profile_name = dataSnapshot.child("name").getValue().toString();
                String profile_image = dataSnapshot.child("image").getValue().toString();
                String user_status = dataSnapshot.child("aboutMe").getValue().toString();

                mProfileName.setText(profile_name);
                Picasso.get().load(profile_image).placeholder(R.drawable.profile_avatar).into(mProfileImage);
                mProfileStatus.setText(user_status);


                //-----------------------------FRIEND LIST FEATURE--------------------------
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(current_userID)){

                            String req_type = dataSnapshot.child(current_userID).child("request_type").getValue().toString();

                            if(req_type.equals("Received")){

                                mCurrentState = "req_received";
                                mBtnSendReq.setText("Accept Friend Request");

                            }else if(req_type.equals("Sent")){

                                mCurrentState = "req_sent";
                                mBtnSendReq.setText("Cancel Friend Request");

                            }
                            mProgressDialog.dismiss();
                        }
                        else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(current_userID)){

                                        mCurrentState = "friends";
                                        mBtnSendReq.setText("Unfriend This Person");


                                    }

                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //---------------------NOT FRIEND STATE-----------------------

        mBtnSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBtnSendReq.setEnabled(false);

                if (mCurrentState.equals("not_friends")) {

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(current_userID).child("request_type")
                            .setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mFriendReqDatabase.child(current_userID).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("Received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrentState = "req_sent";
                                        mBtnSendReq.setText("Cancel Friend Request");
                                        Toast.makeText(ProfileActivity.this, "Request Sent Successfully.", Toast.LENGTH_LONG).show();

                                    }
                                });


                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request.", Toast.LENGTH_LONG).show();
                            }
                            mBtnSendReq.setEnabled(true);
                        }
                    });

                }
                //---------------------CANCEL FRIEND REQUEST STATE----------------------------

                if (mCurrentState.equals("req_sent")) {
                    mFriendReqDatabase.child(current_userID).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendReqDatabase.child(mCurrentUser.getUid()).child(current_userID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mBtnSendReq.setEnabled(true);
                                    mCurrentState = "not_friends";
                                    mBtnSendReq.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }
                //---------------REQUEST RECEIVED STATE----------------------

                if (mCurrentState.equals("req_received")) {

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

                    mFriendDatabase.child(current_userID).child(mCurrentUser.getUid()).setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendDatabase.child(mCurrentUser.getUid()).child(current_userID).
                                    setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDatabase.child(current_userID).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mFriendReqDatabase.child(mCurrentUser.getUid()).child(current_userID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mBtnSendReq.setEnabled(true);
                                                    mCurrentState = "friends";
                                                    mBtnSendReq.setText("Unfriend This Person");

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
