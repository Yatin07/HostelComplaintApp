package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class pendingcomplaint extends AppCompatActivity {

    ImageView btnBack, gotohomepg, btnNotification, staff_manage, imgProfile;

    RecyclerView recyclerView;
    ArrayList<ComplaintModel> list;
    ComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendingcomplaint);

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ComplaintAdapter(list);
        adapter.setWorker(false); // Warden view
        recyclerView.setAdapter(adapter);

        // Firestore - fetch pending complaints
        loadPendingComplaints();

        /// Back
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        /// Home
        gotohomepg = findViewById(R.id.gotohomepg);
        if (gotohomepg != null) {
            gotohomepg.setOnClickListener(v -> finish());
        }

        /// Notification
        btnNotification = findViewById(R.id.btnNotification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                startActivity(new Intent(pendingcomplaint.this, Notification.class));
            });
        }

        /// Staff
        staff_manage = findViewById(R.id.staff_manage);
        if (staff_manage != null) {
            staff_manage.setOnClickListener(v -> {
                startActivity(new Intent(pendingcomplaint.this, staff_manage.class));
            });
        }

        /// Profile
        imgProfile = findViewById(R.id.imgProfile);
        if (imgProfile != null) {
            imgProfile.setOnClickListener(v -> {
                startActivity(new Intent(pendingcomplaint.this, imgProfile_click.class));
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadPendingComplaints() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("complaints")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("PENDING_COMPLAINTS", "Listen failed.", error);
                        fetchOnce();
                        return;
                    }

                    if (value != null) {
                        list.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                String status = doc.getString("status");
                                // Real-time filtering for "Pending" status
                                if (status != null && status.equalsIgnoreCase("Pending")) {
                                    list.add(parseDocument(doc));
                                }
                            } catch (Exception e) {
                                Log.e("PENDING_COMPLAINTS", "Error parsing doc: " + doc.getId(), e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void fetchOnce() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("complaints")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            String status = doc.getString("status");
                            if (status != null && status.equalsIgnoreCase("Pending")) {
                                list.add(parseDocument(doc));
                            }
                        } catch (Exception e) {
                            Log.e("PENDING_COMPLAINTS", "Error parsing doc: " + doc.getId(), e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load pending complaints", Toast.LENGTH_SHORT).show();
                });
    }

    private ComplaintModel parseDocument(QueryDocumentSnapshot doc) {
        ComplaintModel model = new ComplaintModel();
        model.setDocId(doc.getId());
        model.setTitle(doc.getString("title"));
        model.setDescription(doc.getString("description"));
        model.setStatus(doc.getString("status"));
        model.setCategory(doc.getString("category"));
        model.setImageUrl(doc.getString("imageUrl"));
        model.setStudentId(doc.getString("studentId"));

        // Handle Room (stored as "room" in RaiseComplaintActivity)
        Object roomObj = doc.get("room");
        if (roomObj == null) roomObj = doc.get("roomNumber");
        if (roomObj != null) {
            model.setRoom(String.valueOf(roomObj));
            model.setRoomNumber(String.valueOf(roomObj));
        }

        // Handle Timestamp (Server timestamps are stored as Timestamp objects)
        Object tsObj = doc.get("timestamp");
        if (tsObj instanceof Timestamp) {
            model.setTimestamp(((Timestamp) tsObj).toDate().getTime());
        } else if (tsObj instanceof Long) {
            model.setTimestamp((Long) tsObj);
        }
        
        return model;
    }
}