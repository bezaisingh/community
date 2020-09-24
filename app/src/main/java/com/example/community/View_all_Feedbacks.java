package com.example.community;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class View_all_Feedbacks extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    DatabaseReference feedbackRef;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private LinearLayoutManager linearLayoutManager;
    public static final String TAG = View_all_Feedbacks.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all__feedbacks);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Feedbacks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.feedbackRecyclerView);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        feedbackRef = FirebaseDatabase.getInstance().getReference().child("Feedback");
        feedbackRef.keepSynced(true);


        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        FirebaseRecyclerAdapter<feedback_Model, View_all_Feedbacks.feedBackViewHolder>
                firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<feedback_Model, View_all_Feedbacks.feedBackViewHolder>
                (
                        feedback_Model.class,
                        R.layout.layout_feedback_list,
                        View_all_Feedbacks.feedBackViewHolder.class,
                        feedbackRef
                ) {
            @Override
            protected void populateViewHolder(final View_all_Feedbacks.feedBackViewHolder viewHolder, feedback_Model model, int position) {

                final String UserKey=getRef(position).getKey();

                Log.d(TAG, "On Click In adapter PostKey: " + UserKey);

                viewHolder.setFeedback(model.getFeedback());

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

                //to display the user name
                DatabaseReference userRef= firebaseDatabase.getReference().child("User Profiles");
                userRef.child(UserKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile= dataSnapshot.getValue(UserProfile.class);

                        viewHolder.setFname(userProfile.getFname() +" "+ userProfile.getMname() + " " + userProfile.getLname() );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //to display the user name ends here.............

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "View Profile",
                                        "Send Message"
                                };
                        AlertDialog.Builder builder= new AlertDialog.Builder(View_all_Feedbacks.this);
                        builder.setTitle("Select Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(which == 0){

                                    Intent userPassIntent= new Intent(View_all_Feedbacks.this, view_request_profile.class);
                                    userPassIntent.putExtra("UserTopass", UserKey);
                                    startActivity(userPassIntent);

                                }
                                if(which == 1){
                                    Intent userPassIntent= new Intent(View_all_Feedbacks.this, chat_activity.class);
                                    userPassIntent.putExtra("UserTopass", UserKey);
                                    startActivity(userPassIntent);
                                }

                            }
                        });
                        builder.show();
                    }
                });




            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    public static class feedBackViewHolder extends RecyclerView.ViewHolder{
View mView;
        public feedBackViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setFeedback(String feedback) {
            TextView mFeedback=mView.findViewById(R.id.Admin_view_feedback);
            mFeedback.setText(feedback);
        }

        public void setProfileImage(Uri uri){
            CircleImageView theProfPic =mView.findViewById(R.id.feedProfPic);
            Picasso.get().load(uri).into(theProfPic);

        }

        public void setFname(String fname)
        {
            TextView firstName=mView.findViewById(R.id.feeUserName);
            firstName.setText(fname);
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
                Toast.makeText(View_all_Feedbacks.this,"All Posts...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(View_all_Feedbacks.this,View_All_Posts.class));
            }break;

            case R.id.allUsers:{
                Toast.makeText(View_all_Feedbacks.this,"All Users...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(View_all_Feedbacks.this,View_all_users.class));
            }break;

            case R.id.feedback:{
                startActivity(new Intent(View_all_Feedbacks.this, View_all_Feedbacks.class));
                Toast.makeText(View_all_Feedbacks.this,"Feedback...",Toast.LENGTH_SHORT).show();
            }break;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
