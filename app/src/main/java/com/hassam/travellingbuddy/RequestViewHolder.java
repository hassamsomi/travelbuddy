package com.hassam.travellingbuddy;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestViewHolder extends RecyclerView.ViewHolder {


    TextView userName, userStatus;
    Button AcceptBtn, CancelBtn;
    CircleImageView profileImage;


    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);


        userName = itemView.findViewById(R.id.username);
        userStatus = itemView.findViewById(R.id.status);
        profileImage = itemView.findViewById(R.id.profileimage);
        AcceptBtn = itemView.findViewById(R.id.btnAccept);
        CancelBtn = itemView.findViewById(R.id.btnCancel);

    }
}
