package com.example.hostelcomplaintapp.worker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelcomplaintapp.R;

public class WorkerNavigationHelper {

    public static void setupNavigation(Activity activity) {
        // Setup Bottom Navigation
        if (activity.findViewById(R.id.nav_home) != null) {
            activity.findViewById(R.id.nav_home).setOnClickListener(v -> {
                if (!(activity instanceof WorkerDashboardActivity)) {
                    Intent intent = new Intent(activity, WorkerDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });

            activity.findViewById(R.id.nav_notification).setOnClickListener(v -> {
                Toast.makeText(activity, "Notifications feature coming soon", Toast.LENGTH_SHORT).show();
            });

            activity.findViewById(R.id.nav_tasks).setOnClickListener(v -> {
                if (!(activity instanceof WorkerTaskListActivity)) {
                    Intent intent = new Intent(activity, WorkerTaskListActivity.class);
                    intent.putExtra("FILTER_TYPE", "ALL");
                    activity.startActivity(intent);
                    if (!(activity instanceof WorkerDashboardActivity)) activity.finish();
                }
            });

            activity.findViewById(R.id.nav_profile).setOnClickListener(v -> {
                if (!(activity instanceof WorkerProfileActivity)) {
                    Intent intent = new Intent(activity, WorkerProfileActivity.class);
                    activity.startActivity(intent);
                    if (!(activity instanceof WorkerDashboardActivity)) activity.finish();
                }
            });
        }

        // Setup Top Bar Text
        TextView tvWorkerTopName = activity.findViewById(R.id.tvWorkerTopName);
        TextView tvWorkerTopId = activity.findViewById(R.id.tvWorkerTopId);
        
        if (tvWorkerTopName != null && tvWorkerTopId != null) {
            SharedPreferences prefs = activity.getSharedPreferences("WorkerPrefs", Context.MODE_PRIVATE);
            tvWorkerTopName.setText("Worker: " + prefs.getString("name", "John Doe"));
            tvWorkerTopId.setText("ID: " + prefs.getString("worker_id", "W-12345"));
        }
    }
}
