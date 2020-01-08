package com.hassam.travellingbuddy;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;


    public MessageAdapter(List<Messages>mMessageList){
        this.mMessageList = mMessageList;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages_single_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder (@NonNull View itemView){

            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_layout);
            profileImage = itemView.findViewById(R.id.message_profile_layout);

        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int i) {

        if(mAuth.getCurrentUser()!=null) {
            String current_userID = (mAuth.getCurrentUser()).getUid();

            Messages c = mMessageList.get(i);

            String fromUserID = c.getFrom();
            String fromUserType = c.getType();


                if(fromUserID != null){
                if (fromUserID.equals(current_userID)) {

                    holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                    holder.messageText.setTextColor(Color.BLACK);


                } else {

                    holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                    holder.messageText.setTextColor(Color.WHITE);


                }
            }
            holder.messageText.setText(c.getMessage());

        }
    }
    @Override
    public int getItemCount(){
        return mMessageList.size();
    }
}




















