package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class View_All_Posts extends AppCompatActivity {

    private RecyclerView recyclerView;
    DatabaseReference mDatabase;

    private final List<taskDescription> taskList= new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private xxxTaskDescriptionAdapter TaskAdapter;
    public static final String TAG = xxx.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__all__posts);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.adminRecylerView1);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Tasks");
        mDatabase.keepSynced(true);





        //*****************************************************
        TaskAdapter= new xxxTaskDescriptionAdapter(taskList, this) {
            @Override
            public void onItemClick(int position, View v) {
            }
        };

        linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(TaskAdapter);

        fetchTasksDetails();

        //*****************************************************

        //Recycler ka Coding

     //   recyclerView.setHasFixedSize(true);
    //    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    //    linearLayoutManager.setReverseLayout(true);
    //    linearLayoutManager.setStackFromEnd(true);
    //    recyclerView.setLayoutManager(linearLayoutManager);

/*
//_______________________________________________________________________________________________________________
        FirebaseRecyclerAdapter<taskDescription, Second_Activity.taskViewHolder>
                firebaseRecylerAdapter = new FirebaseRecyclerAdapter<taskDescription, Second_Activity.taskViewHolder>
                (taskDescription.class,R.layout.list_item, Second_Activity.taskViewHolder.class, mDatabase)
        {

            @Override
            protected void populateViewHolder (Second_Activity.taskViewHolder viewHolder, taskDescription model, int position){

                final String PostKey= getRef(position).getKey();

                viewHolder.setTask(model.getTask());
                viewHolder.setWorkValue(model.getWorkValue());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent= new Intent(View_All_Posts.this, viewTaskDescription.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecylerAdapter);
        //_______________________________________________________________________________________________________________
        */
    }
    private void fetchTasksDetails() {



        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists()){
                    taskDescription task_description = dataSnapshot.getValue(taskDescription.class);
                    taskList.add(task_description);
                    TaskAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                Toast.makeText(View_All_Posts.this,"All Posts...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(View_All_Posts.this,View_All_Posts.class));
            }break;

            case R.id.allUsers:{
                Toast.makeText(View_All_Posts.this,"All Users...",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(View_All_Posts.this,View_all_users.class));
            }break;


            case R.id.feedback:{

                startActivity(new Intent(View_All_Posts.this, View_all_Feedbacks.class));
                Toast.makeText(View_All_Posts.this,"Feedback...",Toast.LENGTH_SHORT).show();
            }break;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
