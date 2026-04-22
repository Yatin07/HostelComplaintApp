package com.example.hostelcomplaintapp.worker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hostelcomplaintapp.HomePage_Warden;
import com.example.hostelcomplaintapp.Notification_student;
import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.imgProfile_click;
import com.example.hostelcomplaintapp.staff_manage;
import com.example.hostelcomplaintapp.Worker_notification_clk;
import com.example.hostelcomplaintapp.worker_guide_clk;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class WorkerDashboardActivity extends AppCompatActivity {

    private TextView tvTotalCount, tvPendingCount, tvInProgressCount, tvCompletedCount, tvOverdueCount;
    private TextView tvAverageRating, tvRecentFeedback;
    private ListenerRegistration dashboardListener;
    ImageView gotohomepg, imgproff, btnNotification, staff_manage;

    TextView tv1WardenName, tv2WardenId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);

        tv1WardenName = findViewById(R.id.tv1WardenName);
        tv2WardenId = findViewById(R.id.tv2WardenId);

//  GET SAVED DATA
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String name = prefs.getString("name", "Worker");
        String id = prefs.getString("id", "123");

//  SET DATA
        tv1WardenName.setText(name);
        tv2WardenId.setText(id);








        // Setup Bottom Navigation via Helper
        WorkerNavigationHelper.setupNavigation(this);





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalCount = findViewById(R.id.tvTotalCount);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Worker Dashboard");
        }
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvOverdueCount = findViewById(R.id.tvOverdueCount);

        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvRecentFeedback = findViewById(R.id.tvRecentFeedback);




        // Setup mock feedback
        tvAverageRating.setText("Avg Rating: 4.5 / 5.0 ⭐");
        tvRecentFeedback.setText("\"Great job fixing the fan!\" - Room 101\n\"Quick response\" - Room 304");

        // Card navigation enabled
        findViewById(R.id.cardTotal).setOnClickListener(v -> openTaskList("ALL"));
        findViewById(R.id.cardPending).setOnClickListener(v -> openTaskList("PENDING"));
        findViewById(R.id.cardInProgress).setOnClickListener(v -> openTaskList("IN_PROGRESS"));
        findViewById(R.id.cardCompleted).setOnClickListener(v -> openTaskList("COMPLETED"));
        findViewById(R.id.cardOverdue).setOnClickListener(v -> openTaskList("OVERDUE"));

        // Navigation setup handled above via setupNavigation(this)

        setupRealTimeUpdates();
    }

    private void openTaskList(String filterType) {
        Intent intent = new Intent(this, WorkerTaskListActivity.class);
        intent.putExtra("FILTER_TYPE", filterType);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRealTimeUpdates() {
        // Prevent multiple listeners from being registered
        if (dashboardListener != null) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        dashboardListener = db.collection("complaints").addSnapshotListener((value, error) -> {
            if (error != null || value == null) {
                return;
            }

            int pending = 0, inProgress = 0, completed = 0, overdue = 0;

            for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                String status = doc.getString("status");
                if (status != null) {
                    if (status.equalsIgnoreCase("pending")) pending++;
                    else if (status.equalsIgnoreCase("in progress")) inProgress++;
                    else if (status.equalsIgnoreCase("completed")) completed++;
                    else if (status.equalsIgnoreCase("overdue")) overdue++;
                }
            }

            int total = pending + inProgress + completed + overdue;

            tvTotalCount.setText(String.valueOf(total));
            tvPendingCount.setText(String.valueOf(pending));
            tvInProgressCount.setText(String.valueOf(inProgress));
            tvCompletedCount.setText(String.valueOf(completed));
            tvOverdueCount.setText(String.valueOf(overdue));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove Firestore listener to prevent memory leaks
        if (dashboardListener != null) {
            dashboardListener.remove();
            dashboardListener = null;
        }
    }
}
