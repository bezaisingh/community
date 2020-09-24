package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class register extends AppCompatActivity {
    private EditText Name, email, password;
    private Button register;
    private TextView already;
    private FirebaseAuth firebaseAuth;
    String name, country, dist, pin, state, addr, DOB, mName, lName, strGender,MobileNumber, Email,strUPIid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Registration");
        setupUIViews();
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog= new ProgressDialog(this);

       already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register.this, MainActivity.class));
            }
        });

    register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               progressDialog.setMessage("Verifying please wait...");
               progressDialog.show();

               if (validate()) {

                  String userEmail = email.getText().toString().trim();
                  String userPassword= password.getText().toString().trim();

                  firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {

                          if (task.isSuccessful()){
                         // Toast.makeText(com.example.community.register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        sendEmailVerification();

                      }else{
                              Toast.makeText(com.example.community.register.this, "Registration Failed, email already used", Toast.LENGTH_SHORT).show();
                              progressDialog.dismiss();
                          }
                      }
                  });

               }
           }
       });
    }

    private void setupUIViews() {

        Name= findViewById(R.id.name);
        email= findViewById(R.id.email);
        password=findViewById(R.id.pwd);
        register=findViewById(R.id.register);
        already=findViewById(R.id.tv1);
    }

private Boolean validate() {
        Boolean result = false;
        name =Name.getText().toString();
        Email = email.getText().toString();
        String pass = password.getText().toString();

        if(name.isEmpty() || Email.isEmpty() || pass.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }
        else{
            result=true;
        }
        return result;
    }
    private void sendEmailVerification(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        progressDialog.dismiss();
                        Toast.makeText(register.this, "Successfully Registered, Verification mail sent...", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(com.example.community.register.this, MainActivity.class));
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(register.this, "!!! Verification mail NOT been sent...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void sendUserData(){
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference databaseReference= firebaseDatabase.getReference("User Profiles").child(firebaseAuth.getUid());
        UserProfile userProfile = new UserProfile(name,mName,lName,DOB,strGender,MobileNumber,Email,addr,state,pin,dist,country,strUPIid);
        databaseReference.setValue(userProfile);

    }

}



