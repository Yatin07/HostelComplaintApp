package com.example.hostelcomplaintapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.models.Task;
import com.example.hostelcomplaintapp.worker.WorkerTaskDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public void updateTasks(List<Task> newTasks) {
        taskList = new ArrayList<>(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvRoomNumber.setText("Room: " + task.getRoomNumber());
        holder.tvPriority.setText(task.getPriority());
        holder.tvStatus.setText("Status: " + task.getStatus());

        // Format Deadline
        long diff = task.getDeadline() - System.currentTimeMillis();
        if (diff < 0) {
            holder.tvDeadline.setText("Deadline: Overdue");
            holder.tvDeadline.setTextColor(Color.parseColor("#D32F2F")); // Red
        } else {
            long days = diff / (24 * 60 * 60 * 1000);
            holder.tvDeadline.setText("Deadline: " + days + " days left");
            holder.tvDeadline.setTextColor(Color.parseColor("#388E3C")); // Green
        }

        // Color coding based on status/priority
        if (task.getPriority().equalsIgnoreCase("High") || task.getStatus().equalsIgnoreCase("Overdue")) {
            holder.tvPriority.setTextColor(Color.WHITE);
            holder.tvPriority.setBackgroundColor(Color.parseColor("#D32F2F")); // Red
        } else if (task.getPriority().equalsIgnoreCase("Medium")) {
            holder.tvPriority.setTextColor(Color.BLACK);
            holder.tvPriority.setBackgroundColor(Color.parseColor("#FBC02D")); // Yellow
        } else {
            holder.tvPriority.setTextColor(Color.WHITE);
            holder.tvPriority.setBackgroundColor(Color.parseColor("#388E3C")); // Green
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkerTaskDetailsActivity.class);
            intent.putExtra("TASK_ID", task.getTaskId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvRoomNumber, tvPriority, tvStatus, tvDeadline;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
        }
    }
}
