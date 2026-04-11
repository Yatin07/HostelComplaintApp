package com.example.hostelcomplaintapp.worker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class WorkerDashboardActivity extends AppCompatActivity {

    private TextView tvTotalCount, tvPendingCount, tvInProgressCount, tvCompletedCount, tvOverdueCount;
    private TextView tvAverageRating, tvRecentFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);

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

        Button btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup mock feedback
        tvAverageRating.setText("Avg Rating: 4.5 / 5.0 ⭐");
        tvRecentFeedback.setText("\"Great job fixing the fan!\" - Room 101\n\"Quick response\" - Room 304");

        // Setup Click Listeners for Dashboard Cards (Filter Passing)
        findViewById(R.id.cardTotal).setOnClickListener(v -> openTaskList("ALL"));
        findViewById(R.id.cardPending).setOnClickListener(v -> openTaskList("PENDING"));
        findViewById(R.id.cardInProgress).setOnClickListener(v -> openTaskList("IN_PROGRESS"));
        findViewById(R.id.cardCompleted).setOnClickListener(v -> openTaskList("COMPLETED"));
        findViewById(R.id.cardOverdue).setOnClickListener(v -> openTaskList("OVERDUE"));
        
        WorkerNavigationHelper.setupNavigation(this);
        
        setupRealTimeUpdates();
    }

    private void openTaskList(String filterType) {
        Intent intent = new Intent(WorkerDashboardActivity.this, WorkerTaskListActivity.class);
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String workerId = "W-12345"; // Default worker ID

        db.collection("complaints")
          .whereEqualTo("assignedWorkerId", workerId)
          .addSnapshotListener((value, error) -> {
              if (error != null || value == null) {
                  return;
              }

              int total = value.size();
              int pending = 0;
              int inProgress = 0;
              int completed = 0;
              int overdue = 0;

              long currentTime = System.currentTimeMillis();

              for (QueryDocumentSnapshot doc : value) {
                  String status = doc.getString("status");
                  if (status == null) status = "Pending";

                  Object deadlineObj = doc.get("deadline");
                  long deadline = 0;
                  if (deadlineObj instanceof Long) {
                      deadline = (Long) deadlineObj;
                  } else if (deadlineObj instanceof String) {
                      try { deadline = Long.parseLong((String)deadlineObj); } catch(Exception e){}
                  }

                  if (deadline > 0 && currentTime > deadline && !status.equals("Completed") && !status.equals("Overdue")) {
                      status = "Overdue";
                      db.collection("complaints").document(doc.getId()).update("status", "Overdue");
                  }

                  if (status.equalsIgnoreCase("Pending")) pending++;
                  else if (status.equalsIgnoreCase("In Progress")) inProgress++;
                  else if (status.equalsIgnoreCase("Completed")) completed++;
                  else if (status.equalsIgnoreCase("Overdue")) overdue++;
                  else pending++;
              }

              tvTotalCount.setText(String.valueOf(total));
              tvPendingCount.setText(String.valueOf(pending));
              tvInProgressCount.setText(String.valueOf(inProgress));
              tvCompletedCount.setText(String.valueOf(completed));
              tvOverdueCount.setText(String.valueOf(overdue));
          });
    }
}
