package com.example.hostelcomplaintapp;

<<<<<<< HEAD
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
=======
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
>>>>>>> main

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
>>>>>>> main

import java.util.ArrayList;

public class Totalcomplaint_student extends AppCompatActivity {

<<<<<<< HEAD
    private RecyclerView recyclerViewComplaints;
    private ComplaintAdapter_student adapter;
    private ArrayList<ComplaintModel> complaintList;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ImageView btnBack;
=======
    ImageView btnBack, gotohomepg, btnNotification, imgProfile;

    RecyclerView recyclerView;
    ArrayList<ComplaintModel> list;
    ComplaintAdapter adapter;
>>>>>>> main

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_totalcomplaint_student);

<<<<<<< HEAD
=======
        /// RecyclerView setup ///
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ComplaintAdapter(list);

        // ❌ Student = NO resolve button
        adapter.setWorker(false);

        recyclerView.setAdapter(adapter);

        /// 🔥 SAME AS WARDEN (NO FILTER) ///
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("complaints")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ComplaintModel model = doc.toObject(ComplaintModel.class);

                        if (model != null) {
                            model.setDocId(doc.getId()); // IMPORTANT
                            list.add(model);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });

        /// Back button ///
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        /// Home button ///
        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(v -> finish());

        /// Notification ///
        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(Totalcomplaint_student.this, Notification_student.class);
            startActivity(intent);
        });

        /// Profile ///
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Totalcomplaint_student.this, imgProfile_click.class);
            startActivity(intent);
        });

>>>>>>> main
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
<<<<<<< HEAD

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

        // Retrieve logged-in student's ID (mocking with simple fallback for robust evaluation without crash)
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String studentId = prefs.getString("sapid", "S12345"); 

        Log.d("DEBUG", "Student ID: " + studentId);

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
                            // Optional elements
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
=======
>>>>>>> main
    }
}