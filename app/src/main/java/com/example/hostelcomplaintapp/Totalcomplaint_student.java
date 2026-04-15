package com.example.hostelcomplaintapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Totalcomplaint_student extends AppCompatActivity {

    private RecyclerView recyclerViewComplaints;
    private ComplaintAdapter_student adapter;
    private ArrayList<ComplaintModel> complaintList;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_totalcomplaint_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        recyclerViewComplaints = findViewById(R.id.recyclerViewComplaints);
        progressBar = findViewById(R.id.loadingBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBackLayout);

        // Setup RecyclerView
        recyclerViewComplaints.setLayoutManager(new LinearLayoutManager(this));
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapter_student(this, complaintList);
        recyclerViewComplaints.setAdapter(adapter);

        // Back Button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Fetch Data
        fetchStudentComplaints();
    }

    private void fetchStudentComplaints() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (recyclerViewComplaints != null) recyclerViewComplaints.setVisibility(View.GONE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("sapid", "");

        if (studentId.isEmpty()) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (tvEmptyState != null) {
                tvEmptyState.setText("Session expired. Please login again.");
                tvEmptyState.setVisibility(View.VISIBLE);
            }
            return;
        }

        final String finalStudentId = studentId.trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Fetch all complaints and filter locally to ensure reliability
        // (Avoids issues with Firestore composite indexes and type mismatch for studentId)
        db.collection("complaints")
                .addSnapshotListener((value, error) -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    
                    if (error != null) {
                        Log.e("FIRESTORE_ERROR", "Fetch failed: " + error.getMessage());
                        if (tvEmptyState != null) {
                            tvEmptyState.setText("Error loading complaints.");
                            tvEmptyState.setVisibility(View.VISIBLE);
                        }
                        return;
                    }

                    if (value != null) {
                        complaintList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            try {
                                Object docSidObj = document.get("studentId");
                                String docSid = docSidObj != null ? String.valueOf(docSidObj).trim() : "";
                                
                                if (docSid.equals(finalStudentId)) {
                                    complaintList.add(parseDocument(document));
                                }
                            } catch (Exception e) {
                                Log.e("PARSE_ERROR", "Error parsing doc: " + document.getId(), e);
                            }
                        }

                        // Local sorting by timestamp descending (newest first)
                        Collections.sort(complaintList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));

                        if (complaintList.isEmpty()) {
                            if (tvEmptyState != null) {
                                tvEmptyState.setText("No complaints found.");
                                tvEmptyState.setVisibility(View.VISIBLE);
                            }
                            if (recyclerViewComplaints != null) recyclerViewComplaints.setVisibility(View.GONE);
                        } else {
                            if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
                            if (recyclerViewComplaints != null) recyclerViewComplaints.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private ComplaintModel parseDocument(QueryDocumentSnapshot document) {
        ComplaintModel model = new ComplaintModel();
        model.setDocId(document.getId());
        model.setTitle(document.getString("title"));
        model.setDescription(document.getString("description"));
        model.setStatus(document.getString("status"));
        model.setCategory(document.getString("category"));
        model.setImageUrl(document.getString("imageUrl"));
        
        Object sid = document.get("studentId");
        model.setStudentId(sid != null ? String.valueOf(sid) : "");

        // Handle Room
        Object roomObj = document.get("room");
        if (roomObj == null) roomObj = document.get("roomNumber");
        if (roomObj != null) {
            String r = String.valueOf(roomObj);
            model.setRoomNumber(r);
            model.setRoom(r);
        }

        // Handle Timestamp
        Object tsObj = document.get("timestamp");
        if (tsObj instanceof Timestamp) {
            model.setTimestamp(((Timestamp) tsObj).toDate().getTime());
        } else if (tsObj instanceof Long) {
            model.setTimestamp((Long) tsObj);
        }

        return model;
    }
}