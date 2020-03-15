package com.hassam.travellingbuddy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    //Members used
    private FirebaseAuth mAuth;
    private Button LoginBtn;
    private TextInputEditText user_name, pass;
    private View layout;
    private ProgressDialog mLoginProgress;
    private DatabaseReference mUserDatabase;
    private static final int LOCATION_PERMISSION_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        layout = findViewById(R.id.grand_parent);
        mAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

//                                    fetchlocation();

                                }
                            }, 20
        );

        //Forgot Password Auth
        View forgotPass = findViewById(R.id.forgotPassword);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showRecoverDialog();

            }
        });


        //Jump to Register Activity
        View Txtcreateone = findViewById(R.id.createone);


        Txtcreateone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        if (mAuth.getCurrentUser() != null) {
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(mAuth.getCurrentUser().getUid());
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                    mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
//                    mUserDatabase.child("online").setValue("true");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        String username = "", password = "";


        //referencing with elements
        LoginBtn = findViewById(R.id.btnLogin);
        user_name = findViewById(R.id.txtUsername);
        pass = findViewById(R.id.txtPass);
        mLoginProgress = new ProgressDialog(this);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        //Login Click Event
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                startActivity(intent);
                //Progress Dialog Show Here
                mLoginProgress.setTitle("Logging In");
                mLoginProgress.setMessage("Please wait while we check your information.");
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();


                if (username.equals(user_name.getText().toString()) || password.equals(pass.getText().toString())) {

                    Toast.makeText(LoginActivity.this, "Please insert name and password", Toast.LENGTH_SHORT).show();
                    mLoginProgress.dismiss();

                } else {
                    //Authorization and checking user login
                    mAuth.signInWithEmailAndPassword(user_name.getText().toString(), pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LoginBtn.setEnabled(false);
                                mLoginProgress.dismiss();
                                String current_userID = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                mUserDatabase.child(current_userID).child("device_token").setValue(deviceToken).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            } else {
                                LoginBtn.setEnabled(true);
                                mLoginProgress.dismiss();
                                Snackbar.make(layout, "Username or Password is incorrect.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showRecoverDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailRec = new EditText(this);
        emailRec.setHint("Email");
        emailRec.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        emailRec.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        linearLayout.addView(emailRec);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = emailRec.getText().toString().trim();
                beginRecovery(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Snackbar.make(layout, "Code has been sent to your Email.", Snackbar.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Snackbar.make(layout, "Failed to sent.", Snackbar.LENGTH_SHORT).show();

            }
        });

    }
}