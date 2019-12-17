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

public class LoginActivity extends AppCompatActivity {
    //Members used
    private FirebaseAuth mAuth;
    private Button LoginBtn;
    private TextInputEditText user_name;
    private TextInputEditText pass;
    private View layout;
    private ProgressDialog mLoginProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        layout = findViewById(R.id.grand_parent);
        mAuth = FirebaseAuth.getInstance();

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


        //referencing with elements
        LoginBtn = (Button) findViewById(R.id.btnLogin);

        user_name = findViewById(R.id.txtUsername);
        pass = findViewById(R.id.txtPass);
        mLoginProgress = new ProgressDialog(this);


        //Login Click Event
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

//                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                startActivity(intent);

                //Progress Dialog Show Here
                mLoginProgress.setTitle("Logging In");
                mLoginProgress.setMessage("Please wait while we check your information.");
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();




                //Authorization and checking user login
                mAuth.signInWithEmailAndPassword(user_name.getText().toString(),pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                            mLoginProgress.dismiss();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
//                            mLoginProgress.hide();
                            Snackbar.make(layout,"Username or Password is incorrect." ,Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


//        getWindow().setBackgroundDrawableResource(R.drawable.logindisplay);

    }




}
