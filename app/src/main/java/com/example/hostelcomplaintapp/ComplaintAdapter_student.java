package com.example.hostelcomplaintapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ComplaintAdapter_student extends RecyclerView.Adapter<ComplaintAdapter_student.ViewHolder> {

    Context context;
    ArrayList<ComplaintModel> list;

    public ComplaintAdapter_student(Context context, ArrayList<ComplaintModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCategory, txtTitle, txtDescription, txtRoom, tvStatus, txtTime;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtRoom = itemView.findViewById(R.id.taxtRoom);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            progressBar = itemView.findViewById(R.id.progressBar);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.complaint_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ComplaintModel model = list.get(position);

        holder.txtCategory.setText("Category: " + model.getCategory());
        holder.txtTitle.setText("Title: " + model.getTitle());
        holder.txtDescription.setText("Desc: " + model.getDescription());
        holder.txtRoom.setText("Room: " + model.getRoomNumber());

        // Status
        String status = model.getStatus();
        if (status == null) status = "pending";
        
        status = status.toLowerCase();
        if (status.equals("pending")) {
            holder.progressBar.setProgress(25);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336"));
        } else if (status.equals("in progress")) {
            holder.progressBar.setProgress(50);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FFC107"));
        } else if (status.equals("completed")) {
            holder.progressBar.setProgress(100);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else if (status.equals("overdue")) {
            holder.progressBar.setProgress(100);
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#757575"));
        } else {
            holder.progressBar.setProgress(0);
        }

        // Capitalize first letter strictly for UI display
        String displayStatus = status.substring(0, 1).toUpperCase() + status.substring(1);
        holder.tvStatus.setText(displayStatus);

        // Time
        if (model.getTimestamp() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.txtTime.setText(sdf.format(new Date(model.getTimestamp())));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
