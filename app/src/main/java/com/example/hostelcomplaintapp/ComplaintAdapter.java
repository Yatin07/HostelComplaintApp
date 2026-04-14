package com.example.hostelcomplaintapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    ArrayList<ComplaintModel> list;

    // Role check (default false = student/warden, no resolve button)
    boolean isWorker = false;

    public ComplaintAdapter(ArrayList<ComplaintModel> list) {
        this.list = list;
    }

    // Use this in worker activity to show resolve button
    public void setWorker(boolean worker) {
        this.isWorker = worker;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, txtRoom, txtPriority, txtStatus;
        Button removeBtn;
        View arrowBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            txtRoom = itemView.findViewById(R.id.txtRoom);
            txtPriority = itemView.findViewById(R.id.txtPriority);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            removeBtn = itemView.findViewById(R.id.removeBtn);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
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

        holder.tvName.setText(model.getTitle());
        holder.txtRoom.setText("Room: " + model.getRoomNumber() + " (Bed: " + model.getBednumber() + ")");
        holder.txtPriority.setText("Category: " + model.getCategory());

        String status = model.getStatus();

        if (status == null || status.isEmpty() || status.equalsIgnoreCase("pending")) {
            holder.txtStatus.setText("Pending");
            holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));

            if (isWorker) {
                holder.removeBtn.setVisibility(View.VISIBLE);
                holder.removeBtn.setText("Resolve");
            } else {
                holder.removeBtn.setVisibility(View.GONE);
            }
        } else {
            holder.txtStatus.setText(status);
            if (status.equalsIgnoreCase("Completed")) {
                holder.txtStatus.setTextColor(android.graphics.Color.GREEN);
                holder.removeBtn.setVisibility(View.GONE);
            } else {
                holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#FFA000"));
                if (isWorker) {
                    holder.removeBtn.setVisibility(View.VISIBLE);
                } else {
                    holder.removeBtn.setVisibility(View.GONE);
                }
            }
        }

        // Resolve Button Click (only for worker)
        if (isWorker) {
            holder.removeBtn.setOnClickListener(v -> {
                if (model.getDocId() == null) {
                    Toast.makeText(v.getContext(), "Error: Document ID is null", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseFirestore.getInstance()
                        .collection("complaints")
                        .document(model.getDocId())
                        .update("status", "Completed")
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(v.getContext(), "Marked as Completed ✅", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }

        // Details popup on arrow click
        holder.arrowBtn.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(v.getContext());
            builder.setTitle("Complaint Details");

            String message =
                    "Category: " + model.getCategory() + "\n\n" +
                    "Complaint: " + model.getTitle() + "\n\n" +
                    "Description: " + model.getDescription() + "\n\n" +
                    "Room: " + model.getRoomNumber() + " (Bed: " + model.getBednumber() + ")\n\n" +
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