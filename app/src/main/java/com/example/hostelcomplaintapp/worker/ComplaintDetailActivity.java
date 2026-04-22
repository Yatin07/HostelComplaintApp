package com.example.hostelcomplaintapp.worker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.ComplaintModel;
import com.example.hostelcomplaintapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComplaintDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvRoom, tvStudent, tvDesc, tvStatus, tvDeadline;
    private Button btnAction;
    private FirebaseFirestore db;
    private String docId;
    private ComplaintModel currentComplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Complaint Details");
        }

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvRoom = findViewById(R.id.tvDetailRoom);
        tvStudent = findViewById(R.id.tvDetailStudent);
        tvDesc = findViewById(R.id.tvDetailDesc);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvDeadline = findViewById(R.id.tvDeadline);
        btnAction = findViewById(R.id.btnDetailAction);

        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        db = FirebaseFirestore.getInstance();
        docId = getIntent().getStringExtra("docId");

        if (docId != null) {
            fetchComplaintDetails();
        } else {
            Toast.makeText(this, "Error loading document", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchComplaintDetails() {
        db.collection("complaints").document(docId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        ComplaintModel model = new ComplaintModel();
                        model.setDocId(doc.getId());
                        model.setTitle(doc.getString("title"));
                        model.setDescription(doc.getString("description"));
                        model.setRoomNumber(doc.getString("roomNumber"));
                        model.setStudentId(doc.getString("studentId"));
                        model.setStatus(doc.getString("status"));
                        
//                        if (doc.contains("deadline")) {
//                            Long dl = doc.getLong("deadline");
//                            if (dl != null) model.setDeadline(dl);
//                        }
                        if (doc.get("deadline") != null) {
                            Object dlObj = doc.get("deadline");
                            if (dlObj instanceof com.google.firebase.Timestamp) {
                                model.setDeadline(((com.google.firebase.Timestamp) dlObj).toDate().getTime());
                            } else if (dlObj instanceof Long) {
                                model.setDeadline((Long) dlObj);
                            }
                        }

                        currentComplaint = model;
                        evaluateOverdueStateAndRender();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void evaluateOverdueStateAndRender() {
        String status = currentComplaint.getStatus();
        if (status == null) status = "pending";
        else status = status.toLowerCase();

        long deadline = currentComplaint.getDeadline();

        // 5. OVERDUE LOGIC natively handled correctly updating background backend first if tripped
        if (deadline > 0 && System.currentTimeMillis() > deadline && !status.equals("completed") && !status.equals("overdue")) {
            status = "overdue";
            currentComplaint.setStatus("overdue");
            db.collection("complaints").document(docId).update("status", "overdue");
        }

        renderUI(status, deadline);
    }

    private void renderUI(String status, long deadline) {
        tvTitle.setText(currentComplaint.getTitle());
        tvRoom.setText("Room: " + currentComplaint.getRoomNumber());
        tvStudent.setText("Student ID: " + currentComplaint.getStudentId());
        tvDesc.setText("Description:\n" + currentComplaint.getDescription());

        tvStatus.setText("Status: " + status.toUpperCase());

        if (deadline > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            tvDeadline.setText("Deadline: " + sdf.format(new Date(deadline)));
            tvDeadline.setVisibility(View.VISIBLE);
        } else {
            tvDeadline.setVisibility(View.GONE);
            tvDeadline.setText("Deadline: --");
        }

        // Action button configs
        btnAction.setVisibility(View.GONE);
        btnAction.setEnabled(true);
        if (status.equals("pending")) {
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("Start Work");
            btnAction.setBackgroundColor(Color.parseColor("#FF9800"));
            btnAction.setOnClickListener(v -> actionStartWork());
            tvStatus.setTextColor(Color.parseColor("#F44336"));
        } else if (status.equals("in progress")) {
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setText("Mark Completed");
            btnAction.setBackgroundColor(Color.parseColor("#4CAF50"));
            btnAction.setOnClickListener(v -> actionCompleteWork());
            tvStatus.setTextColor(Color.parseColor("#FFC107"));
        } else if (status.equals("overdue")) {
            // ✅ FIX: Allow starting work even if overdue
            btnAction.setVisibility(View.VISIBLE);
            btnAction.setEnabled(true);
            btnAction.setText("Start Work (Late)");
            btnAction.setBackgroundColor(Color.parseColor("#FF9800"));
            btnAction.setOnClickListener(v -> actionStartWork());
            tvStatus.setTextColor(Color.parseColor("#D32F2F")); // RED
        } else if (status.equals("completed")) {
            tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    private void actionStartWork() {
        long deadlineMillis = System.currentTimeMillis() + (3L * 24L * 60L * 60L * 1000L);
        com.google.firebase.Timestamp timestamp = new com.google.firebase.Timestamp(new java.util.Date(deadlineMillis));

        db.collection("complaints").document(docId)
                .update("status", "in progress", "deadline", timestamp)
                .addOnSuccessListener(aVoid -> fetchComplaintDetails())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
    }

    private void actionCompleteWork() {
        db.collection("complaints").document(docId)
                .update("status", "completed")
                .addOnSuccessListener(aVoid -> fetchComplaintDetails())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
