package com.example.community;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Second_Activity extends AppCompatActivity implements
        View.OnClickListener {

    private Button btnPost, btnTRY;
    private TextView tvDate, tvTime, navProfileName, txtDate, txtTime;
    private EditText etAddress, etLocality, etWorkValue, tvPost;
    private RecyclerView recyclerView;
    DatabaseReference databaseReference, mDatabase, userRef;
    private CircleImageView navProfilImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    String currentUser, AmPm;
    private long counter, countpost = 0;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    private final List<taskDescription> taskList= new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private xxxTaskDescriptionAdapter TaskAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        firebaseStorage = FirebaseStorage.getInstance();

        currentUser = firebaseAuth.getCurrentUser().getUid();


        StorageReference storageReference = firebaseStorage.getReference(firebaseAuth.getUid());
        userRef = FirebaseDatabase.getInstance().getReference("User Profiles").child(firebaseAuth.getUid());

        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tasks");
        mDatabase.keepSynced(true);

        txtDate = findViewById(R.id.in_date);
        txtTime = findViewById(R.id.in_time);
        etAddress = findViewById(R.id.postAddress);
        etLocality = findViewById(R.id.locality);
        etWorkValue = findViewById(R.id.cost);
        btnPost = findViewById(R.id.postTask);
        tvPost = findViewById(R.id.tvPost);

        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);
        closeKeyboard();


        recyclerView = findViewById(R.id.recylerView1);

        //*****************************************************
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        TaskAdapter= new xxxTaskDescriptionAdapter(taskList, this) {
            @Override
            public void onItemClick(int position, View v) {
            }
        };
        linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setAdapter(TaskAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        fetchTasksDetails();
        //*****************************************************
/*
//_________________________________________________________________________________________________________

        recyclerView = findViewById(R.id.recylerView1);
        recyclerView.setHasFixedSize(true);
//the 4 lines start from here
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//recyclerView.setLayoutManager(new LinearLayoutManager(this));// use this to sort and remove the above 4 lines

        //recyclerView.setAdapter(adapter);
        //listItems= new ArrayList<>();
        //adapter=new Adapter(listItems,this);
       // DisplayAllPosts();
//_________________________________________________________________________________________________________
*/
        btnTRY = findViewById(R.id.btnTRY);
        btnTRY.setVisibility(View.INVISIBLE);

        btnTRY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Second_Activity.this, xxx.class));
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTasks();
                resetText();
                closeKeyboard();
            }
        });

        dl = findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();



        final NavigationView nav_view = findViewById(R.id.nav_view);
        View naView = nav_view.inflateHeaderView(R.layout.navigation_header);

        navProfileName = naView.findViewById(R.id.navUserName);
        navProfilImage = naView.findViewById(R.id.navHeaderImage);

        storageReference.child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(navProfilImage);

            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String fname = dataSnapshot.child("fname").getValue().toString();
                navProfileName.setText(fname);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.home) {
                    startActivity(new Intent(Second_Activity.this, Second_Activity.class));
                    Toast.makeText(Second_Activity.this, "Home", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.myProfile) {
                    startActivity(new Intent(Second_Activity.this, myProfileActivity.class));
                    Toast.makeText(Second_Activity.this, "My Profile", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.about) {
                    startActivity(new Intent(Second_Activity.this, About.class));
                    Toast.makeText(Second_Activity.this, "About", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.myPosts) {
                    startActivity(new Intent(Second_Activity.this, myPosts.class));

                    Toast.makeText(Second_Activity.this, "My Posts", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.menu_logout) {

                    firebaseAuth.signOut();

                    Intent logoutIntent = new Intent(Second_Activity.this, MainActivity.class);
                    logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                    finish();
                    Toast.makeText(Second_Activity.this, "Logout Success", Toast.LENGTH_SHORT).show();


                } else if (id == R.id.chosenPost) {

                    startActivity(new Intent(Second_Activity.this, chosenTasks.class));
                    Toast.makeText(Second_Activity.this, "Chosen Tasks", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.chatMenu) {
                    startActivity(new Intent(Second_Activity.this, chat_list.class));
                    Toast.makeText(Second_Activity.this, "Chats", Toast.LENGTH_SHORT).show();

                } else if(id==R.id.share) {
                    Intent shareIntent= new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Checkout this awesome app, download from playstore https://www.google.bijay.in/");
                    startActivity(Intent.createChooser(shareIntent, "Share Using..."));

                    //Toast.makeText(Second_Activity.this, "Share...", Toast.LENGTH_SHORT).show();
                } else if(id==R.id.UserFeedback){

                    Intent feedbackInent= new Intent(Second_Activity.this, Feedback.class);
                    startActivity(feedbackInent);
                }
                return true;
            }
        });
    }

/*
// Recycler view ka coding////////////////////////////////////////////////////
    private void DisplayAllPosts() {
        //Query SortPostInDescendingOrder= mDatabase.orderByChild("counter");// use this to sort
        //Query SortPostInDescendingOrder= postRef.orderByChild("counter").startAt(0).endAt(9999);

        mDatabase.keepSynced(true);
        super.onStart();
        // super.onResume();
        final FirebaseRecyclerAdapter<taskDescription, taskViewHolder> firebaseRecylerAdapter = new FirebaseRecyclerAdapter<taskDescription, taskViewHolder>
                (taskDescription.class,R.layout.list_item,taskViewHolder.class, mDatabase)
        {

            @Override
            protected void populateViewHolder (final taskViewHolder viewHolder, final taskDescription model, final int position)
            {
///////////////////////////////


                final String PostKey= getRef(position).getKey();

                viewHolder.setTask(model.getTask());
                viewHolder.setWorkValue(model.getWorkValue());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent= new Intent(Second_Activity.this, viewTaskDescription.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });
///////////////////////////////

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


    }

// Recycler view ka coding ends here..............////////////////////////////////////////////////////
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.adminMenu:{
                startActivity(new Intent(Second_Activity.this, View_All_Posts.class));

            }
        }

        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v == txtDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == txtTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);


            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            if(hourOfDay>=12){
                                AmPm ="PM";
                            }else{
                                AmPm="AM";
                            }
                            txtTime.setText(String.format("%02d:%02d",hourOfDay,minute) +" "+AmPm);

                           // txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    private void addTasks(){

databaseReference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()){

            countpost=dataSnapshot.getChildrenCount();
            counter= 9999-countpost;

        }else{

            countpost=0;

        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
//new sorting technique ends here...

        String task = tvPost.getText().toString().trim();
        String date= txtDate.getText().toString().trim();
        String time= txtTime.getText().toString().trim();
        String address= etAddress.getText().toString().trim();
        String locality= etLocality.getText().toString().trim();
        String workValue= etWorkValue.getText().toString().trim();
        String uid= currentUser;



        if (!TextUtils.isEmpty(task)  ){

            // taskId is PostKey in database

            String taskId= databaseReference.push().getKey();
            taskDescription taskdesc= new taskDescription(uid,task,date,time,address,locality,workValue,taskId);

            databaseReference.child(taskId).setValue(taskdesc);
            databaseReference.child(taskId).child("counter").setValue(counter);

            Toast.makeText(Second_Activity.this,"Task Added",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(Second_Activity.this,"Nothing to post... ",Toast.LENGTH_SHORT).show();
        }

    }

 private void resetText(){

        etAddress.setText("");
        etLocality.setText("");
        etWorkValue.setText("");
        tvPost.setText("");
        //tvDate.setText("");
        //tvTime.setText("");

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

  //Admin Menu ka Coding

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentUser.equals("skd5QFtEKuSCUpWb3PE8DZGSjCs2")){
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        }


        return true;
    }

    //

    private void fetchTasksDetails() {

        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists()){
                    taskDescription task_description = dataSnapshot.getValue(taskDescription.class);
                    taskList.add(task_description);
                    TaskAdapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();

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