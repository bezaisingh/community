package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class view_request_profile extends AppCompatActivity {

    private TextView profileName,profileMobNum, profileGender, profileDistrict, profileAddress, profileState, profileEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase ;
    private FirebaseStorage firebaseStorage;
    private CircleImageView profilePic;
    String UserTopass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_request_profile);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserTopass= getIntent().getExtras().get("UserTopass").toString();




        profileName = findViewById(R.id.tvName1);
        profileMobNum = findViewById(R.id.mobNum1);
        profileGender = findViewById(R.id.tvGender1);
        profileDistrict = findViewById(R.id.tvDistrict1);
        profileAddress = findViewById(R.id.tvAddress1);
        profileState = findViewById(R.id.tvState1);
        profilePic = findViewById(R.id.profilePic1);
        profileEmail=findViewById(R.id.tvEmail1);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();



        if(UserTopass.equals("empty")){
            Toast.makeText(view_request_profile.this, "NO requests yet", Toast.LENGTH_SHORT).show();

        }else{



            DatabaseReference databaseReference = firebaseDatabase.getReference("User Profiles").child(UserTopass);

            StorageReference storageReference = firebaseStorage.getReference();
            storageReference.child(UserTopass).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(profilePic);

                }
            });



            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    profileName.setText("Name: " + userProfile.getFname() + " " +userProfile.getMname() + " " + userProfile.getLname());
                    profileMobNum.setText("Phone:" + userProfile.getMobileNumber());
                    profileGender.setText("Gender: " + userProfile.getGender());
                    profileDistrict.setText("District: " + userProfile.getDistrict());
                    profileAddress.setText("Address: " + userProfile.getAddress());
                    profileState.setText("State: " + userProfile.getState());
                    profileEmail.setText("Email:" + userProfile.getEmailId());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(view_request_profile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

                }
            });


        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new  Intent(view_request_profile.this,Second_Activity.class));
                onBackPressed();
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }






}

