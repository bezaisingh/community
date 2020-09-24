package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private EditText userEmail, userPassword;
    private TextView clickRegis, forgotPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupUIViews();
        progressDialog= new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();

      if(user != null){
            Intent HomeIntent= new Intent(MainActivity.this, Second_Activity.class);
            HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(HomeIntent);
            finish();
        }
      
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    validate(userEmail.getText().toString(),userPassword.getText().toString());
            }
        });

        clickRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, register.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });
    }

    private void setupUIViews(){

        login = findViewById(R.id.btn1);
        userEmail = findViewById(R.id.uEmail);
        userPassword=findViewById(R.id.pswd);
        clickRegis=findViewById(R.id.tv1);
        forgotPassword= findViewById(R.id.forgot);

    }
    private void validate(String email, String pswd){

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();

                        checkEmailVerification();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Wrong Credentials entered", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser= firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag= firebaseUser.isEmailVerified();
        if(emailFlag){
            finish();
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Second_Activity.class));
        }else{
            Toast.makeText(MainActivity.this, "Verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}