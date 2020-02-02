package com.hassam.travellingbuddy;

import android.content.Context;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    String receiverId;
    private Context context;
    private TextToSpeech textToSpeech;

//        -----------TEXT TO SPEECH---------


    public MessageAdapter(List<Messages>mMessageList, String receiverId){
        this.mMessageList = mMessageList;
        this.receiverId = receiverId;
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
        public ImageView senderImage, senderPlayBtn, receiverImage, receiverPlayBtn;

        public MessageViewHolder (@NonNull View itemView){

            super(itemView);
//          SENDER LAYOUT
            senderText = itemView.findViewById(R.id.sender_text_layout);
            senderUserName = itemView.findViewById(R.id.sender_username);
            senderImage = itemView.findViewById(R.id.sender_image_layout);
            senderPlayBtn = itemView.findViewById(R.id.senderbtnPlay);

//          RECEIVER LAYOUT
            receiverText = itemView.findViewById(R.id.receiver_text_layout);
            receiverUserName = itemView.findViewById(R.id.receiver_username);
            receiverImage = itemView.findViewById(R.id.receiver_image_layout);
            receiverPlayBtn = itemView.findViewById(R.id.receiverbtnPlay);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int i) {

//        --------------TEXT TO SPEECH------------
        textToSpeech = new TextToSpeech(null, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if(i!= TextToSpeech.ERROR){

                    textToSpeech.setLanguage(Locale.getDefault());

                }

            }
        });



        String messageSenderID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            Messages c = mMessageList.get(i);

            String fromUserID = c.getFrom();
            String fromUserType = c.getType();




            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(fromUserID);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(receiverId);
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
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    holder.receiverUserName.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.receiverImage.setVisibility(View.GONE);
            holder.receiverUserName.setVisibility(View.GONE);
            holder.receiverText.setVisibility(View.GONE);
            holder.receiverPlayBtn.setVisibility(View.GONE);
            holder.senderImage.setVisibility(View.GONE);
            holder.senderText.setVisibility(View.GONE);
            holder.senderUserName.setVisibility(View.GONE);
            holder.senderPlayBtn.setVisibility(View.GONE);

            if(fromUserType.equals("text"))
            {
                if(fromUserID.equals(messageSenderID)) {
                    holder.senderUserName.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams) holder.senderUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_END);
                    holder.senderText.setText(c.getMessage());
                    ((RelativeLayout.LayoutParams) holder.senderText.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_END);
                    holder.senderText.setVisibility(View.VISIBLE);
                }
                else
                    {
                        holder.receiverText.setVisibility(View.VISIBLE);
                        holder.receiverText.setText(c.getMessage());
                        ((RelativeLayout.LayoutParams) holder.receiverText.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_START);
                        holder.receiverUserName.setVisibility(View.VISIBLE);
                        holder.receiverUserName.setText(c.getMessage());
                        ((RelativeLayout.LayoutParams) holder.receiverUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_START);
                    }
            }
            else if(fromUserType.equals("image")) {
                if (fromUserID.equals(messageSenderID)) {
                    holder.senderUserName.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams) holder.senderUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_END);
                    holder.senderImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(c.getMessage()).into(holder.senderImage);
                    ((RelativeLayout.LayoutParams) holder.senderImage.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_END);
                }
                else
                    {
                        holder.receiverUserName.setVisibility(View.VISIBLE);
                        ((RelativeLayout.LayoutParams) holder.receiverUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_START);
                        holder.receiverImage.setVisibility(View.VISIBLE);
                        Picasso.get().load(c.getMessage()).into(holder.receiverImage);
                        ((RelativeLayout.LayoutParams) holder.receiverImage.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_START);
                    }
            }
            else if(fromUserType.equals("con")){
                if(fromUserID.equals(messageSenderID)){
                    holder.senderUserName.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams) holder.senderUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_END);
                    holder.senderPlayBtn.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams)holder.senderPlayBtn.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_END);
                    holder.senderPlayBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String toSpeak = holder.senderText.getText().toString();
                            textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);

                        }
                    });

                }
                else
                {
                    holder.receiverUserName.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams) holder.receiverUserName.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_START);
                    holder.receiverPlayBtn.setVisibility(View.VISIBLE);
                    ((RelativeLayout.LayoutParams) holder.receiverPlayBtn.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_START);
                    holder.receiverPlayBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String toSpeak = holder.receiverText.getText().toString();
                            textToSpeech.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);

                        }
                    });
                }



            }

    }
    @Override
    public int getItemCount(){
        return mMessageList.size();
    }



}




















