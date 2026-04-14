package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
=======
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

>>>>>>> main
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

<<<<<<< HEAD
=======
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

>>>>>>> main
public class pendingcomplaint extends AppCompatActivity {

    ImageView btnBack, gotohomepg, btnNotification, staff_manage, imgProfile;

<<<<<<< HEAD
=======
    RecyclerView recyclerView;
    ArrayList<ComplaintModel> list;
    ComplaintAdapter adapter;
>>>>>>> main

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendingcomplaint);

<<<<<<< HEAD

        /// back button click --> to previous page code start here////

        btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /// back button click --> to previous page code start here////

        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, HomePage_Warden.class);
                startActivity(intent1);
            }
        });

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, Notification.class);
                startActivity(intent1);
            }
        });

        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, staff_manage.class);
                startActivity(intent1);
            }
        });

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, imgProfile_click.class);
                startActivity(intent1);
            }
        });









=======
        // 🔥 RecyclerView setup
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ComplaintAdapter(list);
        recyclerView.setAdapter(adapter);

        // 🔥 Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("complaints")
                .whereEqualTo("status", "pending") // ✅ filter
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

        /// 🔙 Back
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        /// 🏠 Home
        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, HomePage_Warden.class));
        });

        /// 🔔 Notification
        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, Notification.class));
        });

        /// 👨‍🔧 Staff
        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, staff_manage.class));
        });

        /// 👤 Profile
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint.this, imgProfile_click.class));
        });

>>>>>>> main
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}