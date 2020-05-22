package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_all_users extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    DatabaseReference mDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private LinearLayoutManager linearLayoutManager;
    public static final String TAG = View_all_users.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("View All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recycler_View_All_Users);

        firebaseStorage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("User Profiles");
        mDatabase.keepSynced(true);


        //Recycler ka Coding

        recyclerView.setHasFixedSize(true);
        linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        FirebaseRecyclerAdapter<UserProfile, View_all_users.taskViewHolder>
                firebaseRecylerAdapter = new FirebaseRecyclerAdapter<UserProfile, View_all_users.taskViewHolder>
                (
                        UserProfile.class,
                        R.layout.layout_view_all_users,
                        View_all_users.taskViewHolder.class,
                        mDatabase
                )
        {

            @Override
            protected void populateViewHolder (final View_all_users.taskViewHolder viewHolder, UserProfile model, int position){


                final String UserKey=getRef(position).getKey();

                Log.d(TAG, "On Click In adapter PostKey: " + UserKey);

                viewHolder.setFname(model.getFname() + " "+ model.getMname() + " " + model.getLname() );
                viewHolder.setMobileNumber("Phone: "+ model.getMobileNumber());
                viewHolder.setEmailId("e-mail: "+model.getEmailId());
                viewHolder.setUPIid("UPI Id: "+model.getUPIid());
                viewHolder.setDOB("DOB: "+model.getDOB());
                viewHolder.setGender("Gender: "+model.getGender());
                viewHolder.setAddress("Address: "+model.getAddress());
                viewHolder.setDistrict("District: "+model.getDistrict());
                viewHolder.setState("State: "+model.getState());
                viewHolder.setCountry("Country: "+model.getCountry());


                // To display Profile Pic in Recycler, Niche v hai thora Viewholder pe
                StorageReference profilePicRef = firebaseStorage.getReference();
                profilePicRef.child(UserKey).child("Images/Profile Pictures").getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        viewHolder.setProfileImage(uri);
                    }
                });
                // To display Profile pic in Recycler ends here....


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ViewKyc= new Intent(View_all_users.this, ViewKYC.class);
                        ViewKyc.putExtra("UserKey", UserKey);
                        startActivity(ViewKyc);
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecylerAdapter);



    }

    public static class taskViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public taskViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView;
        }

        public void setProfileImage(Uri uri){
            CircleImageView theProfPic =mView.findViewById(R.id.Admin_view_profilePic);
            Picasso.get().load(uri).into(theProfPic);

        }


        public void setFname(String fname)
        {
            TextView firstName=mView.findViewById(R.id.Admin_view_tvName);
            firstName.setText(fname);
        }

        public void setDOB(String DOB)
        {
            TextView dob=mView.findViewById(R.id.Admin_view_tvDOB);
            dob.setText(DOB);
        }
        public void setGender(String gender)
        {
            TextView Vgender=mView.findViewById(R.id.Admin_view_tvGender);
            Vgender.setText(gender);
        }

        public void setEmailId(String userEmail){
            TextView email=mView.findViewById(R.id.Admin_view_tvEmail);
            email.setText(userEmail);
        }
        public void setAddress(String address)
        {
            TextView Addr=mView.findViewById(R.id.Admin_view_tvAddress);
            Addr.setText(address);
        }
        public void setState(String state)
        {
            TextView Vstate=mView.findViewById(R.id.Admin_view_tvState);
            Vstate.setText(state);
        }
        public void setDistrict(String district)
        {
            TextView dist=mView.findViewById(R.id.Admin_view_tvDistrict);
            dist.setText(district);
        }
        public void setUPIid(String UPIid)
        {
            TextView upiId=mView.findViewById(R.id.Admin_view_tvUPI);
            upiId.setText(UPIid);
        }


            public void setCountry(String country)
        {
            TextView Vcountry=mView.findViewById(R.id.Admin_view_tvCountry);
            Vcountry.setText(country);
        }

            public void setMobileNumber(String mobileNumber)
        {
            TextView mobNo= mView.findViewById(R.id.Admin_view_mobNum);
            mobNo.setText(mobileNumber);
        }

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
                Toast.makeText(View_all_users.this,"All Posts...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(View_all_users.this,View_All_Posts.class));
            }break;

            case R.id.allUsers:{
                Toast.makeText(View_all_users.this,"All Users...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(View_all_users.this,View_all_users.class));
            }break;

            case R.id.feedback:{
                startActivity(new Intent(View_all_users.this, View_all_Feedbacks.class));
                Toast.makeText(View_all_users.this,"Feedback...",Toast.LENGTH_SHORT).show();
            }break;
        }
      onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
