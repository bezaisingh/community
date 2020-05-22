package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class chosenTasks extends AppCompatActivity {

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
        setTitle("Chosen Tasks");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUID=firebaseAuth.getCurrentUser().getUid();



        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        myPostRef= FirebaseDatabase.getInstance().getReference("Tempo").child(currentUID);
        myPostRef.keepSynced(true);

        DisplayChosenTasks();
    }
// Recycler view ka coding
    private void DisplayChosenTasks() {

        myPostRef.keepSynced(true);
        super.onStart();

        // Query myPostsQuery= myPostRef.orderByChild("id").startAt(currentUID).endAt(currentUID +"\uf8ff");

        FirebaseRecyclerAdapter<taskDescription, chosenTasks.taskViewHolder> firebaseRecylerAdapter
                = new FirebaseRecyclerAdapter<taskDescription, chosenTasks.taskViewHolder>
                (taskDescription.class,R.layout.card_list_items, chosenTasks.taskViewHolder.class, myPostRef)
        {
            @Override
            protected void populateViewHolder(taskViewHolder viewHolder, taskDescription model, int position) {


                final String PostKey= getRef(position).getKey();

                viewHolder.setTask(model.getTask());
                viewHolder.setWorkValue(model.getWorkValue());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setID(model.getID());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent= new Intent(chosenTasks.this, viewTaskDescription.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecylerAdapter);
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
