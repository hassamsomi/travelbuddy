package com.hassam.travellingbuddy;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

class ConvViewHolder extends RecyclerView.ViewHolder{

    View mView;

    TextView userStatusView,userNameView;
    CircleImageView userImageView;

    public ConvViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        userNameView = itemView.findViewById(R.id.username);
        userStatusView = itemView.findViewById(R.id.status);
        userImageView = itemView.findViewById(R.id.profileimage);

    }
    public void setMessage(String message, boolean isSeen){

        userStatusView.setText(message);
        if(!isSeen){

            userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);

        }
        else
        {

            userStatusView.setTypeface(userStatusView.getTypeface(),Typeface.NORMAL);

        }

    }

}
