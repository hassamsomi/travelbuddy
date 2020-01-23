package com.hassam.travellingbuddy;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

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

        public TextView messageText, displaytext;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public MessageViewHolder (@NonNull View itemView){

            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_layout);
            profileImage = itemView.findViewById(R.id.message_profile_layout);
            displaytext = itemView.findViewById(R.id.user_text_layout);
            messageImage = itemView.findViewById(R.id.message_image_layout);


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
                    String image = dataSnapshot.child("image").getValue().toString();

                    holder.displaytext.setText(name);
                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.profileImage);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(fromUserType.equals("text"))
            {

                holder.messageText.setText(c.getMessage());
                holder.messageText.setVisibility(View.VISIBLE);

            }
            else if(fromUserType.equals("image"))
            {

                holder.messageImage.setVisibility(View.VISIBLE);
                Picasso.get().load(c.getMessage()).into(holder.messageImage);

            }

    }
    @Override
    public int getItemCount(){
        return mMessageList.size();
    }
}




















