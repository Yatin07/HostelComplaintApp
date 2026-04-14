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

import com.google.firebase.firestore.FirebaseFirestore;
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
        progressBar = findViewById(R.id.loadingBar);
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

        // Retrieve logged-in student's ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("sapid", null);

        Log.d("DEBUG", "StudentId used for query: " + studentId);

        // Guard: if studentId is null or empty, abort
        if (studentId == null || studentId.trim().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            tvEmptyState.setText("No student ID found. Please submit a complaint first.");
            tvEmptyState.setVisibility(View.VISIBLE);
            Log.e("DEBUG", "studentId is null or empty — aborting fetch");
            return;
        }

        final String finalStudentIdStr = studentId.trim();

        // Also parse as int — Firestore may store studentId as number or string
        int parsed;
        try {
            parsed = Integer.parseInt(finalStudentIdStr);
        } catch (NumberFormatException e) {
            parsed = -1; // non-numeric: only String comparison will be used
        }
        final int finalStudentIdInt = parsed;

        Log.d("DEBUG", "studentId str: " + finalStudentIdStr + " | int: " + finalStudentIdInt);

        // SINGLE QUERY — fetch all complaints, filter client-side.
        // Using dual async queries caused a race condition: two callbacks each called
        // list.clear(), so whichever finished last wiped out the other's results.
        // One query → one callback → one clear() → all adds → one notifyDataSetChanged().
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("complaints")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);

                    // Clear ONCE before the loop — never inside it
                    complaintList.clear();

                    Log.d("DEBUG", "Total docs in collection: " + queryDocumentSnapshots.size());

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // Match studentId regardless of Firestore storage type (String or integer)
                            Object firestoreStudentId = document.get("studentId");
                            boolean isMatch = false;

                            if (firestoreStudentId instanceof String) {
                                // Firestore stored as String: compare directly
                                isMatch = finalStudentIdStr.equals(firestoreStudentId);
                            } else if (firestoreStudentId instanceof Long) {
                                // Firestore stored as integer (Long): compare int values
                                isMatch = finalStudentIdInt != -1
                                        && finalStudentIdInt == ((Long) firestoreStudentId).intValue();
                            } else if (firestoreStudentId instanceof Number) {
                                // Any other numeric type
                                isMatch = finalStudentIdInt != -1
                                        && finalStudentIdInt == ((Number) firestoreStudentId).intValue();
                            }

                            if (!isMatch) continue; // skip complaints from other students

                            // Build model manually to handle mixed field types safely
                            ComplaintModel model = new ComplaintModel();
                            model.setDocId(document.getId());
                            model.setTitle(document.getString("title"));
                            model.setDescription(document.getString("description"));
                            model.setStatus(document.getString("status"));
                            model.setCategory(document.getString("category"));

                            // roomNumber: handle both String and integer storage
                            Object roomObj = document.get("roomNumber");
                            if (roomObj instanceof Long) {
                                model.setRoomNumber(String.valueOf(roomObj));
                            } else if (roomObj instanceof String) {
                                model.setRoomNumber((String) roomObj);
                            }

                            Long ts = document.getLong("timestamp");
                            if (ts != null) model.setTimestamp(ts);

                            complaintList.add(model); // add ALL matching complaints
                        } catch (Exception e) {
                            Log.e("FIRESTORE_PARSE", "Error parsing doc: " + document.getId(), e);
                        }
                    }

                    Log.d("DEBUG", "Total complaints for student: " + complaintList.size());

                    if (complaintList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerViewComplaints.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerViewComplaints.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged(); // called ONCE after all items are added
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("ERROR", "Failed to fetch complaints: " + e.getMessage());
                    Toast.makeText(this, "Failed to load complaints: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvEmptyState.setText("Error loading data.");
                    tvEmptyState.setVisibility(View.VISIBLE);
                });
    }
}