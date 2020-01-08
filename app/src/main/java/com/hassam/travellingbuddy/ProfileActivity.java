package com.hassam.travellingbuddy;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
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
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private TextView mProfileName,mProfileStatus;
    private Button mBtnSendReq;
    private DatabaseReference mFriendReqDatabase;
    private String mCurrentState;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private ImageView mProfileImage;
    private DatabaseReference mFriendDatabase;
    private Button mBtnDeclineReq;
    private DatabaseReference mRootRef;
    private DatabaseReference mNotificationDatabase;



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


        mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mCurrentState = "not_friends";


        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        TextView mProfileFriends = findViewById(R.id.profile_totalFriends);
        mBtnSendReq = findViewById(R.id.btnSendReq);
        mBtnDeclineReq = findViewById(R.id.btnDeclineReq);
        mProfileImage = findViewById(R.id.profile_image);

        mBtnDeclineReq.setVisibility(View.INVISIBLE);
        mBtnDeclineReq.setEnabled(false);








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

                                mBtnDeclineReq.setVisibility(View.VISIBLE);
                                mBtnDeclineReq.setEnabled(true);


                            }else if(req_type.equals("Sent")){

                                mCurrentState = "req_sent";
                                mBtnSendReq.setText("Cancel Friend Request");

                                mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnDeclineReq.setEnabled(false);

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

                                        mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                        mBtnDeclineReq.setEnabled(false);

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


                    DatabaseReference newNotificationRef = mRootRef.child("notification").child(current_userID).push();
                    String newNotificationID = newNotificationRef.getKey();

//                    DatabaseReference name = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID).child("name");
//                    DatabaseReference mImage = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID).child("image");

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Req/"+mCurrentUser.getUid() +"/"+current_userID+"/request_type","Sent");
                    requestMap.put("Friend_Req/"+current_userID+"/"+mCurrentUser.getUid()+"/request_type","Received");


                    requestMap.put("notification/"+current_userID+"/"+newNotificationID,notificationData);


                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ProfileActivity.this,"There was some error in sending request",Toast.LENGTH_LONG).show();

                            }

                            mBtnSendReq.setEnabled(true);
                            mCurrentState = "req_sent";
                            mBtnSendReq.setText("Cancel Friend Request");


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

                                    mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                    mBtnDeclineReq.setEnabled(false);

                                }
                            });
                        }
                    });
                }
                //---------------REQUEST RECEIVED STATE----------------------

                if (mCurrentState.equals("req_received")) {

                    final String currentDate = DateFormat.getDateInstance().format(new Date());

//                    DatabaseReference name = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID).child("name");
//                    DatabaseReference mImage = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID).child("image");



                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+current_userID+"/date",currentDate);
                    friendsMap.put("Friends/"+current_userID+"/"+mCurrentUser.getUid()+"/date",currentDate);






                    friendsMap.put("Friend_Req"+mCurrentUser.getUid()+"/"+current_userID,null);
                    friendsMap.put("Friend_Req"+current_userID+"/"+mCurrentUser.getUid(),null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mBtnSendReq.setEnabled(true);
                                mCurrentState = "friends";
                                mBtnSendReq.setText("Unfriend this person");

                                mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnDeclineReq.setEnabled(false);

                            }else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();

                            }


                        }
                    });
                }
//                ------------------------UNFRIENDS STATE-----------------
                if(mCurrentState.equals("friends")){

                    DatabaseReference name = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID).child("name");
                    DatabaseReference mImage = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID).child("image");


                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/"+current_userID,null);
                    unfriendMap.put("Friends/"+current_userID+"/"+mCurrentUser.getUid(),null);
                    unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/name",name);
                    unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/image",mImage);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mCurrentState = "not_friends";
                                mBtnSendReq.setText("Send Friend Request");

                                mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnDeclineReq.setEnabled(false);

                            }else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();

                            }
                            mBtnSendReq.setEnabled(true);


                        }
                    });

                }






            }
        });
    }
}
