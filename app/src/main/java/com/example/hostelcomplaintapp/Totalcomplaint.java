package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

public class Totalcomplaint extends AppCompatActivity {

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
        adapter.setWorker(false);
        recyclerView.setAdapter(adapter);
        /// make card design end ///

        /// Load Data from Firestore using .get() — avoids composite index requirement ///
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
                Intent intent = new Intent(Totalcomplaint.this, Notification.class);
                startActivity(intent);
            }
        });
        /// notification button code end ///

        /// staff_manage button code start ///
        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Totalcomplaint.this, staff_manage.class);
                startActivity(intent);
            }
        });
        /// staff manage button code end ///

        /// profile button code start ///
        imgprof = findViewById(R.id.imgprof);
        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Totalcomplaint.this, imgProfile_click.class);
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
        // Use .get() (one-time fetch) instead of addSnapshotListener + orderBy.
        // addSnapshotListener + orderBy("timestamp") requires a composite Firestore index
        // that does NOT exist — this causes a FAILED_PRECONDITION crash.
        // .get() works without any index and is safe for a read-only warden view.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("complaints")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            ComplaintModel model = doc.toObject(ComplaintModel.class);
                            if (model == null) continue; // skip malformed documents
                            model.setDocId(doc.getId());
                            list.add(model);
                        } catch (Exception e) {
                            Log.e("WARDEN_COMPLAINTS", "Error parsing doc: " + doc.getId(), e);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("WARDEN_COMPLAINTS", "Failed to load: " + e.getMessage());
                });
    }
}