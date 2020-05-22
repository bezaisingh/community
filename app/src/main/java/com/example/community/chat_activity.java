package com.example.community;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chat_activity extends AppCompatActivity {
    private ImageButton sendMsgBtn, sendImgBtn;
    private EditText userMsgInput;
    private RecyclerView userMessagesList;
    private final List<Messages>messagesList= new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    String messageReceiverId, messageReceiverName, messageSenderId, saveCurrentDate, saveCurrentTime;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    DatabaseReference userNameRef, databaseReference;
    private TextView receiverName;
    private CircleImageView receiverImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        final Toolbar chat_toolbar=findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendImgBtn=findViewById(R.id.sendImgBtn);
        sendMsgBtn=findViewById(R.id.sendMsgBtn);
        userMsgInput=findViewById(R.id.writeMsg);
        userMessagesList =findViewById(R.id.messages_list_users);
        receiverImage=findViewById(R.id.customProfileImage);
        receiverName=findViewById(R.id.toolbarTitle);

        messagesAdapter= new MessagesAdapter(messagesList);
        userMessagesList= findViewById(R.id.messages_list_users);
        linearLayoutManager= new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference();

        messageReceiverId=getIntent().getExtras().get("UserTopass").toString();

        messageSenderId= firebaseAuth.getCurrentUser().getUid();

        userNameRef =FirebaseDatabase.getInstance().getReference().child("User Profiles");

        // Display User Details on Toolbar
        userNameRef.child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                receiverName.setText(userProfile.getFname() + " " +userProfile.getMname() + " " + userProfile.getLname());

                messageReceiverName=dataSnapshot.child("fname").getValue().toString();
                //Toolbar Heading
                setTitle("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(messageReceiverId).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(receiverImage);

            }
        });

        //Display User Details on Toolbar Ends here....

        //Send Message Coding Starts

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        FetchMessages();

    }

    private void FetchMessages()
    {
        databaseReference.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                       if (dataSnapshot.exists())
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();
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

    private void sendMessage() {

        String messageTxt= userMsgInput.getText().toString();
        if(TextUtils.isEmpty(messageTxt)){
            Toast.makeText(this, "Nothing to send...", Toast.LENGTH_SHORT).show();
        }else {
            String message_sender_Ref   = "Messages" + "/" + messageSenderId    +  "/" + messageReceiverId;
            String message_receiver_Ref = "Messages" + "/" + messageReceiverId  +  "/" + messageSenderId;

            DatabaseReference user_Message_KeyRef= databaseReference.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push();

            String message_push_id= user_Message_KeyRef.getKey();

            Calendar calForDate= Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate= currentDate.format(calForDate.getTime());

            Calendar calForTime= Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calForTime.getTime());

            Map messageTextBody= new HashMap();
            messageTextBody.put("Message", messageTxt);
            messageTextBody.put("Time", saveCurrentTime);
            messageTextBody.put("Date", saveCurrentDate);
            messageTextBody.put("From", messageSenderId);
            messageTextBody.put("Type", messageTxt);

            Map messageBodyDetails= new HashMap();
            messageBodyDetails.put(message_sender_Ref   +  "/"   + message_push_id, messageTextBody);
            messageBodyDetails.put(message_receiver_Ref +  "/"   + message_push_id, messageTextBody);

            databaseReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(chat_activity.this, "Message Sent...", Toast.LENGTH_SHORT).show();
                        userMsgInput.setText("");
                    }else{

                        String Error_message= task.getException().getMessage();
                        Toast.makeText(chat_activity.this, "Error:"+ Error_message, Toast.LENGTH_SHORT).show();
                        userMsgInput.setText("");
                    }

                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(chat_activity.this, Second_Activity.class));
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
