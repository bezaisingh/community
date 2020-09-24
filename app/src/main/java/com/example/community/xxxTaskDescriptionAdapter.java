package com.example.community;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.media.CamcorderProfile.get;
import static android.support.v4.content.ContextCompat.startActivity;

public abstract class xxxTaskDescriptionAdapter extends RecyclerView.Adapter<xxxTaskDescriptionAdapter.xxxTaskViewHolder> {

    private  static ClickListener clickListener;
    private List<taskDescription> userTaskList;
    private Context context;


    public static final String TAG = xxxTaskDescriptionAdapter.class.getSimpleName();



    public  xxxTaskDescriptionAdapter (List<taskDescription> userTaskList, Context context)
    {
        this.userTaskList = userTaskList;
        this.context=context;
    }

    public abstract void onItemClick(int position, View v);



    public class xxxTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView workValue, taskName, time, date;
        public CardView xxxRelativeLayout;


        public xxxTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            workValue=itemView.findViewById(R.id.TRY_work_val);
            taskName=itemView.findViewById(R.id.TRY_tsk_des);
            time=itemView.findViewById(R.id.TRY_listTvTime);
            date=itemView.findViewById(R.id.TRY_listTvDate);
            xxxRelativeLayout= itemView.findViewById(R.id.xxxRelativeLayout);



            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }

    public void setOnItemClickListener(xxxTaskDescriptionAdapter.ClickListener clickListener) {
        xxxTaskDescriptionAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }



    @NonNull
    @Override
    public xxxTaskDescriptionAdapter.xxxTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)

    {
        View V= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_task_list, parent,false);
        return  new xxxTaskViewHolder (V);
    }

    @Override
    public void onBindViewHolder(@NonNull xxxTaskViewHolder holder, final int position)
    {
       final taskDescription task_Description = userTaskList.get(position);
       final String PostKey= userTaskList.get(position).getPostKey();

       holder.time.setText(task_Description.getTime());
       holder.taskName.setText(task_Description.getTask());
       holder.workValue.setText("₹"+task_Description.getWorkValue());
       holder.date.setText(task_Description.getDate());

     holder.xxxRelativeLayout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

              Intent intent= new Intent(context, viewTaskDescription.class);
             intent.putExtra("PostKey", PostKey);
             context.startActivity(intent);
             Log.d(TAG, "On Click In adapter PostKey: " + PostKey);
         }
     });


    }

    @Override
    public int getItemCount() {
        return userTaskList.size();
    }
}
