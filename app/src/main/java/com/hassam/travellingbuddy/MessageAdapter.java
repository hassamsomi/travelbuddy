package com.hassam.travellingbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private String receiverId;
    private Context context;
    private TextToSpeech textToSpeech;
    private AlertDialog dialog;
    private ImageView imageView;
    private FusedLocationProviderClient fusedLocationClient;
    private String maptoken;// = String.valueOf(R.string.map_view_key);
    private static String MAP_ACCESS_TOKEN;
    private LocationManager locationManager;
    private MapboxNavigation mapboxNavigation;

    //        -----------TEXT TO SPEECH---------
    MessageAdapter(Context context, List<Messages> mMessageList, String receiverId) {
        this.mMessageList = mMessageList;
        this.receiverId = receiverId;
        this.context = context;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        imageView = new ImageView(context);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLayout.setBackgroundColor(Color.parseColor("#BB000000"));
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        frameLayout.addView(imageView);
        builder.setView(frameLayout);
        dialog = builder.create();

        maptoken = context.getString(R.string.map_view_key);


    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.messages_single_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView senderText, senderUserName, receiverText, receiverUserName;
        ImageView senderImage, senderPlayBtn, receiverImage, receiverPlayBtn, mPopImageView;
        ImageView receiverGif, senderGif;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
//          SENDER LAYOUT
            senderText = itemView.findViewById(R.id.sender_text_layout);
            senderUserName = itemView.findViewById(R.id.sender_username);
            senderImage = itemView.findViewById(R.id.sender_image_layout);
            senderPlayBtn = itemView.findViewById(R.id.senderbtnPlay);
            mPopImageView = itemView.findViewById(R.id.alertImage);
            senderGif = itemView.findViewById(R.id.senderGif);

//          RECEIVER LAYOUT
            receiverText = itemView.findViewById(R.id.receiver_text_layout);
            receiverUserName = itemView.findViewById(R.id.receiver_username);
            receiverImage = itemView.findViewById(R.id.receiver_image_layout);
            receiverPlayBtn = itemView.findViewById(R.id.receiverbtnPlay);
            receiverGif = itemView.findViewById(R.id.receiverGif);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int i) {
//        --------------TEXT TO SPEECH------------
        textToSpeech = new TextToSpeech(context, i1 -> {
            if (i1 != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });
        String messageSenderID = (Objects.requireNonNull(mAuth.getCurrentUser())).getUid();
        Messages c = mMessageList.get(i);
        String fromUserID = c.getFrom();
        String fromUserType = c.getType();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("Loading Route");
        mProgressDialog.setMessage("Please wait while we gather information of route.");
        mProgressDialog.setCanceledOnTouchOutside(false);


        mapboxNavigation = new MapboxNavigation(context, context.getString(R.string.map_view_key));

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(fromUserID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(receiverId);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                holder.senderUserName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
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
        holder.mPopImageView.setVisibility(View.GONE);
        holder.senderGif.setVisibility(View.GONE);
        holder.receiverGif.setVisibility(View.GONE);

        switch (fromUserType) {
            case "text":
                if (fromUserID.equals(messageSenderID)) {
                    holder.senderText.setText(c.getMessage());
                    holder.senderText.setVisibility(View.VISIBLE);

                } else {
                    holder.receiverText.setVisibility(View.VISIBLE);
                    holder.receiverText.setText(c.getMessage());
                    holder.receiverText.setTextColor(Color.parseColor("#000000"));
                }
                break;
            case "image":
                if (fromUserID.equals(messageSenderID)) {
                    holder.senderImage.setVisibility(View.VISIBLE);
                    holder.mPopImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(c.getMessage()).into(holder.senderImage);
                    holder.mPopImageView = holder.senderImage;
                    holder.senderImage.setOnClickListener(view -> {
                        Drawable drawable = holder.senderImage.getDrawable();
                        imageView.setImageDrawable(drawable);
                        dialog.show();
                    });
                } else {
                    holder.receiverImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(c.getMessage()).into(holder.receiverImage);
                    holder.receiverImage.setOnClickListener(view -> {
                        Drawable drawable = holder.receiverImage.getDrawable();
                        imageView.setImageDrawable(drawable);
                        dialog.show();
                    });
                }

                break;
            case "con":
                if (fromUserID.equals(messageSenderID)) {
                    holder.senderPlayBtn.setVisibility(View.VISIBLE);
                    holder.senderText.setText(c.getMessage());
                    holder.senderPlayBtn.setOnClickListener(view -> {
                        String toSpeak = holder.senderText.getText().toString();
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                    });
                } else {
                    holder.receiverPlayBtn.setVisibility(View.VISIBLE);
                    holder.receiverText.setText(c.getMessage());
                    holder.receiverPlayBtn.setOnClickListener(view -> {
                        String toSpeak = holder.receiverText.getText().toString();
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    });
                }
                break;
            case "location":
                if (fromUserID.equals(messageSenderID)) {
                    holder.senderGif.setVisibility(View.VISIBLE);
                    holder.senderGif.setOnClickListener(view -> {
                        if (holder.senderGif.isClickable()) {

                            Intent intent = new Intent(context, CurrentLocationActivity.class);
                            context.startActivity(intent);
                        }
                    });

                } else {
                    holder.receiverGif.setVisibility(View.VISIBLE);
                    double latitude = c.getLatitude();
                    double longitude = c.getLatitude();
                    Mapbox.getInstance(context, context.getString(R.string.map_view_key));
                    holder.receiverGif.setOnClickListener(view -> {
                        mProgressDialog.show();
                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {


                            if (location != null) {
                                final double longi = location.getLongitude();
                                final double lati = location.getLatitude();

                                Point origin = Point.fromLngLat(longitude, latitude);
                                Point destination = Point.fromLngLat(longi, lati);

                                NavigationRoute.builder(context).accessToken(context.getString(R.string.map_view_key))
                                        .origin(origin)
                                        .destination(destination)
                                        .build()
                                        .getRoute(new Callback<DirectionsResponse>() {
                                            @Override
                                            public void onResponse(@NotNull Call<DirectionsResponse> call,
                                                                   @NotNull Response<DirectionsResponse> response) {
                                                MAP_ACCESS_TOKEN = context.getString(R.string.map_view_key);

                                                assert response.body() != null;
                                                DirectionsRoute route = response.body().routes().get(0);
                                                if (route != null) {
                                                    // Create a NavigationLauncherOptions object to package everything together
                                                    NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                                            .directionsRoute(route)
                                                            .shouldSimulateRoute(false)
                                                            .build();

                                                    // Call this method with Context from within an Activity
                                                    if (options != null) {
                                                        NavigationLauncher.startNavigation((Activity) context, options);
                                                        mProgressDialog.dismiss();
                                                    } else {
                                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                        mProgressDialog.dismiss();
                                                    }
                                                } else {

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(context, "Route not found", Toast.LENGTH_LONG).show();

                                                }

                                            }

                                            @Override
                                            public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable t) {

                                            }
                                        });
                            }
                        });

                    });
                }

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}