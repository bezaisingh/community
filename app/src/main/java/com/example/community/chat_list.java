package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class chat_list extends AppCompatActivity {

    private RecyclerView myChatList;
    private DatabaseReference chatref, userRef, CuserRef;
    private FirebaseAuth firebaseAuth;
    private String online_user_id;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ImageView navProfilImage;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private TextView navProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Chats");

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

      storageReference = firebaseStorage.getReference();

        online_user_id= firebaseAuth.getCurrentUser().getUid();
        chatref= FirebaseDatabase.getInstance().getReference().child("Messages").child(online_user_id);
        userRef=FirebaseDatabase.getInstance().getReference().child("User Profiles");
        CuserRef= FirebaseDatabase.getInstance().getReference("User Profiles").child(firebaseAuth.getUid());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        myChatList= findViewById(R.id.chat_list);
        myChatList.setHasFixedSize(true);
        LinearLayoutManager  linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myChatList.setLayoutManager(linearLayoutManager);


        DisplayAllChats();

        //Navigation Bar ka coding starts//

        dl=findViewById(R.id.dl);
        abdt= new ActionBarDrawerToggle(this,dl,R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final NavigationView nav_view = findViewById(R.id.nav_view);
        View naView = nav_view.inflateHeaderView(R.layout.navigation_header);

        navProfileName=naView.findViewById(R.id.navUserName);
        navProfilImage= naView.findViewById(R.id.navHeaderImage);

        StorageReference storageReference = firebaseStorage.getReference(firebaseAuth.getUid());
        storageReference.child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(navProfilImage);

            }
        });
        CuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String fname= dataSnapshot.child("fname").getValue().toString();
                navProfileName.setText(fname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id=item.getItemId();


                if (id==R.id.home)
                {
                    startActivity(new Intent(chat_list.this,Second_Activity.class));
                    Toast.makeText(chat_list.this,"Home",Toast.LENGTH_SHORT).show();
                }

                else if (id == R.id.chatMenu) {
                    startActivity(new Intent(chat_list.this, chat_list.class));
                    Toast.makeText(chat_list.this, "Chats", Toast.LENGTH_SHORT).show();

                }
                else if (id==R.id.myProfile)
                {
                    startActivity(new Intent(chat_list.this,myProfileActivity.class));
                    Toast.makeText(chat_list.this,"My Profile",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.about)
                {   startActivity(new Intent(chat_list.this,About.class));
                    Toast.makeText(chat_list.this,"Settings",Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.myPosts){
                    startActivity(new Intent(chat_list.this,myPosts.class));

                    Toast.makeText(chat_list.this,"My Posts",Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.chosenPost) {

                    startActivity(new Intent(chat_list.this, chosenTasks.class));
                    Toast.makeText(chat_list.this, "Chosen Tasks", Toast.LENGTH_SHORT).show();
                }
                else if (id==R.id.menu_logout){

                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(chat_list.this, MainActivity.class));
                    Toast.makeText(chat_list.this,"Logout Success",Toast.LENGTH_SHORT).show();

                }
                else if(id==R.id.share) {
                    Intent shareIntent= new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Checkout this awesome app, download from playstore https://www.google.bijay.in/");
                    startActivity(Intent.createChooser(shareIntent, "Share Using..."));

                    //Toast.makeText(Second_Activity.this, "Share...", Toast.LENGTH_SHORT).show();
                } else if(id==R.id.UserFeedback){

                    Intent feedbackInent= new Intent(chat_list.this, Feedback.class);
                    startActivity(feedbackInent);
                }
                return true;
            }
        });


    }

    private void DisplayAllChats()
    {
        FirebaseRecyclerAdapter<chats, chatsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<chats, chatsViewHolder>
                (
                        chats.class,
                        R.layout.user_display_layout,
                        chatsViewHolder.class,
                        chatref
                )
        {
            @Override
            protected void populateViewHolder(final chatsViewHolder viewHolder, chats model, int position)
            {

                final String usersIDs= getRef(position).getKey();

                userRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        viewHolder.setFullname(userProfile.getFname() + " " +userProfile.getMname() + " " + userProfile.getLname());
                        viewHolder.setMobNo(userProfile.getMobileNumber());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent chatIntent= new Intent(chat_list.this, chat_activity.class);
                                chatIntent.putExtra("UserTopass", usersIDs);
                                startActivity(chatIntent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

// To display Profile [ic in Recycler, Niche v hai thora Viewholder pe
                StorageReference storageReference = firebaseStorage.getReference();
                storageReference.child(usersIDs).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       //Picasso.get().load(uri).fit().centerCrop().into(profileImage);
                        viewHolder.setProfileImage(uri);
                    }
                });
                // To display Profile [ic in Recycler ends here....
            }
        };

        myChatList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class chatsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setProfileImage(Uri uri){
            CircleImageView theProfPic =mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(uri).into(theProfPic);

        }


        public void setFullname(String fullname)
        {
            TextView myName= mView.findViewById(R.id.all_users_profile_fullName);
            myName.setText(fullname);
        }

       public void setMobNo(String mobNo)
        {
            TextView chatDate= mView.findViewById(R.id.all_users_status);
            chatDate.setText(" "+ mobNo);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
