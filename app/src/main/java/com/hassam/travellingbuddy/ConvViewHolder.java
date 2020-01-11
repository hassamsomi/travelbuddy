package com.hassam.travellingbuddy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

class ConvViewHolder extends RecyclerView.ViewHolder{

    View mView;

    TextView userStatusView,userNameView;
    CircleImageView userImageView;
    ImageView userOnlineView;

    public ConvViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        userNameView = itemView.findViewById(R.id.username);
        userStatusView = itemView.findViewById(R.id.status);
        userImageView = itemView.findViewById(R.id.profileimage);
        userOnlineView = itemView.findViewById(R.id.onlinePNG);

    }
}
