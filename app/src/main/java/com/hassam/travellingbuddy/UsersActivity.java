package com.hassam.travellingbuddy;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UsersActivity extends AppCompatActivity {


    private RecyclerView mUsersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mUsersList = findViewById(R.id.userslist);
        mUsersList.setHasFixedSize(true);



        mUsersList.setLayoutManager(new LinearLayoutManager(this));



    }



    public class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }
    }



}