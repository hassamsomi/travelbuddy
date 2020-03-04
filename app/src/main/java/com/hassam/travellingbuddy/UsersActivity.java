package com.hassam.travellingbuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private FirebaseRecyclerAdapter adapter;
    private ProgressDialog mProgressDialog;
    private ImageButton mBtnSettings, mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mBtnLogout = findViewById(R.id.logout_btn);
        mBtnSettings = findViewById(R.id.btn_Settings);

        //------------RECYCLER VIEW IDENTITY
        mUsersList = findViewById(R.id.userslist);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        //------------PROGRESS DIALOG-----------
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Please wait we are loading user's list");
        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.show();


        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UsersActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UsersActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            }
        });

        //------------INSERTING DATA IN FIREBASE STORAGE
        Query query = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, new SnapshotParser<User>() {
            @NonNull
            @Override
            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new User
                        (
                                snapshot.child("name").getValue(String.class),
                                snapshot.child("image").getValue(String.class),
                                snapshot.child("aboutMe").getValue(String.class)
                        );
            }
        }).build();
        //------------HOLDER TO LOAD FILES
        adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_single_layout, parent, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
                holder.name.setText(model.name);
                Picasso.get().load(model.image).placeholder(R.drawable.profile_image).into(holder.image);
                holder.aboutMe.setText(model.aboutMe);
                mProgressDialog.dismiss();

                final String current_userID = getRef(position).getKey();

                holder.mView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent profile_intent = new Intent(UsersActivity.this, ProfileActivity.class);
                                profile_intent.putExtra("current_userID", current_userID);
                                startActivity(profile_intent);
                            }
                        });
            }
        };
        mUsersList.setAdapter(adapter);
        adapter.startListening();
    }
}