package com.hassam.travellingbuddy;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import de.hdodenhof.circleimageview.CircleImageView;

class ConvViewHolder extends RecyclerView.ViewHolder
{
    View mView;

    TextView userStatusView,userNameView,lastSeenTime;
    CircleImageView userImageView;
    ImageView userOnlineView,camera,mic,location;

    ConvViewHolder(@NonNull View itemView)
    {
        super(itemView);
        mView = itemView;

        userNameView = itemView.findViewById(R.id.username);
        userStatusView = itemView.findViewById(R.id.status);
        userImageView = itemView.findViewById(R.id.profileimage);
        userOnlineView = itemView.findViewById(R.id.onlinePNG);
        lastSeenTime = itemView.findViewById(R.id.lastSeen);
        camera = itemView.findViewById(R.id.camera);
        camera.setVisibility(View.GONE);
        mic = itemView.findViewById(R.id.mic);
        mic.setVisibility(View.GONE);
        location = itemView.findViewById(R.id.location);
        location.setVisibility(View.GONE);
    }
    public void setMessage(String message, boolean isSeen)
    {
        userStatusView.setText(message);
        if(!isSeen)
        {
            userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
        }
        else
        {
            userStatusView.setTypeface(userStatusView.getTypeface(),Typeface.NORMAL);
        }
    }
    public void setUserOnline(String online_status)
    {
        if(online_status.equals("true"))
        {
            userOnlineView.setVisibility(View.VISIBLE);
        }
        else
        {
            userOnlineView.setVisibility(View.GONE);
        }
    }
}