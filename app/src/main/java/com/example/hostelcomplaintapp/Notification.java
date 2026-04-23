package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Notificationpgdatastore> list;
    NotificationAdapter adapter;
    FirebaseFirestore db;
    ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 🔗 Connect RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new NotificationAdapter(this, list);

        recyclerView.setAdapter(adapter);
        // 🔥 Firestore instance
        db = FirebaseFirestore.getInstance();

        // Get user role from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        Log.d("Notification", "User role detected: " + role);

        // 📡 Fetch data based on user role
        if (role.equals("worker") || role.equals("Staff") || role.equals("Worker")) {
            // Worker: fetch from staff_announcements + announcements
            Log.d("Notification", "Fetching from staff_announcements and announcements for worker");
            db.collection("staff_announcements")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("Notification", "Firestore error (staff_announcements)", error);
                            return;
                        }
                        if (value != null) {
                            list.clear();
                            Log.d("Notification", "Staff documents fetched: " + value.size());
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                if (model != null) {
                                    model.setId(doc.getId());
                                    list.add(model);
                                }
                            }
                            db.collection("announcements")
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnSuccessListener(allValue -> {
                                        Log.d("Notification", "All announcements fetched: " + allValue.size());
                                        for (DocumentSnapshot doc : allValue.getDocuments()) {
                                            Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                            if (model != null) {
                                                model.setId(doc.getId());
                                                list.add(model);
                                            }
                                        }
                                        list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Notification", "Error fetching announcements", e);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    });
        } else if (role.equals("student") || role.equals("Student")) {
            // Student: fetch from student_announcements + announcements
            Log.d("Notification", "Fetching from student_announcements and announcements for student");
            db.collection("student_announcements")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("Notification", "Firestore error (student_announcements)", error);
                            return;
                        }
                        if (value != null) {
                            list.clear();
                            Log.d("Notification", "Student documents fetched: " + value.size());
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                if (model != null) {
                                    model.setId(doc.getId());
                                    list.add(model);
                                }
                            }
                            db.collection("announcements")
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnSuccessListener(allValue -> {
                                        Log.d("Notification", "All announcements fetched: " + allValue.size());
                                        for (DocumentSnapshot doc : allValue.getDocuments()) {
                                            Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                            if (model != null) {
                                                model.setId(doc.getId());
                                                list.add(model);
                                            }
                                        }
                                        list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Notification", "Error fetching announcements", e);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    });
        } else {
            // Warden: fetch from all collections
            Log.d("Notification", "Fetching from all collections for warden");
            db.collection("announcements")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.e("Notification", "Firestore error (announcements)", error);
                            return;
                        }
                        if (value != null) {
                            list.clear();
                            Log.d("Notification", "Announcements fetched: " + value.size());
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                if (model != null) {
                                    model.setId(doc.getId());
                                    list.add(model);
                                }
                            }
                            db.collection("staff_announcements")
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnSuccessListener(staffValue -> {
                                        Log.d("Notification", "Staff announcements fetched: " + staffValue.size());
                                        for (DocumentSnapshot doc : staffValue.getDocuments()) {
                                            Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                            if (model != null) {
                                                model.setId(doc.getId());
                                                list.add(model);
                                            }
                                        }
                                        db.collection("student_announcements")
                                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                                .get()
                                                .addOnSuccessListener(studentValue -> {
                                                    Log.d("Notification", "Student announcements fetched: " + studentValue.size());
                                                    for (DocumentSnapshot doc : studentValue.getDocuments()) {
                                                        Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                                        if (model != null) {
                                                            model.setId(doc.getId());
                                                            list.add(model);
                                                        }
                                                    }
                                                    list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                                                    adapter.notifyDataSetChanged();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Notification", "Error fetching student announcements", e);
                                                    adapter.notifyDataSetChanged();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Notification", "Error fetching staff announcements", e);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    });
        }

        // Bottom Navigation - use LinearLayout parents for better touch response
        LinearLayout llHome = findViewById(R.id.llHome);
        LinearLayout llNotification = findViewById(R.id.llNotification);
        LinearLayout llStaff = findViewById(R.id.llStaff);
        LinearLayout llProfile = findViewById(R.id.llProfile);

        llHome.setOnClickListener(v -> {
            Intent intent1 = new Intent(Notification.this, HomePage_Warden.class);
            startActivity(intent1);
        });

        llNotification.setOnClickListener(v -> {
            // Already on Notification page
        });

        llStaff.setOnClickListener(v -> {
            Intent intent1 = new Intent(Notification.this, staff_manage.class);
            startActivity(intent1);
        });

        llProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Notification.this, imgProfile_click.class);
            startActivity(intent);
        });

        /// battery level,wifi,time visible ///
        androidx.core.view.WindowInsetsControllerCompat controller = 
            new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}