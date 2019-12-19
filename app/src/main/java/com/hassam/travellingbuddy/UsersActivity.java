package com.hassam.travellingbuddy;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {


    private RecyclerView mUsersList;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mUsersList = findViewById(R.id.userslist);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


        Query query = FirebaseDatabase.getInstance().getReference().child("UserInfo");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, new SnapshotParser<User>() {
            @NonNull
            @Override
            public User parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new User (

                 snapshot.child("name").getValue(String.class),
                        snapshot.child("image").getValue(String.class),
                snapshot.child("aboutMe").getValue(String.class)


                );
            }
        }).build();
        adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(options){

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.users_single_layout,parent,false);
                return new UsersViewHolder(view);



            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull User model) {
                holder.name.setText(model.name);
                Picasso.get().load(model.image).placeholder(R.drawable.profile_image).into(holder.image);
                holder.aboutMe.setText(model.aboutMe);


                final String current_userID = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
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
class UsersViewHolder extends RecyclerView.ViewHolder{

    View mView;

    TextView name;
    TextView aboutMe;
    CircleImageView image;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        name = itemView.findViewById(R.id.username);
        aboutMe = itemView.findViewById(R.id.status);
        image = itemView.findViewById(R.id.profileimage);



    }



}



