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

public class Totalcomplaint_student extends AppCompatActivity {

    ImageView btnBack, gotohomepg, btnNotification, imgProfile;

    RecyclerView recyclerView;
    ArrayList<ComplaintModel> list;
    ComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_totalcomplaint_student);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}