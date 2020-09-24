package com.example.community;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class xxx extends AppCompatActivity {

    private RecyclerView recyclerView;
    DatabaseReference mDatabase;


    private final List<taskDescription> taskList= new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private xxxTaskDescriptionAdapter TaskAdapter;
    public static final String TAG = xxx.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xxx);


        recyclerView = findViewById(R.id.recycler_xxx);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tasks");
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

}
