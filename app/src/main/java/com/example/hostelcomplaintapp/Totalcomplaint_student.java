package com.example.hostelcomplaintapp;

import android.content.Intent;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBackLayout);

        // Setup RecyclerView
        recyclerViewComplaints.setLayoutManager(new LinearLayoutManager(this));
        complaintList = new ArrayList<>();
        adapter = new ComplaintAdapter_student(this, complaintList);
        recyclerViewComplaints.setAdapter(adapter);

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Fetch Data
        fetchStudentComplaints();
    }

    private void fetchStudentComplaints() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewComplaints.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        // Retrieve logged-in student's ID
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("sapid", null);

        Log.d("DEBUG", "StudentId used for query: " + studentId);

        // Guard: if studentId is null or empty, we cannot query reliably
        if (studentId == null || studentId.trim().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            tvEmptyState.setText("No student ID found. Please submit a complaint first.");
            tvEmptyState.setVisibility(View.VISIBLE);
            Log.e("DEBUG", "studentId is null or empty — aborting fetch");
            return;
        }

        // Trim to avoid whitespace mismatch with Firestore value
        studentId = studentId.trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("complaints")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    complaintList.clear();

                    Log.d("DEBUG", "Docs: " + queryDocumentSnapshots.size());

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            ComplaintModel model = new ComplaintModel();
                            model.setDocId(document.getId());
                            model.setTitle(document.getString("title"));
                            model.setDescription(document.getString("description"));
                            model.setRoomNumber(document.getString("roomNumber"));
                            model.setStatus(document.getString("status"));
                            model.setCategory(document.getString("category"));
                            if (document.contains("timestamp")) {
                                Long ts = document.getLong("timestamp");
                                if (ts != null) model.setTimestamp(ts);
                            }

                            complaintList.add(model);
                        } catch (Exception e) {
                            Log.e("FIRESTORE_PARSE", "Error parsing doc", e);
                        }
                    }

                    if (complaintList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerViewComplaints.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerViewComplaints.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("ERROR", e.getMessage());
                    Toast.makeText(this, "Failed to load complaints: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvEmptyState.setText("Error loading data.");
                    tvEmptyState.setVisibility(View.VISIBLE);
                });
    }
}