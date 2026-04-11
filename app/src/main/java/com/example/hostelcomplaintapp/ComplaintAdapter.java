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

        // ✅ Set correct Firestore data
        holder.tvName.setText(model.getTitle());
        holder.txtRoom.setText("Room: " + model.getRoom());

        // Temporary priority
        holder.txtPriority.setText("High Priority");

        // Status logic
        String status = model.getStatus();

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

        // Remove button (local only)
        holder.removeBtn.setText("Resolve");

        holder.removeBtn.setOnClickListener(v -> {

            FirebaseFirestore.getInstance()
                    .collection("complaints")
                    .document(model.getDocId())
                    .update("status", "Completed")
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(v.getContext(), "Marked as Completed ✅", Toast.LENGTH_SHORT).show();
                    });
        });

        // 🔥 Details popup
        holder.itemView.findViewById(R.id.arrowBtn).setOnClickListener(v -> {

            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(v.getContext());

            builder.setTitle("Complaint Details");

            String message =
                    "Complaint: " + model.getTitle() + "\n\n" +
                            "Description: " + model.getDescription() + "\n\n" +
                            "Room: " + model.getRoom() + "\n\n" +
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