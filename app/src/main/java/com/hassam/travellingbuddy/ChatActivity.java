package com.hassam.travellingbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ChatActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQ_CODE = 1;
    private String mChatUser, mCurrentUserID;
    private TextView mDisplayUserName, mLastSeenView;
    private EditText mConversionTextView;
    private DatabaseReference mRootRef;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private static final int TOTAL_ITEMS_TO_LOAD = 5;
    private int mCurrentPage = 1;
    private ImageView mImage;
    private TextToSpeech textToSpeech;
    private String mEnglish, mUrdu;
    private AlertDialog.Builder builder;
    private ImageButton mChatAddButton, mChatSendButton, mChatMicButton;
    private EditText messageBox;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    public String locationstate = "";

    private RecyclerView mMessagesList;

    private List<Messages> messagesList = new ArrayList<>();
    private SpeedyLinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private String mPrevKey = "";
    private String mLastKey = "";
    private int itemPos = 0;
    private String myUrl = "";
    private StorageTask uploadTask;
    private Uri mImageUri;
    final String[] mSource = {""};
    final String[] mDestination = {""};
    private View view;
    private ImageButton mLocationBtn;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);

        requestPermission();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final String[] lang = new String[]{"", "az", "sq", "am", "en", "ar", "hy", "af", "eu", "ba", "be", "bn", "my", "bg", "bs", "cy", "hu", "vi", "ht", "gl", "nl", "mrj", "el", "ka", "gu", "da", "he", "yi", "id", "ga", "it", "is", "es"
                , "kk", "kn", "ca", "ky", "zh", "ko", "xh", "km", "lo", "la", "lv", "lt", "lb", "mg", "ms", "ml", "mt", "mk", "mi", "mr", "mhr", "mn", "de", "ne", "no", "pa", "pap", "fa", "pl", "pt"
                , "ro", "ru", "ceb", "sr", "si", "sk", "sl", "sw", "su", "tg", "th", "tl", "ta", "tt", "te", "tr", "udm", "uz", "uk", "ur", "fi", "fr", "hi", "hr", "cs", "sv", "gd", "et", "eo", "jv", "ja"};

        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mChatUser = getIntent().getStringExtra("chatScreen");
        builder = new AlertDialog.Builder(this);

        view = findViewById(R.id.parent);




        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

//      -------------SOURCE SPINNER-----------------
        final Spinner sourceSpinner = findViewById(R.id.sourceSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapter);
//      -------------DESTINATION SPINNER-------------
        Spinner destinationSpinner = findViewById(R.id.destinationSpinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.Languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(adapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mSource[0] = lang[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mDestination[0] = lang[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//      CHAT SCREEN LAYOUT ELEMENTS
        mDisplayUserName = findViewById(R.id.custom_profile_name);
        mLastSeenView = findViewById(R.id.custom_user_last_seen);
        mProfileImage = findViewById(R.id.custom_profile_image);

        mChatAddButton = findViewById(R.id.send_files_btn);
        mChatSendButton = findViewById(R.id.send_message_btn);
        mChatMicButton = findViewById(R.id.btnMic);

        messageBox = findViewById(R.id.input_message);
        mConversionTextView = findViewById(R.id.converterTextView);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mLocationBtn = findViewById(R.id.btnLocation);

        mEnglish = "";
        mUrdu = "";

        mAdapter = new MessageAdapter(ChatActivity.this, messagesList, mChatUser);
        mMessagesList = findViewById(R.id.messages_list);
        mRefreshLayout = findViewById(R.id.message_swipe_layout);
        mLinearLayout = new SpeedyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
//        mLinearLayout.setReverseLayout(true);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);

        mRootRef.child("Chat").child(mCurrentUserID).child(mChatUser).child("seen").setValue(true);


//      LOAD MESSAGES IN CHAT SCREEN
        loadMessage();

        mRootRef.child("UserInfo").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String userName = dataSnapshot.child("name").getValue().toString();

                mDisplayUserName.setText(userName);
                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(mProfileImage);

                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                } else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    mLastSeenView.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (ContextCompat.checkSelfPermission(ChatActivity.this,
//                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(ChatActivity.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
//                    fetchLocation();
//                }
//                else {
//                    fetchLocation();
//                }

                if(ActivityCompat.checkSelfPermission(ChatActivity.this, ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                    return;
                }

                if (locationstate.equals("granted")) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(ChatActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.

                                    if (location != null) {
                                        // Logic to handle location object
                                        double longitude = location.getLongitude();
                                        double latitute = location.getLatitude();


                                        final String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
                                        final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

                                        DatabaseReference user_message_push = mRootRef.child("messages")
                                                .child(mCurrentUserID).child(mChatUser).push();

                                        String push_id = user_message_push.getKey();


                                        Map<String, Object> messageMap = new HashMap<>();
                                        messageMap.put("longitude", longitude);
                                        messageMap.put("latitude", latitute);
                                        messageMap.put("seen", false);
                                        messageMap.put("type", "location");
                                        messageMap.put("time", ServerValue.TIMESTAMP);
                                        messageMap.put("from", mCurrentUserID);
                                        messageMap.put("to", mChatUser);
                                        messageMap.put("messageID", push_id);

                                        Map<String, Object> messageUserMap = new HashMap<String, Object>();
                                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                if (databaseError != null) {
                                                    Snackbar.make(view, "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        });


                                    }
                                }
                            });
                } else {
//                    fetchLocation();
                }
            }
        });


        mRootRef.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map<String, Object> chatAddMap = new HashMap<>();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map<String, Object> chatUserMap = new HashMap<String, Object>();
                    chatUserMap.put("Chat/" + mCurrentUserID + "/" + mChatUser, chatAddMap);
                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(view, databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

//      -----------------SEND MESSAGES FEATURE--------------------
        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
//                listen();
            }
        });

//      -----------------SEND IMAGE FEATURE---------------
        mChatAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 438);
            }
        });
//        --------------TEXT TO SPEECH FUNCTIONALITY----------------
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
//       ---------------PAGINATION------------------
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mCurrentPage++;
                loadMoreMessage();
            }
        });
//      ----------------VOICE MESSAGE SENT BUTTON---------------
        mChatMicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
    }

    private void fetchLocation() {

        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this,
                    ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("This permission is needed to use location feature.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(ChatActivity.this, new String[]
                                        {ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQ_CODE);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();

                            }
                        })
                        .create().show();
            } else {

                ActivityCompat.requestPermissions(ChatActivity.this, new String[]
                        {ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQ_CODE);

            }

        }


    }

    //    ---------------RECORD VOICE FUNCTION----------------
    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Snackbar.make(view, "" + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


    //  -----------------REQUEST AND RESULT CODE---------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mConversionTextView.setText(result.get(0));
                translate(mSource[0], mDestination[0], mConversionTextView.getText().toString());
            }
        }
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Bitmap bitmap = null, decoded = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
                byte[] mdata = out.toByteArray();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image_files");

                final String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
                final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

                DatabaseReference user_message_push = mRootRef.child("messages")
                        .child(mCurrentUserID).child(mChatUser).push();

                final String push_id = user_message_push.getKey();
                final StorageReference filePath = storageReference.child(push_id + "." + "jpg");
                uploadTask = filePath.putBytes(mdata);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUrl = task.getResult();
                                    myUrl = downloadUrl.toString();

                                    Map<String, Object> messageMap = new HashMap<>();
                                    messageMap.put("message", myUrl);
                                    messageMap.put("name", mImageUri.getLastPathSegment());
                                    messageMap.put("seen", false);
                                    messageMap.put("type", "image");
                                    messageMap.put("time", ServerValue.TIMESTAMP);
                                    messageMap.put("from", mCurrentUserID);
                                    messageMap.put("to", mChatUser);
                                    messageMap.put("messageID", push_id);

                                    Map<String, Object> messageUserMap = new HashMap<>();
                                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                                    messageBox.setText("");

                                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Snackbar.make(view, "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });


            } catch (IOException e) {
                e.printStackTrace();
                String msg = e.getMessage();
                Snackbar.make(view, msg + "", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    //  -----------------LOAD MESSAGE FUNCTION-----------------
    private void loadMessage() {
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserID).child(mChatUser);
        Query messageQuery = messageRef.orderByChild("timestamp").limitToLast(/* mCurrentPage * */ TOTAL_ITEMS_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                itemPos++;
                if (itemPos == 1) {
                    mLastKey = dataSnapshot.getKey();
//                    mPrevKey = messageKey;
                }
                messagesList.add(0, message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.smoothScrollToPosition(0);
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

    //  -----------------SEND MESSAGE FUNCTION------------------
    private void sendMessage() {
        String message = messageBox.getText().toString();
//  ------------------SENDING TEXT MESSAGE------------------
        if (!TextUtils.isEmpty(message)) {
            String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserID).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserID);
            messageMap.put("to", mChatUser);
            messageMap.put("messageID", push_id);

            Map<String, Object> messageUserMap = new HashMap<>();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            messageBox.setText("");

            mRootRef.child("Chat").child(mCurrentUserID).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserID).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserID).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserID).child("timestamp").setValue(ServerValue.TIMESTAMP);
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Snackbar.make(view, "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //  -----------------FIXING PAGINATION----------------------
    private void loadMoreMessage() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserID).child(mChatUser);
        final Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEMS_TO_LOAD);
        final int[] tempItemPosiion = {itemPos};
        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();
                message.key = messageKey;

                if (!mLastKey.equals(messageKey)) {
                    messagesList.add(tempItemPosiion[0], message);
                    itemPos++;
                } else {
                    mLastKey = messagesList.get(messagesList.size() - 1).key;
                }
//                else
//                    {
//                    mPrevKey = mLastKey;
//                }

                Log.d("TOTALKEYS", "Last Key" + mLastKey + "| Prev Key" + mPrevKey + "| Message Key" + messageKey);
//                mAdapter.notifyDataSetChanged();
//                mRefreshLayout.setRefreshing(false);
//                mLinearLayout.scrollToPositionWithOffset(0, 0);
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
        };
        messageQuery.addChildEventListener(childEventListener);

        messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.getChildrenCount() > 1) {
                    mLinearLayout.smoothScrollToPosition(mMessagesList, null, Long.valueOf(messagesList.size() - 1 - dataSnapshot.getChildrenCount() + 2).intValue());
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mMessagesList.smoothScrollToPosition(Long.valueOf(messagesList.size() - 1 - dataSnapshot.getChildrenCount() + 2).intValue());
//                        }
//                    }, 2000);
                }
                messageQuery.removeEventListener(childEventListener);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //  -----------------PLAY VOICE MESSAGE FUNCTIONALITY----------
    public void listen() {
        String text = mConversionTextView.getText().toString();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //  ------------------TEXT CONVERSION-----------------------
    public String translate(String source, String destination, String content) {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate" +
                "?key=" + getString(R.string.yandex_api_key) +
                "&text=" + content +
                "&lang=" + source + "-" + destination;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("YANDEX_RESPONSE_STRING",
                                "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + response + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

                        try {
                            JSONObject json = new JSONObject(response);
                            mConversionTextView.setText(json.getString("text"));
                            String voicemessage = mConversionTextView.getText().toString();

                            String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
                            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

                            DatabaseReference user_message_push = mRootRef.child("messages")
                                    .child(mCurrentUserID).child(mChatUser).push();

                            String push_id = user_message_push.getKey();

                            Map<String, Object> messageMap = new HashMap<>();
                            messageMap.put("message", voicemessage);
                            messageMap.put("seen", false);
                            messageMap.put("type", "con");
                            messageMap.put("time", ServerValue.TIMESTAMP);
                            messageMap.put("from", mCurrentUserID);
                            messageMap.put("to", mChatUser);
                            messageMap.put("messageID", push_id);

                            Map<String, Object> messageUserMap = new HashMap<String, Object>();
                            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    if (databaseError != null) {
                                        Snackbar.make(view, "" + databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String msg = e.getMessage();
                            Snackbar.make(view, "" + msg, Snackbar.LENGTH_LONG).show();
                        }
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("YANDEX_RESPONSE_STRING", "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + error.getMessage() + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return null;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
        locationstate = "granted";
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == LOCATION_PERMISSION_REQ_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(ChatActivity.this, "Location Permission is Granted.", Toast.LENGTH_SHORT).show();
//                locationstate = "granted";
//            } else {
//                Toast.makeText(ChatActivity.this, "Location Permission is not Granted.", Toast.LENGTH_SHORT).show();
//            }
//        }
//        ActivityCompat.requestPermissions(this,);
//    }
}
