package com.example.community;

import android.app.Notification;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseRef;
    private FirebaseStorage firebaseStorage;


    public  MessagesAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList =userMessagesList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView SenderMessageText, ReceiverMessageText;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            SenderMessageText=itemView.findViewById(R.id.sender_msg_txt);
            ReceiverMessageText=itemView.findViewById(R.id.receiver_msg_txt);
            receiverProfileImage=itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View V= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users, parent,false);
        firebaseAuth= FirebaseAuth.getInstance();
        return  new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        String messageSenderID=firebaseAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID= messages.getFrom();
        String fromMessagesType= messages.getType();
       // usersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("")
        firebaseStorage= FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(fromUserID).child("Images/Profile Pictures").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(holder.receiverProfileImage);
            }
        });


        if (!fromMessagesType.isEmpty())
        {
            holder.SenderMessageText.setVisibility(View.VISIBLE);
            holder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);

            if(fromUserID.equals(messageSenderID))
            {
                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_backround);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.LEFT);
                holder.SenderMessageText.setText(messages.getMessage());

            }
            else
                {
                    holder.SenderMessageText.setVisibility(View.INVISIBLE);

                    holder.ReceiverMessageText.setVisibility(View.VISIBLE);
                    holder.receiverProfileImage.setVisibility(View.VISIBLE);

                    holder.ReceiverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                    holder.ReceiverMessageText.setTextColor(Color.WHITE);
                    holder.ReceiverMessageText.setGravity(Gravity.LEFT);
                    holder.ReceiverMessageText.setText(messages.getMessage());

            }
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
