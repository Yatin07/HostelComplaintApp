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

import android.content.SharedPreferences;
import android.widget.TextView;
import java.util.ArrayList;

public class pendingcomplaint_student extends AppCompatActivity {
    ImageView btnBack, gotohomepg, btnNotification, howtouseapp, imgprof;
    private View loadingBar;
    private TextView tvEmptyState;
    private String currentStudentId;
    private ArrayList<ComplaintModel> list;
    private ComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendingcomplaint_student);

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ComplaintAdapter(list);
        adapter.setWorker(false); // Student view
        recyclerView.setAdapter(adapter);

        loadingBar = findViewById(R.id.loadingBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        currentStudentId = prefs.getString("id", "");

        // Load data
        loadPendingComplaints();

        // Standardized Navigation
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(v -> finish());

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint_student.this, Notification_student.class));
        });

        howtouseapp = findViewById(R.id.howtouseapp);
        howtouseapp.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint_student.this, Guide_pg_student.class));
        });

        imgprof = findViewById(R.id.imgprof);
        imgprof.setOnClickListener(v -> {
            startActivity(new Intent(pendingcomplaint_student.this, student_profile_pg.class));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadPendingComplaints() {
        if (loadingBar != null) loadingBar.setVisibility(View.VISIBLE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("complaints")
                .whereEqualTo("studentId", currentStudentId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (loadingBar != null) loadingBar.setVisibility(View.GONE);

                    if (error != null) {
                        Log.e("PENDING_STUDENT", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        list.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            try {
                                String status = doc.getString("status");
                                if (status == null || status.isEmpty() || status.equalsIgnoreCase("pending")) {
                                    list.add(parseDocument(doc));
                                }
                            } catch (Exception e) {
                                Log.e("PENDING_STUDENT", "Error parsing doc: " + doc.getId(), e);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (tvEmptyState != null) {
                            tvEmptyState.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }
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

        Object roomObj = doc.get("room");
        if (roomObj == null) roomObj = doc.get("roomNumber");
        if (roomObj != null) {
            model.setRoom(String.valueOf(roomObj));
            model.setRoomNumber(String.valueOf(roomObj));
        }

        Object tsObj = doc.get("timestamp");
        if (tsObj instanceof Timestamp) {
            model.setTimestamp(((Timestamp) tsObj).toDate().getTime());
        } else if (tsObj instanceof Long) {
            model.setTimestamp((Long) tsObj);
        }
        return model;
    }
}