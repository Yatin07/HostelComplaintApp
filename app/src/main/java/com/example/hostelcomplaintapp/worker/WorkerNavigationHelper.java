package com.example.hostelcomplaintapp.worker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.Worker_notification_clk;
import com.example.hostelcomplaintapp.worker_guide_clk;

public class WorkerNavigationHelper {

    public static void setupNavigation(Activity activity) {
        // Setup Bottom Navigation
        if (activity.findViewById(R.id.gotohomepg) != null) {
            activity.findViewById(R.id.gotohomepg).setOnClickListener(v -> {
                if (!(activity instanceof WorkerDashboardActivity)) {
                    Intent intent = new Intent(activity, WorkerDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        if (activity.findViewById(R.id.btnNotification) != null) {
            activity.findViewById(R.id.btnNotification).setOnClickListener(v -> {
                if (!(activity instanceof Worker_notification_clk)) {
                    Intent intent = new Intent(activity, Worker_notification_clk.class);
                    activity.startActivity(intent);
                    if (!(activity instanceof WorkerDashboardActivity)) activity.finish();
                }
            });
        }

        if (activity.findViewById(R.id.staff_manage) != null) {
            activity.findViewById(R.id.staff_manage).setOnClickListener(v -> {
                if (!(activity instanceof worker_guide_clk)) {
                    Intent intent = new Intent(activity, worker_guide_clk.class);
                    activity.startActivity(intent);
                    if (!(activity instanceof WorkerDashboardActivity)) activity.finish();
                }
            });
        }

        if (activity.findViewById(R.id.imgproff) != null) {
            activity.findViewById(R.id.imgproff).setOnClickListener(v -> {
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
            SharedPreferences prefs = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
            tvWorkerTopName.setText("Worker: " + prefs.getString("name", "Worker"));
            tvWorkerTopId.setText("ID: " + prefs.getString("id", "ID"));
        }
    }
}
