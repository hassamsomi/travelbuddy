package com.hassam.travellingbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class RequestFragment extends Fragment {

    private View mMainView;
    private RecyclerView mReqList;
    private FirebaseAuth mAuth;
    private String mCurrent_UserID;
    private DatabaseReference mUsersDatabase, mFriendsDatabase, mFriendRequestDatabase, mRootRef;


    public RequestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity) Objects.requireNonNull(getActivity())).changetitle("Friend Request");

        mMainView = inflater.inflate(R.layout.fragment_request, container, false);

        mReqList = mMainView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mRootRef = FirebaseDatabase.getInstance().getReference();


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendRequestDatabase.child(mCurrent_UserID), Friends.class).build();
        FirebaseRecyclerAdapter<Friends, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Friends model) {

                holder.itemView.findViewById(R.id.btnAccept).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);

                final String list_user_id = getRef(position).getKey();
                DatabaseReference getReqType = getRef(position).child("request_type").getRef();

                getReqType.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("Received")) {

                                mUsersDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild("image")) {
                                            final String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                        }
                                        final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                        final String requestUserStatus = dataSnapshot.child("aboutMe").getValue().toString();
                                        holder.userName.setText(requestUserName);
                                        holder.userStatus.setText("Wants to connect with you.");

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]{"Accept", "Cancel"};

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestUserName + "Friend Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (i == 0) {
                                                            final String currentDate = DateFormat.getDateInstance().format(new Date());

                                                            FirebaseDatabase.getInstance().getReference().child("Friends")
                                                                    .child(mCurrent_UserID).child(list_user_id).child("date")
                                                                    .setValue(currentDate)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            FirebaseDatabase.getInstance().getReference().child("Friends")
                                                                                    .child(list_user_id).child(mCurrent_UserID)
                                                                                    .child("date").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    FirebaseDatabase.getInstance().getReference().child("Friend_Req")
                                                                                            .child(mCurrent_UserID).child(list_user_id).setValue(null)
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    FirebaseDatabase.getInstance().getReference().child("Friend_Req")
                                                                                                            .child(list_user_id).child(mCurrent_UserID).setValue(null)
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        Toast.makeText(getContext(), "New Friend Added", Toast.LENGTH_SHORT).show();
                                                                                                                    }

                                                                                                                }
                                                                                                            });

                                                                                                }
                                                                                            });

                                                                                }
                                                                            });

                                                                        }
                                                                    });

                                                        }
                                                        if (i == 1) {

                                                            Map unfriendMap = new HashMap();
                                                            unfriendMap.put("Friends/" + mCurrent_UserID + "/" + list_user_id, null);
                                                            unfriendMap.put("Friends/" + list_user_id + "/" + mCurrent_UserID, null);

                                                            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                                    if (databaseReference.equals(true)) {
                                                                        Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                    } else {

                                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else if (type.equals("Sent")) {

                                Button req_sent_btn = holder.itemView.findViewById(R.id.btnAccept);
                                req_sent_btn.setText("Send Request");

                                holder.itemView.findViewById(R.id.btnCancel).setVisibility(View.GONE);
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild("image")) {

                                            final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                            Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                        }
                                        final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                        final String requestUserStatus = dataSnapshot.child("status").getValue().toString();
                                        holder.userName.setText(requestUserName);
                                        holder.userStatus.setText("You have sent a request to" + requestUserName);

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]{"Cancel Chat Request"};
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Already Sent Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (i == 0) {
                                                            mFriendRequestDatabase.child(mCurrent_UserID).child(list_user_id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    mFriendRequestDatabase.child(list_user_id).child(mCurrent_UserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getContext(), "You have cancelled the friend request.", Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                }
                                                                            });

                                                                }
                                                            });

                                                        }


                                                    }
                                                });

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = getLayoutInflater().from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new RequestViewHolder(view);
            }
        };
        mReqList.setAdapter(adapter);
        adapter.startListening();
    }
}