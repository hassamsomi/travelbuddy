package com.hassam.travellingbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {


    private TextView mProfileName, mProfileStatus;
    private Button mBtnSendReq;
    private DatabaseReference mFriendReqDatabase;
    private String mCurrentState;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private ImageView mProfileImage;
    private DatabaseReference mFriendDatabase;
    private Button mBtnDeclineReq;
    private DatabaseReference mRootRef;
    private TextView mTotalFriends;
    private View layout;

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

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentState = "not_friends";
        mTotalFriends = findViewById(R.id.profile_totalFriends);

        layout = findViewById(R.id.grand_parent);
        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mBtnSendReq = findViewById(R.id.btnSendReq);
        mBtnDeclineReq = findViewById(R.id.btnDeclineReq);
        mProfileImage = findViewById(R.id.profile_image);

        mBtnDeclineReq.setVisibility(View.INVISIBLE);
        mBtnDeclineReq.setEnabled(false);

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String profile_name = dataSnapshot.child("name").getValue().toString();
                String profile_image = dataSnapshot.child("image").getValue().toString();
                String user_status = dataSnapshot.child("aboutMe").getValue().toString();

                mProfileName.setText(profile_name);
                Picasso.get().load(profile_image).placeholder(R.drawable.profile_avatar).into(mProfileImage);

                mProfileStatus.setText(user_status);

                if (mCurrentUser.getUid().equals(current_userID)) {
                    mBtnDeclineReq.setEnabled(false);
                    mBtnDeclineReq.setVisibility(View.INVISIBLE);

                    mBtnSendReq.setEnabled(false);
                    mBtnSendReq.setVisibility(View.INVISIBLE);
                }

                //-----------------------------FRIEND LIST FEATURE--------------------------
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(current_userID)) {
                            String req_type = dataSnapshot.child(current_userID).child("request_type").getValue().toString();

                            if (req_type.equals("Received")) {
                                mCurrentState = "req_received";
                                mBtnSendReq.setText("Accept Friend Request");

                                mBtnDeclineReq.setVisibility(View.VISIBLE);
                                mBtnDeclineReq.setEnabled(true);
                            } else if (req_type.equals("Sent")) {
                                mCurrentState = "req_sent";
                                mBtnSendReq.setText("Cancel Friend Request");

                                mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnDeclineReq.setEnabled(false);
                            }

                            mProgressDialog.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(current_userID)) {
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
                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Req/" + mCurrentUser.getUid() + "/" + current_userID + "/request_type", "Sent");
                    requestMap.put("Friend_Req/" + current_userID + "/" + mCurrentUser.getUid() + "/request_type", "Received");
                    requestMap.put("notification/" + current_userID + "/" + newNotificationID, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                String e = databaseError.getMessage();
                                Snackbar.make(layout, e, Snackbar.LENGTH_LONG).show();
                            } else {
                                mCurrentState = "req_sent";
                                mBtnSendReq.setText("Cancel Friend Request");
                            }
                            mBtnSendReq.setEnabled(true);
                        }
                    });
                }
                //---------------------CANCEL FRIEND REQUEST STATE----------------------------

                if (mCurrentState.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(current_userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendReqDatabase.child(current_userID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

                    FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser.getUid()).child(current_userID).child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            FirebaseDatabase.getInstance().getReference().child("Friends").child(current_userID)
                                    .child(mCurrentUser.getUid()).child("date").setValue(currentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(mCurrentUser.getUid()).child(current_userID).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(current_userID).child(mCurrentUser.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                mBtnSendReq.setEnabled(true);
                                                                mCurrentState = "friends";
                                                                mBtnSendReq.setText("Unfriend this person");

                                                                mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                                                mBtnDeclineReq.setEnabled(false);
                                                            } else {
                                                                Objects.requireNonNull(task.getException()).printStackTrace();
                                                                Snackbar.make(layout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                            }

                                                        }
                                                    });

                                                }
                                            });

                                        }
                                    });

                        }
                    });

                    String totalFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser.getUid()).child(current_userID).getRef().getKey();
                    mTotalFriends.setText(totalFriends);
                }
//                ------------------------UNFRIENDS STATE-----------------
                if (mCurrentState.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + current_userID, null);
                    unfriendMap.put("Friends/" + current_userID + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrentState = "not_friends";
                                mBtnSendReq.setText("Send Friend Request");

                                mBtnDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnDeclineReq.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                            }
                            mBtnSendReq.setEnabled(true);
                        }
                    });
                }
            }
        });
    }
}
