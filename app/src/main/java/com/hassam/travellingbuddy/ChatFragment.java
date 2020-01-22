package com.hassam.travellingbuddy;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ChatFragment extends Fragment {

    private RecyclerView mChatList;
    private DatabaseReference  mChatDatabase, mUsersDatabase;

    String mCurrent_UserID="";

    private FirebaseAuth mAuth;
    private View mMainView;

    public ChatFragment(){

//        REQUIRED EMPTY PUBLIC CONSTRUCTOR

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

        mChatList = mMainView.findViewById(R.id.conv_list);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mChatDatabase,Friends.class).build();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                final String usersIDs = getRef(position).getKey();
                final String[] retImage = {"default_image"};

                assert usersIDs != null;
                mUsersDatabase.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            if(dataSnapshot.hasChild("image")){

                                retImage[0] = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retImage[0]).into(holder.mImage);

                            }
                            final String retName = dataSnapshot.child("name").getValue().toString();
                            holder.mName.setText(retName);

                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("chatScreen",usersIDs);
                                startActivity(chatIntent);
                            }
                        });
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(getContext(), (CharSequence) databaseError,Toast.LENGTH_LONG).show();
                    }
                });


            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout,parent,false);
                return new FriendsViewHolder(view);
            }
        };
        mChatList.setAdapter(adapter);
        adapter.startListening();

    }

}
