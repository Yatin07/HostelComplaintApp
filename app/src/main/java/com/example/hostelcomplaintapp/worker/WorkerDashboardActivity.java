package com.example.hostelcomplaintapp.worker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.models.DataRepository;
import com.example.hostelcomplaintapp.models.Task;

import java.util.List;

public class WorkerDashboardActivity extends AppCompatActivity {

    private TextView tvTotalCount, tvPendingCount, tvInProgressCount, tvCompletedCount, tvOverdueCount;
    private TextView tvAverageRating, tvRecentFeedback;
    private Button btnViewAllTasks, btnProfileLogout;

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

        btnViewAllTasks = findViewById(R.id.btnViewAllTasks);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);
        Button btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnViewAllTasks.setOnClickListener(v -> {
            openTaskList("ALL");
        });

        // Setup mock feedback
        tvAverageRating.setText("Avg Rating: 4.5 / 5.0 ⭐");
        tvRecentFeedback.setText("\"Great job fixing the fan!\" - Room 101\n\"Quick response\" - Room 304");

        btnProfileLogout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerDashboardActivity.this, WorkerProfileActivity.class);
            startActivity(intent);
        });

        // Setup Click Listeners for Dashboard Cards (Filter Passing)
        findViewById(R.id.cardTotal).setOnClickListener(v -> openTaskList("ALL"));
        findViewById(R.id.cardPending).setOnClickListener(v -> openTaskList("PENDING"));
        findViewById(R.id.cardInProgress).setOnClickListener(v -> openTaskList("IN_PROGRESS"));
        findViewById(R.id.cardCompleted).setOnClickListener(v -> openTaskList("COMPLETED"));
        findViewById(R.id.cardOverdue).setOnClickListener(v -> openTaskList("OVERDUE"));
    }

    private void openTaskList(String filterType) {
        Intent intent = new Intent(WorkerDashboardActivity.this, WorkerTaskListActivity.class);
        intent.putExtra("FILTER_TYPE", filterType);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardCounts();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDashboardCounts() {
        List<Task> tasks = DataRepository.getInstance().getTasks();
        
        int total = tasks.size();
        int pending = 0;
        int inProgress = 0;
        int completed = 0;
        int overdue = 0;

        long currentTime = System.currentTimeMillis();

        for (Task task : tasks) {
            String status = task.getStatus();
            if (task.getDeadline() < currentTime && !status.equalsIgnoreCase("Completed")) {
                task.setStatus("Overdue"); // Auto update to overdue if missed deadline
                status = "Overdue";
            }

            if (status.equalsIgnoreCase("Pending")) pending++;
            else if (status.equalsIgnoreCase("In Progress")) inProgress++;
            else if (status.equalsIgnoreCase("Completed")) completed++;
            else if (status.equalsIgnoreCase("Overdue")) overdue++;
        }

        tvTotalCount.setText(String.valueOf(total));
        tvPendingCount.setText(String.valueOf(pending));
        tvInProgressCount.setText(String.valueOf(inProgress));
        tvCompletedCount.setText(String.valueOf(completed));
        tvOverdueCount.setText(String.valueOf(overdue));
    }
}
