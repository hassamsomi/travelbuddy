package com.hassam.travellingbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RequestFragment extends Fragment {

    private View mMainView;
    private RecyclerView mReqList;
    private FirebaseAuth mAuth;
    private String mCurrent_UserID;
    private DatabaseReference mUsersDatabase, mFriendsDatabase, mFriendRequestDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity) Objects.requireNonNull(getActivity())).changetitle("Friend Request");

        mMainView =  inflater.inflate(R.layout.fragment_request,container,false);

        mReqList = mMainView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");


        return mMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendRequestDatabase.child(mCurrent_UserID),Friends.class).build();
        FirebaseRecyclerAdapter<Friends,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Friends model) {



            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };

    }
}
