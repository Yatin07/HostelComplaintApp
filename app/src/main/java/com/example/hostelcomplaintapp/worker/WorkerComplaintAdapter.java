package com.example.hostelcomplaintapp.worker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelcomplaintapp.ComplaintModel;
import com.example.hostelcomplaintapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorkerComplaintAdapter extends RecyclerView.Adapter<WorkerComplaintAdapter.ComplaintViewHolder> {

    private Context context;
    private List<ComplaintModel> complaintList;
    private FirebaseFirestore db;

    public WorkerComplaintAdapter(Context context, List<ComplaintModel> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
        this.db = FirebaseFirestore.getInstance();
    }

    public void updateComplaints(List<ComplaintModel> newList) {
        complaintList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_worker_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        ComplaintModel model = complaintList.get(position);

        holder.tvComplaintTitle.setText(model.getTitle());
        holder.tvRoomNumber.setText("Room: " + model.getRoomNumber());
        holder.tvDescription.setText(model.getDescription());

        String status = model.getStatus();
        if (status == null) status = "pending";
        holder.tvStatus.setText("Status: " + status.toUpperCase());

        // Manage button visibility based on rules
        holder.btnStartWork.setVisibility(View.GONE);
        holder.btnCompleteWork.setVisibility(View.GONE);

        if (status.equalsIgnoreCase("pending")) {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); 
            holder.btnStartWork.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("in progress")) {
            holder.tvStatus.setTextColor(Color.parseColor("#FFC107"));
            holder.btnCompleteWork.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("completed")) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else if (status.equalsIgnoreCase("overdue")) {
            holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"));
            holder.btnCompleteWork.setVisibility(View.VISIBLE); // Let them complete overdue as well
        }

        // Action Listeners
        holder.btnStartWork.setOnClickListener(v -> updateStatus(model.getDocId(), "in progress"));
        holder.btnCompleteWork.setOnClickListener(v -> updateStatus(model.getDocId(), "completed"));

        // Global card tap trigger
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, ComplaintDetailActivity.class);
            intent.putExtra("docId", model.getDocId());
            context.startActivity(intent);
        });

        // Load complaint image (stored as Base64 data URL from RaiseComplaintActivity)
        String imageUrl = model.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            holder.ivComplaintImage.setVisibility(View.VISIBLE);
            // Decode on background thread to keep RecyclerView scroll smooth
            new Thread(() -> {
                try {
                    // Strip "data:image/jpeg;base64," prefix before decoding
                    String base64Data = imageUrl.contains(",")
                            ? imageUrl.substring(imageUrl.indexOf(",") + 1)
                            : imageUrl;
                    byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (bitmap != null) {
                            holder.ivComplaintImage.setImageBitmap(bitmap);
                        } else {
                            holder.ivComplaintImage.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            holder.ivComplaintImage.setVisibility(View.GONE));
                }
            }).start();
        } else {
            holder.ivComplaintImage.setVisibility(View.GONE);
        }
    }

    private void updateStatus(String docId, String newStatus) {
        if (docId == null || docId.isEmpty()) {
            Toast.makeText(context, "Invalid document ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("complaints").document(docId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvComplaintTitle, tvRoomNumber, tvDescription, tvStatus;
        Button btnStartWork, btnCompleteWork;
        ImageView ivComplaintImage;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComplaintTitle   = itemView.findViewById(R.id.tvComplaintTitle);
            tvRoomNumber       = itemView.findViewById(R.id.tvRoomNumber);
            tvDescription      = itemView.findViewById(R.id.tvDescription);
            tvStatus           = itemView.findViewById(R.id.tvStatus);
            btnStartWork       = itemView.findViewById(R.id.btnStartWork);
            btnCompleteWork    = itemView.findViewById(R.id.btnCompleteWork);
            ivComplaintImage   = itemView.findViewById(R.id.ivComplaintImage);
        }
    }
}
