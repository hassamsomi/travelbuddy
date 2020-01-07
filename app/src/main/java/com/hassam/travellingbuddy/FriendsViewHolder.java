package com.hassam.travellingbuddy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

class FriendsViewHolder extends RecyclerView.ViewHolder{

    View mView;
    TextView mAboutMe;
    TextView mName;
    CircleImageView mImage;
    ImageView mOnline;


    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        mAboutMe = itemView.findViewById(R.id.status);
        mName = itemView.findViewById(R.id.username);
        mImage = itemView.findViewById(R.id.profileimage);
        mOnline = (ImageView) itemView.findViewById(R.id.onlinePNG);




    }

}
