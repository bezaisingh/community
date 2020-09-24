package com.example.community;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class ViewKYC extends AppCompatActivity {
    private String UserKey;
    private ImageView idBack, idFront;
    private Button delete;
    private TextView tvIdNumber;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;
    private DatabaseReference IdRef;
    private FirebaseDatabase firebaseDatabase;
    public static final String TAG = ViewKYC.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_kyc);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("KYC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserKey= getIntent().getExtras().get("UserKey").toString();

        firebaseStorage =FirebaseStorage.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        IdRef= FirebaseDatabase.getInstance().getReference("KYC Details").child(UserKey);



        idBack=findViewById(R.id.kycIdBack);
        idFront=findViewById(R.id.kycIdFront);
        delete=findViewById(R.id.btnDeleteUser);
        tvIdNumber=findViewById(R.id.tvIdNumber);
        DisplayPictures();
        DisplayIdNumber();

        //Delete Button .............
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[]= new CharSequence[]
                        {
                                "Yes, Delete",
                                "No, Cancel"
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(ViewKYC.this);
                builder.setTitle("Confirm Delete Profile...");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){

                       // databaseReference.child(UserKey).removeValue();



                            Intent myPostIntent =new Intent(ViewKYC.this ,View_all_users.class);
                            myPostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(myPostIntent);
                            finish();

                            Toast.makeText(ViewKYC.this, "Deletion Successful", Toast.LENGTH_SHORT).show();

                        }
                        if(which == 1){
                            Toast.makeText(ViewKYC.this, "Cancel", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.show();
            }
        });



    }

    private void DisplayIdNumber() {
        IdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("KycId")){

                    String KycId= dataSnapshot.child("KycId").getValue().toString();
                    tvIdNumber.setText(KycId);
                    Log.d(TAG, "Success! " + KycId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayPictures() {

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Loading in...");
        progressDialog.show();

        StorageReference storageReference = firebaseStorage.getReference();

        storageReference.child(UserKey).child("Images/Id Front")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .fit()
                        .into(idFront);
            }
        });

        storageReference.child(UserKey).child("Images/Id Back").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().into(idBack);
                progressDialog.dismiss();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.allPosts:{
                Toast.makeText(ViewKYC.this,"All Posts...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewKYC.this,View_All_Posts.class));
            }break;

            case R.id.allUsers:{
                Toast.makeText(ViewKYC.this,"All Users...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewKYC.this,View_all_users.class));
            }break;


            case R.id.feedback:{

                startActivity(new Intent(ViewKYC.this, chat_list.class));
                Toast.makeText(ViewKYC.this,"Feedback...",Toast.LENGTH_SHORT).show();
            }break;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
