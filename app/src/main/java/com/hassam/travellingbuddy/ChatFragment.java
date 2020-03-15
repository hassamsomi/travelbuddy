package com.hassam.travellingbuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ChatFragment extends Fragment {
    private RecyclerView mChatList;
    private DatabaseReference mChatDatabase, mUsersDatabase, mConvDatabase, mMessageDatabase;
    String mCurrent_UserID = "";


    private FirebaseAuth mAuth;
    private View mMainView;
    String currentUser;
    private View view;
    public Context context = this.getContext();


    public ChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity) Objects.requireNonNull(getActivity())).changetitle("Chat");

        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_UserID);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_UserID);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_UserID);

        view = mMainView.findViewById(R.id.childparent);

        mConvDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);
        mChatList = mMainView.findViewById(R.id.conv_list);


        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatList.setHasFixedSize(true);
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

//


        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
        FirebaseRecyclerOptions<Conv> options1 = new FirebaseRecyclerOptions.Builder<Conv>().setQuery(conversationQuery, Conv.class).build();
        FirebaseRecyclerAdapter<Conv, ConvViewHolder> adapter1 = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final ConvViewHolder holder, int position, @NonNull final Conv model) {
                final String user_list_id = getRef(position).getKey();
                holder.mView.setOnClickListener(view -> {


                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                    chatIntent.putExtra("chatScreen", user_list_id);
                    startActivity(chatIntent);


                });


                assert user_list_id != null;
                Query lastMessageQuery = mMessageDatabase.child(user_list_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String type = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString().toLowerCase();
                        holder.camera.setVisibility(View.GONE);
                        holder.location.setVisibility(View.GONE);
                        holder.mic.setVisibility(View.GONE);
                        if (type.equals("image")) {
                            holder.camera.setVisibility(View.VISIBLE);
                            holder.userStatusView.setText("Photo");
                        } else if (type.equals("con")) {
                            holder.mic.setVisibility(View.VISIBLE);
                            holder.userStatusView.setText("Voice Message");
                        } else if(type.equals("location")){
                            holder.location.setVisibility(View.VISIBLE);
                            holder.userStatusView.setText("Location");
                        } else if(type.equals("text")) {
                            String message = dataSnapshot.child("message").getValue().toString();
                            holder.userStatusView.setText(message);
                        } else {

                            Toast.makeText(context, "Loading Error.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                mUsersDatabase.child(user_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            holder.userNameView.setText(name);
                            String image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.userImageView);
                            if (dataSnapshot.hasChild("online")) {
                                String userOnline = Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString();
                                holder.setUserOnline(userOnline);
                                if(dataSnapshot.child("online").child("true").exists()) {
                                    holder.userOnlineView.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(view, "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
                mConvDatabase.child(user_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("seen")) {
                            String online = Objects.requireNonNull(dataSnapshot.child("seen").getValue()).toString();
                            if (online.equals("seen")) {
                                long lastTime = Long.parseLong(online);
                                String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getContext());
                                holder.lastSeenTime.setText(lastSeenTime);
                            }
                        } else {
                           Toast.makeText(getContext(),"Object not found.",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(view, "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                });
            }

            @NonNull
            @Override
            public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

                return new ConvViewHolder(view);
            }
        };

        mChatList.setAdapter(adapter1);
        adapter1.startListening();
    }
}