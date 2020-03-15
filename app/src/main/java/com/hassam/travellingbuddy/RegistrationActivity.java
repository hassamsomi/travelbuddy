package com.hassam.travellingbuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView textView;
    private ProgressDialog mRegDialogue;
    String codeSent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        layout = findViewById(R.id.grand_parent);
//        getWindow().setBackgroundDrawableResource(R.drawable.logindisplay);
        mAuth = FirebaseAuth.getInstance();
        mRegDialogue = new ProgressDialog(this);
        Fullname = findViewById(R.id.txtFullName);
        Email = findViewById(R.id.txtEmail);
        Password = findViewById(R.id.txtPassword);
        Phonenumber = findViewById(R.id.txtPhoneNumber);
        ConPassword = findViewById(R.id.txtConfirmPassword);
        CreateAccount = findViewById(R.id.btnCreateAccount);
        textView = findViewById(R.id.alreadytext);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRegDialogue.setTitle("Registering User");
                mRegDialogue.setMessage("Please wait while we create your account!");
                mRegDialogue.setCanceledOnTouchOutside(false);
                mRegDialogue.show();

                CreateAccount.setEnabled(false);

                try {
                    final String full_name = Fullname.getText().toString();
                    final String E_mail = Email.getText().toString();
                    final String Phone_No = Phonenumber.getText().toString();
                    final String Pass = Password.getText().toString();
                    final String Con_Pass = ConPassword.getText().toString();

                    String namechk = "", emailchk = "", phonechk = "", passchk = "", conchk = "";


                    if (namechk.equals(full_name) || emailchk.equals(E_mail) || phonechk.equals(Phone_No) || passchk.equals(Pass) || conchk.equals(Con_Pass)) {
                        Toast.makeText(RegistrationActivity.this, "Please fill the registration form", Toast.LENGTH_SHORT).show();
                    } else {
                        //-----------------HASHMAP USED FOR DATA INSERTION------------
                        mAuth.createUserWithEmailAndPassword(E_mail, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    String userID = mAuth.getCurrentUser().getUid();

                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("name", full_name);
                                    data.put("email", E_mail);
                                    data.put("phoneNo", Phone_No);
                                    data.put("password", Pass);
                                    data.put("image", "Default");
                                    data.put("aboutMe", "Hi there!");
                                    data.put("thumbImage", "Default");
                                    data.put("device_token", deviceToken);

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                    reference.child("UserInfo").child(userID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (Pass.equals(Con_Pass)) {
                                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                    CreateAccount.setEnabled(false);
                                                    mRegDialogue.dismiss();
                                                } else {
                                                    mRegDialogue.dismiss();
                                                    Snackbar.make(layout, "Password not Matched.", Snackbar.LENGTH_SHORT).show();
                                                    CreateAccount.setEnabled(true);
                                                }
                                            } else {
                                                mRegDialogue.dismiss();
                                                CreateAccount.setEnabled(true);
                                                Snackbar.make(layout, "Connection Failed.", Snackbar.LENGTH_LONG).show();
                                                CreateAccount.setEnabled(true);
                                            }
                                        }
                                    });
                                } else {
                                    CreateAccount.setEnabled(true);
                                    Snackbar.make(layout, "Task Not Successful", Snackbar.LENGTH_LONG).show();
                                    CreateAccount.setEnabled(true);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(layout, "Something is missing.", Snackbar.LENGTH_LONG).show();
                    CreateAccount.setEnabled(true);
                }
            }

        });
    }
}
