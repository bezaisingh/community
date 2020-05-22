package com.example.community;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class viewTaskDescription extends AppCompatActivity {

    private Button btnAcept, btnDelete, btnPayment;
    private TextView vpost,vLandmark, vAddress, vWorkVal, vDate, vTime, receiverUser, name;
    private  String PostKey, CURRENT_STATE, currentUserId;
    private DatabaseReference clickPostRef, RequestedRef, AcceptedRef, toPath;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private  FirebaseStorage firebaseStorage;
    private CircleImageView imgBtnUserPic;
    String receiver, saveCurrentDate,sender;
    String UserTopass;
    private CardView cardView;
    public static final String TAG = viewTaskDescription.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_description);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Task Description");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUserId=firebaseAuth.getCurrentUser().getUid();

        PostKey= getIntent().getExtras().get("PostKey").toString();

        clickPostRef= FirebaseDatabase.getInstance().getReference().child("Tasks").child(PostKey);
        toPath=FirebaseDatabase.getInstance().getReference().child("Tempo").child(currentUserId).child(PostKey);

        RequestedRef =FirebaseDatabase.getInstance().getReference().child("Requested");
        AcceptedRef =FirebaseDatabase.getInstance().getReference().child("Accepted");

UserTopass="empty";

        btnAcept= findViewById(R.id.btnAccept);
        btnPayment=findViewById(R.id.btnPayment);
        vpost=findViewById(R.id.viewPost);
        vLandmark=findViewById(R.id.viewLandmark);
        vAddress=findViewById(R.id.viewAddress);
        vWorkVal=findViewById(R.id.viewWorkValue);
        vDate= findViewById(R.id.viewDate);
        vTime=findViewById(R.id.viewTime);
        receiverUser =findViewById(R.id.tvReceiver);
        name= findViewById(R.id.name);
        imgBtnUserPic=findViewById(R.id.imgBtnUserPic);
        cardView=findViewById(R.id.cardViewAcceptBtn);
        btnDelete=findViewById(R.id.deleteTask);

        CURRENT_STATE="not_accepted";

//Delete Button .............
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[]= new CharSequence[]
                        {
                                "Yes, Delete",
                                "No, Cancel"
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(viewTaskDescription.this);
                builder.setTitle("Are you sure to Delete this post...");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){

                            clickPostRef.removeValue();
                            RequestedRef.child(PostKey).removeValue();
                            AcceptedRef.child(PostKey).removeValue();


                            Intent myPostIntent =new Intent(viewTaskDescription.this ,myPosts.class);
                            myPostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(myPostIntent);
                            finish();

                            Toast.makeText(viewTaskDescription.this, "Deletion Successful", Toast.LENGTH_SHORT).show();
                            Toast.makeText(viewTaskDescription.this, "Claim Sent...", Toast.LENGTH_SHORT).show();

                        }
                        if(which == 1){
                            Toast.makeText(viewTaskDescription.this, "Cancel", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.show();
            }
        });








        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    String task= dataSnapshot.child("task").getValue().toString();
                    String address= dataSnapshot.child("address").getValue().toString();
                    String date= dataSnapshot.child("date").getValue().toString();
                    String time= dataSnapshot.child("time").getValue().toString();
                    String workValue= dataSnapshot.child("workValue").getValue().toString();
                    String landmark = dataSnapshot.child("landmark").getValue().toString();
                    receiver =dataSnapshot.child("id").getValue().toString();

                    vpost.setText(task);
                    vAddress.setText("Address: "+ address);
                    vWorkVal.setText("Amount to be Earned: ₹"+workValue);
                    vDate.setText(" "+ date);
                    vTime.setText("   "+time);
                    vLandmark.setText("Landmark/Locality: "+landmark);
                    receiverUser.setText(receiver);


                    MaintenanceOfButtons();


                    imgBtnUserPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(UserTopass.equals("empty")){
                                Toast.makeText(viewTaskDescription.this, "No requests yet", Toast.LENGTH_SHORT).show();
                            }else{

                                Intent userPassIntent= new Intent(viewTaskDescription.this, view_request_profile.class);
                                userPassIntent.putExtra("UserTopass", UserTopass);
                                startActivity(userPassIntent);

                            }

                        }
                    });




                    if(!currentUserId.equals(receiver)){

                        UserTopass=receiver;

                        //Displaying the data from database//
                        DisplayJobProvider();



                        btnAcept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnAcept.setEnabled(false);

                                if(CURRENT_STATE.equals("not_accepted")){

                                    AcceptTask();
                                }

                                if(CURRENT_STATE.equals("request_sent")){


                                    CancelRequest();
                                }

                            }


                        });
                    }else{
                        btnAcept.setText("No Requests");
                        //btnAcept.setVisibility(View.INVISIBLE);


                        btnAcept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                                if(CURRENT_STATE.equals("request_received")){

                                    AcceptRequest();

                                }
                                if(CURRENT_STATE.equals("Pay")){

                                    Intent userPassIntent= new Intent(viewTaskDescription.this, PaymentRedirection.class);
                                    userPassIntent.putExtra("UserTopass", UserTopass);
                                    userPassIntent.putExtra("PostKey", PostKey);

                                    startActivity(userPassIntent);

                                    Toast.makeText(viewTaskDescription.this, "Add payment Gateway", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[]= new CharSequence[]
                        {
                                "View Profile",
                                "Send Message"
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(viewTaskDescription.this);
                builder.setTitle("Select Options");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){

                            Intent userPassIntent= new Intent(viewTaskDescription.this, view_request_profile.class);
                            userPassIntent.putExtra("UserTopass", UserTopass);
                            startActivity(userPassIntent);

                        }
                        if(which == 1){
                            Intent userPassIntent= new Intent(viewTaskDescription.this, chat_activity.class);
                            userPassIntent.putExtra("UserTopass", UserTopass);
                            startActivity(userPassIntent);
                        }

                    }
                });
                builder.show();
            }
        });

/////////////////////////////////////////////////////////////////////////////////////////////////////

//Payment Claim Button.............

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[]= new CharSequence[]
                        {
                                "Request for Payment",
                                "Cancel"
                        };
                AlertDialog.Builder builder= new AlertDialog.Builder(viewTaskDescription.this);
                builder.setTitle("If Task is Complete...");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){

                            claimPayment();

                            Toast.makeText(viewTaskDescription.this, "Claim Sent...", Toast.LENGTH_SHORT).show();


                        }
                        if(which == 1){
                            Toast.makeText(viewTaskDescription.this, "Cancel", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.show();
            }
        });

        //Payment Claim Button.............ends here.......
    }




    private void claimPayment() {
        RequestedRef.child(PostKey).child(receiver)
                .child("request_type").setValue("Pay")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            btnAcept.setEnabled(true);
                            CURRENT_STATE = "Pay";
                            btnPayment.setText("Pay Request Sent");
                            //btnAcept.setText("Pay");
                           RequestedRef.child(PostKey).child(currentUserId).child("request_type").setValue("Claimed");
                        }
                    }
                });

    }


    //Copy data fom the table and Paste to another table

    private void copyRecord(DatabaseReference clickPostRef,final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d(TAG, "Success!");
                        } else {
                            Log.d(TAG, "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        clickPostRef.addListenerForSingleValueEvent(valueEventListener);
    }

    //Copy data fom the table and Paste to another table ends here......//


// Reverse Copy Starts here

    private void RevcopyRecord(DatabaseReference toPath,final DatabaseReference clickPostRef) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clickPostRef.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d(TAG, "Success!");
                        } else {
                            Log.d(TAG, "Copy failed!");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        clickPostRef.addListenerForSingleValueEvent(valueEventListener);
    }

    //Copy data fom the table and Paste to another table ends here......//


    private void DisplayJobProvider() {


        DatabaseReference databaseReference = firebaseDatabase.getReference("User Profiles").child(receiver);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                name.setText(userProfile.getFname() + " " + userProfile.getLname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(receiver).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(imgBtnUserPic);

            }
        });
    }


    private void AcceptTask() {

        copyRecord(clickPostRef,toPath);

        RequestedRef.child(PostKey).child(currentUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            RequestedRef.child(PostKey).child(receiver).child("sender").setValue(currentUserId);
                            clickPostRef.child("Status").setValue("Taken");


                            RequestedRef.child(PostKey).child(receiver)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btnAcept.setEnabled(true);
                                                CURRENT_STATE="request_sent";
                                                btnAcept.setText("Cancel Request");
                                            }

                                        }
                                    });
                        }

                    }
                });


    }


    private void CancelRequest() {

        RequestedRef.child(PostKey).child(currentUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            RequestedRef.child(PostKey).child(receiver)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btnAcept.setEnabled(true);
                                                CURRENT_STATE="not_accepted";
                                                btnAcept.setText("Accept");
                                            }

                                        }
                                    });
                        }

                    }
                });
        clickPostRef.child("Status").removeValue();
        RevcopyRecord(toPath, clickPostRef);
        toPath.removeValue();

    }


    private void AcceptRequest() {
        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate= currentDate.format(calForDate.getTime());

      AcceptedRef.child(PostKey).child("Chosen_User")
              .setValue(sender);

        AcceptedRef.child(PostKey).child(receiver)
                .child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            AcceptedRef.child(PostKey).child(currentUserId)
                                    .child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){


                                                RequestedRef.child(PostKey).child(currentUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    RequestedRef.child(PostKey).child(receiver)
                                                                            .child("request_type").setValue("Accepted")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        btnAcept.setEnabled(true);
                                                                                        CURRENT_STATE="Accepted";
                                                                                        btnAcept.setText("Accepted");
                                                                                        RequestedRef.child(PostKey).child(sender).child("request_type")
                                                                                                .setValue("RequestAccepted");

                                                                                    }

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });


                                            }

                                        }
                                    });

                        }

                    }
                });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void MaintenanceOfButtons() {

        // Delete button ka coding..........
        btnPayment.setVisibility(View.INVISIBLE);

        if(currentUserId.equals(receiver)){
            btnDelete.setVisibility(View.VISIBLE);

        }else
        {
            btnDelete.setVisibility(View.INVISIBLE);
        }

        //Claim Payments Button...........

        if( CURRENT_STATE.equals("RequestAccepted"))
        {
            btnPayment.setVisibility(View.VISIBLE);
        }




        firebaseDatabase =FirebaseDatabase.getInstance();

        AcceptedRef.child(PostKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Chosen_User"))
                {
                    String chosen_user = dataSnapshot.child("Chosen_User").getValue().toString();
                    UserTopass=chosen_user;

                    //Displaying the data from database//

                    DatabaseReference databaseReference =firebaseDatabase.getReference("User Profiles").child(chosen_user);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserProfile userProfile= dataSnapshot.getValue(UserProfile.class);
                            name.setText(userProfile.getFname() + " " + userProfile.getLname());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                    });
                    StorageReference storageReference = firebaseStorage.getReference();
                    storageReference.child(chosen_user).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).fit().centerCrop().into(imgBtnUserPic);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        clickPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Status")) {

                    String status = dataSnapshot.child("Status").getValue().toString();
                    CURRENT_STATE = status;

                    if (!currentUserId.equals(receiver) && !currentUserId.equals(sender)) {

                        if (CURRENT_STATE.equals("Taken")) {
                            UserTopass=receiver;
                            displayReceiverData();
                            CURRENT_STATE = "Taken";
                            btnAcept.setText("Not Available");
                            DisplayJobProvider();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        RequestedRef.child(PostKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        /*if(!dataSnapshot.hasChild(currentUserId)&& !currentUserId.equals(receiver) && !currentUserId.equals(sender)){

                            CURRENT_STATE="Taken";
                            btnAcept.setText("Already Taken Babu");
                            if(CURRENT_STATE.equals("Taken")){
                                DisplayJobProvider();
                            }
                        }*/



                        if(dataSnapshot.hasChild(currentUserId)) {
                            String request_type = dataSnapshot.child(currentUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")){

                                CURRENT_STATE="request_sent";
                                btnAcept.setText("Cancel Request");

                            }
                            if(request_type.equals("received")){

                                btnAcept.setVisibility(View.VISIBLE);
                                btnAcept.setEnabled(true);

                                sender= dataSnapshot.child(currentUserId).child("sender").getValue().toString();

                                CURRENT_STATE="request_received";
                                btnAcept.setText("Accept Request");

                                UserTopass=sender;

                                //Displaying the data from database//


                                DatabaseReference databaseReference =firebaseDatabase.getReference("User Profiles").child(sender);
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserProfile userProfile= dataSnapshot.getValue(UserProfile.class);
                                        name.setText(userProfile.getFname() +  " " + userProfile.getLname());

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                StorageReference storageReference = firebaseStorage.getReference();
                                storageReference.child(sender).child("Images/Profile Pictures").getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.get().load(uri).fit().centerCrop().into(imgBtnUserPic);

                                    }
                                });




                            }
                            if(request_type.equals("Accepted")){

                                CURRENT_STATE="Accepted";
                                btnAcept.setText("Accepted");

                            }
                            if(request_type.equals("RequestAccepted")){

                                UserTopass=receiver;

                                CURRENT_STATE="RequestAccepted";
                                btnAcept.setText("Request Accepted");
                                btnPayment.setVisibility(View.VISIBLE);
                                displayReceiverData();





                            }
                            if(request_type.equals("Pay")){


                                CURRENT_STATE="Pay";
                                btnAcept.setText("Pay");
                                btnDelete.setVisibility(View.INVISIBLE);
                            }
                            if(request_type.equals("Claimed")){

                                UserTopass=receiver;

                                CURRENT_STATE="Claimed";
                                btnAcept.setText("Claimed");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
    //Displaying the data from database//
    private void displayReceiverData() {
        DatabaseReference databaseReference =firebaseDatabase.getReference("User Profiles").child(receiver);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile= dataSnapshot.getValue(UserProfile.class);
                name.setText(userProfile.getFname() + " " + userProfile.getLname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(receiver).child("Images/Profile Pictures").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(imgBtnUserPic);

                    }
                });


    }
}
