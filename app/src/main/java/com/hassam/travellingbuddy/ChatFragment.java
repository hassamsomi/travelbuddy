package com.hassam.travellingbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private RecyclerView mConvList;
    private DatabaseReference mConvDatabase, mMessageDatabase, mUsersDatabase;

    String mCurrent_UserID;

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

        mConvList = mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_UserID = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_UserID);

        mConvDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_UserID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        return mMainView;

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        Query conversationQuery = mConvDatabase.orderByChild("timestamp");
//
//        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mMessageDatabase,Friends.class).build();
//
//        FirebaseRecyclerAdapter<Conv,ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ConvViewHolder holder, int position, @NonNull Conv model) {
//
//                final String list_user_id = getRef(position).getKey();
//
//                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);
//
//                lastMessageQuery.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                        String data = dataSnapshot.child("message").getValue().toString();
//
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//
//            }
//
//            @NonNull
//            @Override
//            public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;
//            }
//        };
//
//
//    }
}
