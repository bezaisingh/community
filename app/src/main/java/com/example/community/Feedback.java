package com.example.community;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {
    private EditText etFeedback;
    private Button btnFeedBack;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference feedbackRef;
    String feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        feedbackRef=FirebaseDatabase.getInstance().getReference().child("Feedback").child(firebaseAuth.getUid());

        etFeedback=findViewById(R.id.feedbackText);
        btnFeedBack=findViewById(R.id.btnFeedback);

        btnFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                             {
                                 SubmitFeedback();
                                 resetText();
                             }
            }
        });
    }

    private void SubmitFeedback() {
        feedback=etFeedback.getText().toString();
        feedbackRef.child("Feedback").setValue(feedback);
    }

    private void resetText(){

        etFeedback.setText("");
        Toast.makeText(this, "Feedback Sent...", Toast.LENGTH_SHORT).show();

    }


    private Boolean validate(){
        Boolean result = false;

        feedback = etFeedback.getText().toString();

        if(feedback.isEmpty()){
            Toast.makeText(this, "Please enter ID Number", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
