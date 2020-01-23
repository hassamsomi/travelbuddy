package com.hassam.travellingbuddy;

import
        androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import
        androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser, mCurrentUserID;
    private TextView mDisplayUserName, mLastSeenView;
    private DatabaseReference mRootRef;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private ImageView mImage;

    private ImageButton mChatAddButton, mChatSendButton;
    private EditText messageBox;

    private RecyclerView mMessagesList;

    private List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private String mPrevKey = "";
    private String mLastKey = "";
    private int itemPos = 0;
    private String myUrl="";
    private StorageTask uploadTask;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("chatScreen");

//      CHAT SCREEN LAYOUT ELEMENTS
        mDisplayUserName = findViewById(R.id.custom_profile_name);
        mLastSeenView = findViewById(R.id.custom_user_last_seen);
        mProfileImage = findViewById(R.id.custom_profile_image);

        mChatAddButton = findViewById(R.id.send_files_btn);
        mChatSendButton = findViewById(R.id.send_message_btn);
        messageBox = findViewById(R.id.input_message);

        mAdapter = new MessageAdapter(messagesList, mChatUser);

        mMessagesList = findViewById(R.id.messages_list);

        mRefreshLayout = findViewById(R.id.message_swipe_layout);

        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);

        loadMessage();


        mRootRef.child("UserInfo").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String userName = dataSnapshot.child("name").getValue().toString();

                mDisplayUserName.setText(userName);
                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(mProfileImage);

                if(online.equals("true")){

                    mLastSeenView.setText("Online");

                }
                else{

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                    mLastSeenView.setText(lastSeenTime);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mChatUser)){

                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map<String, Object> chatUserMap = new HashMap<String, Object>();
                    chatUserMap.put("Chat/"+mCurrentUserID+"/"+mChatUser,chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Log.d( "CHAT_LOG",databaseError.getMessage().toString());

                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ChatActivity.this, (CharSequence) databaseError,Toast.LENGTH_LONG).show();

            }
        });

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });

        mChatAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),438);

            }
        });


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessage();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData()!=null)
        {
            fileUri = data.getData();
            String checker = "image";
            if(checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image_files");

                final String current_user_ref = "messages/"+mCurrentUserID+"/"+mChatUser;
                final String chat_user_ref = "messages/"+mChatUser+"/"+mCurrentUserID;

                DatabaseReference user_message_push = mRootRef.child("messages")
                        .child(mCurrentUserID).child(mChatUser).push();

                final String push_id = user_message_push.getKey();

                final StorageReference filePath = storageReference.child(push_id+"."+"jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map<String, Object> messageMap = new HashMap<>();
                            messageMap.put("message",myUrl);
                            messageMap.put("name",fileUri.getLastPathSegment());
                            messageMap.put("seen",false);
                            messageMap.put("type","image");
                            messageMap.put("time",ServerValue.TIMESTAMP);
                            messageMap.put("from",mCurrentUserID);
                            messageMap.put("to",mChatUser);
                            messageMap.put("messageID",push_id);

                            Map<String, Object> messageUserMap = new HashMap<>();
                            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                            messageBox.setText("");

                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    if(databaseError != null){

                                        Toast.makeText(ChatActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                        }
                    }
                });


            }
            else
                {
                    Toast.makeText(this,"Nothing Selected, Error.", Toast.LENGTH_LONG).show();
                }

        }

    }

    private void loadMessage() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserID).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(mCurrentPage = TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(){

        String message = messageBox.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/"+mCurrentUserID+"/"+mChatUser;
            String chat_user_ref = "messages/"+mChatUser+"/"+mCurrentUserID;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserID).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUserID);
            messageMap.put("to",mChatUser);
            messageMap.put("messageID",push_id);

            Map<String, Object> messageUserMap = new HashMap<String, Object>();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            messageBox.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Toast.makeText(ChatActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();

                    }

                }
            });


        }

    }

    private void loadMoreMessage(){

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserID).child(mChatUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++,message);

                }
                else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1){

                    mLastKey = messageKey;

                }

                Log.d( "TOTALKEYS","Last Key"+mLastKey+"| Prev Key"+ mPrevKey+"| Message Key"+ messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10,0);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
