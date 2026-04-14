package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
        recyclerView.setAdapter(adapter);

        // Firestore - fetch only pending complaints
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("complaints")
                .whereEqualTo("status", "pending")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ComplaintModel model = doc.toObject(ComplaintModel.class);
                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();
                });

        /// Back
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        /// Home
        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, HomePage_Warden.class));
        });

        /// Notification
        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, Notification.class));
        });

        /// Staff
        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, staff_manage.class));
        });

        /// Profile
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, imgProfile_click.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}