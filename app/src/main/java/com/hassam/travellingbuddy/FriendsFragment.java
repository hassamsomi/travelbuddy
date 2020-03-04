package com.hassam.travellingbuddy;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private TextView mName;
    private CircleImageView mImage;
    private Context context = getContext();
    private String current_userID;
    private View mMainView;

    public FriendsFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).changetitle("Friends");
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friendlist);
        mAuth = FirebaseAuth.getInstance();

        current_userID = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_userID);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        final FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendsDatabase, Friends.class).build();
        final FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friends model) {
                final String friends_list_id = getRef(position).getKey();
//
//                if (friends_list_id != null) {
//                    DatabaseReference mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(current_userID).child(friends_list_id);
//                    mChatDatabase.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                            boolean seen = (boolean) dataSnapshot.child("seen").getValue();
//                            if (!seen) {
//
//
//                                String channelID = "com.hassam.travellingbuddy#messageChannel";
//
//
//                                String msg = "You have a new message";
//
//                                Intent intent = new Intent(getContext(), ChatActivity.class);
//                                intent.putExtra("chatScreen", friends_list_id);
//                                startActivity(intent);
//
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                                PendingIntent intent1 = PendingIntent.getActivity(getContext(), 0, intent, 0);
//
//
//                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
//                                builder.setSmallIcon(R.drawable.communication)
//                                        .setContentTitle("New Message")
//                                        .setContentText(msg)
//                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                                        .setContentIntent(intent1);
//
//                                int notificationID = 1;
//
//                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//                                notificationManager.notify(notificationID, builder.build());
//
//
//                            }

//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });


//                }


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i == 0) {

                                    Intent profile_intent = new Intent(getContext(), ProfileActivity.class);
                                    profile_intent.putExtra("current_userID", friends_list_id);
                                    startActivity(profile_intent);
                                }
                                if (i == 1) {
                                    final Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("chatScreen", friends_list_id);
                                    startActivity(chatIntent);
                                }
                            }
                        });
                        builder.show();
                    }
                });
                mFriendsDatabase.child(friends_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("date")) {
                            String addedDate = dataSnapshot.child("date").getValue().toString();
                            holder.mAboutMe.setText(addedDate);
                            holder.mOnline.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.mAboutMe.setText(model.date);
                mUsersDatabase.child(friends_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.hasChild("name")) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userImage = dataSnapshot.child("image").getValue().toString();
                                holder.mName.setText(userName);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.mImage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (dataSnapshot.hasChild("online")) {
                                String onlineState = dataSnapshot.child("online").getValue().toString();
                                try {
                                    if (onlineState.equals("true")) {
                                        holder.mOnline.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.mOnline.setVisibility(View.INVISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.mOnline.setVisibility(View.INVISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.mOnline.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                FriendsViewHolder holder = new FriendsViewHolder(view);
                holder.mOnline.setVisibility(View.INVISIBLE);
                return holder;
            }
        };
        mFriendsList.setAdapter(adapter);
        adapter.startListening();
        return mMainView;
    }



}