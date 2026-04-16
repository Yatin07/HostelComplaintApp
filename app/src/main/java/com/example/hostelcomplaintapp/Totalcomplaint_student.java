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

public class Totalcomplaint_student extends AppCompatActivity {

    ImageView btnBack, gotohomepg, btnNotification, staff_manage, imgprof;

    private ArrayList<ComplaintModel> list;
    private ComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_totalcomplaint);

        /// make card design start ///
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ComplaintAdapter(list);
        adapter.setWorker(false); // Warden view
        recyclerView.setAdapter(adapter);
        /// make card design end ///

        /// Load Data from Firestore ///
        loadComplaints();

        /// back button code start ///
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /// back button code end ///

        /// home button code start ///
        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /// home button code end ///

        /// notification button code start ///
        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Totalcomplaint_student.this, Notification.class);
                startActivity(intent);
            }
        });
        /// notification button code end ///

        /// staff_manage button code start ///
        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Totalcomplaint_student.this, staff_manage.class);
                startActivity(intent);
            }
        });
        /// staff manage button code end ///

        /// profile button code start ///
        imgprof = findViewById(R.id.imgprof);
        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Totalcomplaint_student.this, imgProfile_click.class);
                startActivity(intent);
            }
        });
        /// profile button code end ///

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadComplaints() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use a simpler query that doesn't require composite indexes if possible,
        // or just fetch all and handle parsing.
        db.collection("complaints")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("WARDEN_COMPLAINTS", "Listen failed.", error);
                        // Fallback to one-time fetch if snapshot listener fails (likely due to missing index)
                        fetchOnce();
                        return;
                    }

                    if (value != null) {
                        list.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                ComplaintModel model = parseDocument(doc);
                                list.add(model);
                            } catch (Exception e) {
                                Log.e("WARDEN_COMPLAINTS", "Error parsing doc: " + doc.getId(), e);
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
                            ComplaintModel model = parseDocument(doc);
                            list.add(model);
                        } catch (Exception e) {
                            Log.e("WARDEN_COMPLAINTS", "Error parsing doc: " + doc.getId(), e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("WARDEN_COMPLAINTS", "Failed to load: " + e.getMessage());
                    Toast.makeText(this, "Failed to load complaints", Toast.LENGTH_SHORT).show();
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

        // Handle Room
        Object roomObj = doc.get("room");
        if (roomObj == null) roomObj = doc.get("roomNumber");
        if (roomObj != null) {
            model.setRoom(String.valueOf(roomObj));
            model.setRoomNumber(String.valueOf(roomObj));
        }

        // Handle Timestamp
        Object tsObj = doc.get("timestamp");
        if (tsObj instanceof Timestamp) {
            model.setTimestamp(((Timestamp) tsObj).toDate().getTime());
        } else if (tsObj instanceof Long) {
            model.setTimestamp((Long) tsObj);
        }

        return model;
    }
}