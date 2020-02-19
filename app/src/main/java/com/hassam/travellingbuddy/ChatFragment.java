package com.hassam.travellingbuddy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment
{
    private RecyclerView mChatList;
    private DatabaseReference  mChatDatabase, mUsersDatabase, mConvDatabase,mMessageDatabase;
    String mCurrent_UserID="";

    private SinchClient sinchClient;
    private Call call;

    private FirebaseAuth mAuth;
    private View mMainView;
    String currentUser;
    public Context context = this.getContext();


    public ChatFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ( (HomeActivity) Objects.requireNonNull(getActivity())).changetitle("Chat");

        mMainView =  inflater.inflate(R.layout.fragment_chat,container,false);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_UserID);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_UserID);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_UserID);


        mConvDatabase.keepSynced(true);
        mUsersDatabase.keepSynced(true);
        mChatList = mMainView.findViewById(R.id.conv_list);




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

//



        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
        FirebaseRecyclerOptions<Conv> options1 = new FirebaseRecyclerOptions.Builder<Conv>().setQuery(conversationQuery,Conv.class).build();
        FirebaseRecyclerAdapter<Conv,ConvViewHolder> adapter1 = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final ConvViewHolder holder, int position, @NonNull final Conv model)
            {
                final String user_list_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                        chatIntent.putExtra("chatScreen",user_list_id);
                        startActivity(chatIntent);

                    }
                });
                assert user_list_id != null;
                Query lastMessageQuery = mMessageDatabase.child(user_list_id).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        String type = dataSnapshot.child("type").getValue().toString().toLowerCase();
                        holder.camera.setVisibility(View.GONE);
                        if(type.equals("image"))
                        {
                            holder.camera.setVisibility(View.VISIBLE);
                            holder.userStatusView.setText("Photo");
                        }
                        else if(type.equals("con"))
                        {
                            holder.mic.setVisibility(View.VISIBLE);
                            holder.userStatusView.setText("Voice Message");
                        }
                        else
                        {
                            holder.camera.setVisibility(View.GONE);
                            String data = dataSnapshot.child("message").getValue().toString();
                            holder.setMessage(data, model.isSeen());
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

//                holder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//                        CharSequence option[] = new CharSequence[] {"Send Message","Call"};
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
//                                .setTitle("Pick One");
//                        builder.setItems(option, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                if(i==1)
//                                {
//
//
//                                }
//                                else if(i==2)
//                                {
//
//
//                sinchClient = Sinch.getSinchClientBuilder().context(context).applicationKey("")
//                        .applicationSecret("").environmentHost("clientapi.sinch.com").userId(mCurrent_UserID)
//                        .build();
//
//
//                sinchClient.setSupportCalling(true);
//                sinchClient.startListeningOnActiveConnection();
//
//
//
//                class SinchCallListener implements CallListener
//                {
//
//
//                    @Override
//                    public void onCallProgressing(Call call) {
//
//                        Snackbar.make(mMainView,"Ringing...",Snackbar.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onCallEstablished(Call call) {
//
//                        Snackbar.make(mMainView,"Call Established",Snackbar.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onCallEnded(Call endcall) {
//
//                        Snackbar.make(mMainView,"Call Ended",Snackbar.LENGTH_LONG).show();
//                        call = null;
//                        endcall.hangup();
//
//                    }
//
//                    @Override
//                    public void onShouldSendPushNotification(Call call, List<PushPair> list) {
//
//
//
//                    }
//                }
//
//                sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
//                    @Override
//                    public void onIncomingCall(CallClient callClient, Call call) {
//
//
//
//                    }
//                });
//
//                sinchClient.start();
//
//
//
//                                }
//
//                            }
//                        });
//
//
//                    }
//                });
                mUsersDatabase.child(user_list_id).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild("image"))
                        {
                            String name = dataSnapshot.child("name").getValue().toString();
                            holder.userNameView.setText(name);
                            String image = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.userImageView);
                            if(dataSnapshot.hasChild("online"))
                            {
                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                holder.setUserOnline(userOnline);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(mMainView.getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                mConvDatabase.child(user_list_id).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String online = dataSnapshot.child("seen").getValue().toString();
                        if(online.equals("seen"))
                        {
                            long lastTime = Long.parseLong(online);
                            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime,getContext());
                            holder.lastSeenTime.setText(lastSeenTime);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }

                });
            }
            @NonNull
            @Override
            public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
               View view = getLayoutInflater().from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);

                return new ConvViewHolder(view);
            }
        };

        mChatList.setAdapter(adapter1);
        adapter1.startListening();
    }
}