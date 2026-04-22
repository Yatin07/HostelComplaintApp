package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelcomplaintapp.NotificationAdapter;
import com.example.hostelcomplaintapp.Notificationpgdatastore;
import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.worker_guide_clk;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.hostelcomplaintapp.worker.WorkerNavigationHelper;

import java.util.ArrayList;

public class Worker_notification_clk extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Notificationpgdatastore> list;
    NotificationAdapter adapter; // Staff version with remove button
    FirebaseFirestore db;

    ImageView btnBack, gotohomepg, imgprof, btnNotification, staff_manage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_notification_clk);

        // 🔙 Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🔗 RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new NotificationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        // 🔥 Firestore
        db = FirebaseFirestore.getInstance();

        db.collection("announcements") // you can change collection if needed
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {
                        list.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);

                            if (model != null) {
                                model.setId(doc.getId());
                                list.add(model);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });

        // Setup Bottom Navigation via Helper
        WorkerNavigationHelper.setupNavigation(this);

        // 🔥 Insets (status bar fix)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}