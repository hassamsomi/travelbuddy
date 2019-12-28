package com.hassam.travellingbuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private  FirebaseUser mCurrentUser;
    private CircleImageView mImage;
    private TextView mName;
    private ImageView mBtnConfirmStat;
    private EditText mAbout_Me;
    private ImageView mBtnCancelStat;
    private String tempStatus;
    private Button btnChangeImage;
    private View layout;
    private Uri ImageUri;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        layout = findViewById(R.id.grand_parent);

        final View layout = findViewById(R.id.grand_parent);

        //------------------LINKING LAYOUT---------------
        mName = findViewById(R.id.display_text);
        mImage = findViewById(R.id.display_image);
        mBtnConfirmStat = findViewById(R.id.btn_confirm_status);
        mBtnCancelStat = findViewById(R.id.btn_cancel_status);
        mAbout_Me = findViewById(R.id.about_text);
        btnChangeImage = findViewById(R.id.btnChange_image);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_userID =  mCurrentUser.getUid();

        //---------------EDIT STATUS BUTTON----------------
        mBtnConfirmStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAbout_Me.isEnabled()) {
                    mBtnConfirmStat.setImageResource(R.drawable.status_edit_icon);
                    mAbout_Me.setEnabled(false);
                    mBtnCancelStat.setVisibility(View.GONE);
                    mBtnCancelStat.invalidate();
                    mBtnConfirmStat.invalidate();
//                        HashMap<String, Object> data = new HashMap();
//                        data.put("aboutMe","I'm using Travel Assistant App.");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child("UserInfo").child(current_userID).child("aboutMe").setValue(mAbout_Me.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(!task.isSuccessful()){

                                Snackbar.make(layout,"Connection Failed! Try again.",Snackbar.LENGTH_LONG).show();
                                mAbout_Me.setText(tempStatus);
                            }else
                            {
                                Snackbar.make(layout, "Successfully Done",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    mAbout_Me.setEnabled(true);
                    mBtnConfirmStat.setImageResource(R.drawable.ic_status_confirmation);
                    tempStatus = mAbout_Me.getText().toString();
                    mBtnCancelStat.setVisibility(View.VISIBLE);
                    mAbout_Me.requestFocus();
                    mBtnCancelStat.invalidate();
                    mBtnConfirmStat.invalidate();
                }
            }
        });

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(current_userID);
        mUserDatabase.keepSynced(true);


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String about_me = dataSnapshot.child("aboutMe").getValue().toString();
                String thumb_image = dataSnapshot.child("thumbImage").getValue().toString();

                mName.setText(name);
                mAbout_Me.setText(about_me);
                tempStatus = about_me;
                if(!image.equals("Default")) {

//                    Picasso.get().load(image).placeholder(R.drawable.profile_image).error(android.R.drawable.stat_notify_error).into(mImage);
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image)
                        .into(mImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                    Picasso.get().load(image).placeholder(R.drawable.profile_image).error(android.R.drawable.stat_notify_error).into(mImage);

                            }
                        });


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Cancel Status Button
        mBtnCancelStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnConfirmStat.setImageResource(R.drawable.status_edit_icon);
                mAbout_Me.setEnabled(false);
                mAbout_Me.setText(tempStatus);
                mBtnCancelStat.setVisibility(View.GONE);
                mBtnCancelStat.invalidate();
                mBtnConfirmStat.invalidate();
                mAbout_Me.invalidate();

            }
        });
        //Image Change Button
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_intent,"Select Image"),GALLERY_PICK);
// start picker to get image for cropping and then use the image in cropping activity
//                 CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .start(this);

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);
        }
        //----------------REQUEST CODE IS USED HERE FOR PHOTO PICKER---------------
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialogue = new ProgressDialog(SettingsActivity.this);
                mProgressDialogue.setTitle("Uploading Image...");
                mProgressDialogue.setMessage("Please wait while we upload and process the image.");
                mProgressDialogue.setCanceledOnTouchOutside(false);
                mProgressDialogue.show();
                final Bitmap[] bitmap = new Bitmap[1];
                Uri resultUri = result.getUri();
                try {
                    bitmap[0] = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String current_userID =  mCurrentUser.getUid();

                final StorageReference filepath = mImageStorage.child("profile_images").child(current_userID+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Task<Uri> uri =  filepath.getDownloadUrl();
                            uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()) {
                                        mImage.setImageBitmap(bitmap[0]);
                                        String url = task.getResult().toString();
                                        mUserDatabase.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressDialogue.dismiss();
                                                Toast .makeText(SettingsActivity.this,"Successfully uploaded.",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else{

                            Toast.makeText(SettingsActivity.this,"Error in uploading.",Toast.LENGTH_LONG).show();
                            mProgressDialogue.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}

