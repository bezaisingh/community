package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class myProfileActivity extends AppCompatActivity {

    private TextView profileName,profileMobNum ,profileDob, profileGender, profileDistrict, profileAddress, profileState, profileEmail, profileUPI;
    private Button editProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private ImageView idBack, idFront;
    private CircleImageView profilePic;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Loading in...");
        progressDialog.show();



        profileName = findViewById(R.id.tvName);
        profileMobNum = findViewById(R.id.mobNum);
        profileDob = findViewById(R.id.tvDOB);
        profileGender = findViewById(R.id.tvGender);
        profileDistrict = findViewById(R.id.tvDistrict);
        profileAddress = findViewById(R.id.tvAddress);
        profileState = findViewById(R.id.tvState);
        editProfile = findViewById(R.id.btnUpdateDetails);
        profilePic = findViewById(R.id.profilePic);
        idFront = findViewById(R.id.viewIdFront);
        idBack = findViewById(R.id.viewIdBack);
        profileEmail=findViewById(R.id.tvEmail);
        profileUPI=findViewById(R.id.tvUPI);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("User Profiles").child(firebaseAuth.getUid());



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profileName.setText("Name: " + userProfile.getFname() + " " +userProfile.getMname() + " " + userProfile.getLname());
                profileMobNum.setText("Phone:" + userProfile.getMobileNumber());
                profileDob.setText("DOB: " + userProfile.getDOB());
                profileGender.setText("Gender: " + userProfile.getGender());
                profileDistrict.setText("District: " + userProfile.getDistrict());
                profileAddress.setText("Address: " + userProfile.getAddress());
                profileState.setText("State: " + userProfile.getState());
                profileEmail.setText("Email:" + userProfile.getEmailId());
                profileUPI.setText("UPI Id: "+ userProfile.getUPIid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(myProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(myProfileActivity.this, UserDetails.class));
            }
        });

        DisplayPictures();

    }

    private void DisplayPictures() {

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profilePic);

            }
        });


        storageReference.child(firebaseAuth.getUid()).child("Images/Id Front").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(idFront);

            }
        });



        storageReference.child(firebaseAuth.getUid()).child("Images/Id Back").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(idBack);
                progressDialog.dismiss();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}