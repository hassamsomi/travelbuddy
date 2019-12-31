package com.hassam.travellingbuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText Fullname;
    private TextInputEditText Email;
    private TextInputEditText Phonenumber;
    private TextInputEditText Password;
    private TextInputEditText ConPassword;
    private Button CreateAccount;
    private FirebaseAuth mAuth;
    private View layout;
    private ProgressDialog mRegDialogue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        layout = findViewById(R.id.grand_parent);
//        getWindow().setBackgroundDrawableResource(R.drawable.logindisplay);
        mAuth = FirebaseAuth.getInstance();
        mRegDialogue = new ProgressDialog(this);
        Fullname= findViewById(R.id.txtFullName);
        Email= findViewById(R.id.txtEmail);
        Password= findViewById(R.id.txtPassword);
        Phonenumber= findViewById(R.id.txtPhoneNumber);
        ConPassword= findViewById(R.id.txtConfirmPassword);
        CreateAccount= findViewById(R.id.btnCreateAccount);

        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRegDialogue.setTitle("Registering User");
                mRegDialogue.setMessage("Please wait while we create your account!");
                mRegDialogue .setCanceledOnTouchOutside(false);
                mRegDialogue.show();

                try
                {
                    final String full_name=Fullname.getText().toString();
                    final String E_mail=Email.getText().toString();
                    final String Phone_No=Phonenumber.getText().toString();
                    final String Pass=Password.getText().toString();
                    final String Con_Pass=ConPassword.getText().toString();

                    //-----------------HASHMAP USED FOR DATA INSERTION------------
                    mAuth.createUserWithEmailAndPassword(E_mail,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {

                                String userID = mAuth.getCurrentUser().getUid();

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();


                                HashMap<String,Object> data=new HashMap<>();
                                data.put("name",full_name);
                                data.put("email",E_mail);
                                data.put("phoneNo",Phone_No);
                                data.put("password",Pass);
                                data.put("confirmPassword",Con_Pass);
                                data.put("image","Default");
                                data.put("aboutMe","I'm using Travel Assistant App.");
                                data.put("thumbImage","Default");
                                data.put("device_token",deviceToken);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                reference.child("UserInfo").child(userID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mRegDialogue.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            mRegDialogue.hide();
                                            CreateAccount.setEnabled(true);
                                            Snackbar.make(layout,"Connection Failed.",Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else {
                                CreateAccount.setEnabled(true);
                                Snackbar.make(layout,"Task Not Successful",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                    Snackbar.make(layout,"Something is missing.",Snackbar.LENGTH_LONG).show();
                }
            }

        });
    }
}