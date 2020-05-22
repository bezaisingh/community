package com.example.community;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private Button forgotNext;
    private EditText resetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Forgot Password");

        firebaseAuth=FirebaseAuth.getInstance();

        forgotNext=findViewById(R.id.btnNext);
        resetPassword=findViewById(R.id.forgotEmail);
        forgotNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String userEmail= resetPassword.getText().toString().trim();

              if( userEmail.equals("")){

                  Toast.makeText(ForgotPassword.this, "Please Enter your registered emailId", Toast.LENGTH_SHORT).show();
              }else{
                  firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              Toast.makeText(ForgotPassword.this, "Password reset link sent to your email address...", Toast.LENGTH_SHORT).show();
                              finish();
                              startActivity(new Intent(ForgotPassword.this , MainActivity.class));
                          }else{
                              Toast.makeText(ForgotPassword.this, "User is not registered ....", Toast.LENGTH_SHORT).show();
                          }
                      }
                  });
              }

            }
        });
    }
}
