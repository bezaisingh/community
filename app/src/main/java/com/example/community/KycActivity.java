package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KycActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button UpdateKyc,btnUpPP,btnUpIdf,btnUpIdb;
    private ImageView profilePic,idBack,idFront;
    private EditText idNumber;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private  DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private static int PICK_IMAGE1=121,PICK_IMAGE2=122,PICK_IMAGE3=123;
    Uri imagePath1,imagePath2,imagePath3;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    String idNo;

    private  Spinner spinner;
    public static final String TAG = KycActivity.class.getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE1 && resultCode == RESULT_OK && data.getData() != null) {
            imagePath1=data.getData();


            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath1);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE2 && resultCode == RESULT_OK && data.getData() != null) {
            imagePath2=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath2);
                idFront.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE3 && resultCode == RESULT_OK && data.getData() != null) {
            imagePath3=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath3);
                idBack.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("KYC Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog= new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        storageReference= firebaseStorage.getReference();

        userRef= FirebaseDatabase.getInstance().getReference("KYC Details").child(firebaseAuth.getUid());



        UpdateKyc=findViewById(R.id.btnUpdate);
        profilePic=findViewById(R.id.imgProfile);
        idBack=findViewById(R.id.imgIdBack);
        idFront=findViewById(R.id.imgIdFront);
        idNumber=findViewById(R.id.idNumber);
        btnUpPP=findViewById(R.id.btnUpdatePP);
        btnUpIdf=findViewById(R.id.btnUpdateIdf);
        btnUpIdb=findViewById(R.id.btnUpdateIdb);


        spinner=findViewById(R.id.spinIdName);
        final ArrayAdapter<CharSequence >adapter=ArrayAdapter.createFromResource(this, R.array.idList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        btnUpPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading please wait...");
                progressDialog.show();

                firebaseAuth.getCurrentUser();
                if(imagePath1==null){
                    Toast.makeText(KycActivity.this, "Nothing chosen to upload....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else {
                    final StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pictures");
                    final UploadTask uploadTask1 = imageReference1.putFile(imagePath1);

                    uploadTask1.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(KycActivity.this, "DP Upload Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(KycActivity.this, "DP Upload Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }


            }

        });


        btnUpIdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading please wait...");
                progressDialog.show();


                firebaseAuth.getCurrentUser();
                if(imagePath2==null){
                    Toast.makeText(KycActivity.this, "Nothing chosen to upload...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else {
                    final StorageReference imageReference2 = storageReference.child(firebaseAuth.getUid())
                            .child("Images")
                            .child("Id Front");

                    UploadTask uploadTask2 = imageReference2.putFile(imagePath2);

                    uploadTask2.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(KycActivity.this, "ID Front Upload Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(KycActivity.this, "ID Front Upload Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }

            }
        });

        btnUpIdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading please wait...");
                progressDialog.show();


                firebaseAuth.getCurrentUser();
                if(imagePath3==null){
                    Toast.makeText(KycActivity.this, "Nothing chosen to upload...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else {
                final StorageReference imageReference3= storageReference.child(firebaseAuth.getUid()).child("Images").child("Id Back");
                UploadTask uploadTask3= imageReference3.putFile(imagePath3);

                uploadTask3.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KycActivity.this, "ID Back Upload Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(KycActivity.this, "ID Back Upload Successful", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });
            }}
        });

       firebaseDatabase = FirebaseDatabase.getInstance();
       DatabaseReference databaseReference = firebaseDatabase.getReference("User Profiles").child(firebaseAuth.getUid());

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid())
                .child("Images/Profile Pictures")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profilePic);
            }
        });


        storageReference.child(firebaseAuth.getUid())
                .child("Images/Id Front")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get().load(uri).fit().centerCrop().into(idFront);

            }
        });



        storageReference.child(firebaseAuth.getUid()).child("Images/Id Back").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(idBack);

            }
        });




//Select Profile Picture

       profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"),PICK_IMAGE1);
            }
        });




//Select ID front

        idFront.setOnClickListener(new View.OnClickListener() {
             @Override
                  public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select image"),PICK_IMAGE2);

    }
});

        idBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"),PICK_IMAGE3);
            }
        });



//



//Displaying the data from database//

        firebaseDatabase =FirebaseDatabase.getInstance();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("KycId")){

                    String KycId= dataSnapshot.child("KycId").getValue().toString();
                    idNumber.setText(KycId);
                    Log.d(TAG, "Success! " + KycId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UpdateKyc.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              if(validate()) {
                  firebaseAuth.getCurrentUser();
                  sendUserData();

                  Intent myPostIntent =new Intent(KycActivity.this ,Second_Activity.class);
                  myPostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(myPostIntent);
                  finish();
              }

          }
      });

    }

    private void sendUserData() {

        idNo=idNumber.getText().toString();
        userRef.child("KycId").setValue(idNo);

        String text = spinner.getSelectedItem().toString();
        userRef.child("KycIdName").setValue(text);


        // userRef.child("ProfilePicUri").setValue(ProfPicUrl);
        // userRef.child("IdFrontUri").setValue(IdFrontUrl);
        // userRef.child("IdBackUri").setValue(IdBackUrl);
    }


    private Boolean validate(){
        Boolean result = false;

        idNo =idNumber.getText().toString();

        if(idNo.isEmpty()){
            Toast.makeText(this, "Please enter ID Number", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }

        return result;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text= parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
