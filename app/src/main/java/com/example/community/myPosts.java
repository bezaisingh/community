package com.example.community;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class myPosts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    DatabaseReference  myPostRef;
    private FirebaseAuth firebaseAuth;
    private  String currentUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("My Posts");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUID=firebaseAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        myPostRef= FirebaseDatabase.getInstance().getReference().child("Tasks");
        myPostRef.keepSynced(true);

        DisplayMyPosts();
    }

// Recycler view ka coding
    private void DisplayMyPosts() {


        myPostRef.keepSynced(true);
        super.onStart();

        Query myPostsQuery= myPostRef.orderByChild("id").startAt(currentUID).endAt(currentUID +"\uf8ff");

        FirebaseRecyclerAdapter<taskDescription, myPosts.taskViewHolder> firebaseRecylerAdapter
                = new FirebaseRecyclerAdapter<taskDescription, myPosts.taskViewHolder>
                (
                        taskDescription.class,
                        R.layout.card_list_items,
                        myPosts.taskViewHolder.class,
                        myPostsQuery)
        {
            @Override
            protected void populateViewHolder(final taskViewHolder viewHolder, final taskDescription model, final int position) {

                myPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String PostKey= getRef(position).getKey();

                        viewHolder.setTask(model.getTask());
                        viewHolder.setWorkValue(model.getWorkValue());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setID(model.getID());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent clickPostIntent= new Intent(myPosts.this, viewTaskDescription.class);
                                clickPostIntent.putExtra("PostKey", PostKey);
                                startActivity(clickPostIntent);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecylerAdapter);
        firebaseRecylerAdapter.notifyDataSetChanged();
    }

    public static class taskViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public taskViewHolder(View itemView){
            super(itemView);
            mView=itemView;

        }
        public void setTask(String task){
            TextView post_Task=mView.findViewById(R.id.tvContents);
            post_Task.setText(task);
        }
        public void setWorkValue(String workValue){
            TextView workRate= mView.findViewById(R.id.tvHeading);
            workRate.setText("₹"+ workValue);
        }
        public void setDate(String date) {
            TextView ListDate= mView.findViewById(R.id.listTvDate);
            ListDate.setText(date);
        }
        public void setTime(String time){

            TextView ListTime=mView.findViewById(R.id.listTvTime);
            ListTime.setText(time);
        }

        public void setID(String ID){
            TextView ListId=mView.findViewById(R.id.tv_cardUid);
           ListId.setText(ID);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                onBackPressed();
                this.finish();
        return super.onOptionsItemSelected(item);
    }

    //Accept request k liye comparison coding


}
