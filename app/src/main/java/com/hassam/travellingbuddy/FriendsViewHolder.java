package com.hassam.travellingbuddy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

class FriendsViewHolder extends RecyclerView.ViewHolder{

    View mView;
    TextView mAboutMe;


    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        mAboutMe = itemView.findViewById(R.id.status);

    }

}
