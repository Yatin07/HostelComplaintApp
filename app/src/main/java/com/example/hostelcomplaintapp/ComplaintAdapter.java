package com.example.hostelcomplaintapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    ArrayList<ComplaintModel> list;

    public ComplaintAdapter(ArrayList<ComplaintModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView txtRoom;
        TextView txtPriority;
        Button removeBtn;
        TextView txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            txtRoom = itemView.findViewById(R.id.txtRoom);
            txtPriority = itemView.findViewById(R.id.txtPriority);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.totalcomplaint_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ComplaintModel model = list.get(position);

        // Set data
        holder.tvName.setText(model.getText());
        holder.txtRoom.setText("Room: " + model.getRoom());

        // Temporary priority
        holder.txtPriority.setText("High Priority");

        // Button click (optional)
        holder.removeBtn.setOnClickListener(v -> {
            // For now just remove from list
            list.remove(position);
            notifyDataSetChanged();
        });


        // Get status
        String status = model.getStatus();

// If status is null → show Pending
        if (status == null || status.isEmpty()) {
            holder.txtStatus.setText("Pending");
            holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));
        } else {

            holder.txtStatus.setText(status);

            if (status.equals("Completed")) {
                holder.txtStatus.setTextColor(android.graphics.Color.GREEN);
            } else {
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));
            }
        }


        holder.itemView.findViewById(R.id.arrowBtn).setOnClickListener(v -> {

            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(v.getContext());

            builder.setTitle("Complaint Details");

            String message =
                    "Complaint: " + model.getText() + "\n\n" +
                            "Room: " + model.getRoom() + "\n\n" +
                            "Bed: " + (model.getBedNumber() == null ? "N/A" : model.getBedNumber()) + "\n\n" +
                            "Student ID: " + (model.getStudentId() == null ? "N/A" : model.getStudentId()) + "\n\n" +
                            "Status: " + (model.getStatus() == null ? "Pending" : model.getStatus());

            builder.setMessage(message);

            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            builder.show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}