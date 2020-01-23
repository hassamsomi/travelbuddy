package com.hassam.travellingbuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

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

        public TextView senderText, senderUserName, receiverText, receiverUserName;
        public ImageView senderImage, receiverImage;

        public MessageViewHolder (@NonNull View itemView){

            super(itemView);
//          SENDER LAYOUT
            senderText = itemView.findViewById(R.id.sender_text_layout);
            senderUserName = itemView.findViewById(R.id.sender_username);
            senderImage = itemView.findViewById(R.id.sender_image_layout);
//          RECEIVER LAYOUT
            receiverText = itemView.findViewById(R.id.receiver_text_layout);
            receiverUserName = itemView.findViewById(R.id.receiver_username);
            receiverImage = itemView.findViewById(R.id.receiver_image_layout);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int i) {


            Messages c = mMessageList.get(i);

            String fromUserID = c.getFrom();
            String fromUserType = c.getType();



            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(fromUserID);
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();

                    holder.senderUserName.setText(name);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(fromUserType.equals("text"))
            {

                holder.senderText.setText(c.getMessage());
                holder.senderText.setVisibility(View.VISIBLE);

            }
            else if(fromUserType.equals("image"))
            {

                holder.senderImage.setVisibility(View.VISIBLE);
                Picasso.get().load(c.getMessage()).into(holder.senderImage);

            }

    }
    @Override
    public int getItemCount(){
        return mMessageList.size();
    }
}




















