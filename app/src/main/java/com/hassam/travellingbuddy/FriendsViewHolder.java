package com.hassam.travellingbuddy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

class FriendsViewHolder extends RecyclerView.ViewHolder{

    View mView;

    TextView name;
    TextView aboutMe;
    CircleImageView image;

    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        name = itemView.findViewById(R.id.username);
        aboutMe = itemView.findViewById(R.id.status);
        image = itemView.findViewById(R.id.profileimage);
    }
}
