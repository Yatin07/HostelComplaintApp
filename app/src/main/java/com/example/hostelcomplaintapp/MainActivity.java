package com.example.hostelcomplaintapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hostelcomplaintapp.worker.WorkerDashboardActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_Log";
    Button btnStudent, btnStaff, btnWarden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        try {
            // 1. Check Session
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            String role = prefs.getString("role", "");
            Log.d(TAG, "Current Session Role: " + role);

            if (role != null && !role.isEmpty()) {
                Intent intent = null;
                if (role.equalsIgnoreCase("student")) {
                    intent = new Intent(this, HomePage_Student.class);
                } else if (role.equalsIgnoreCase("warden")) {
                    intent = new Intent(this, HomePage_Warden.class);
                } else if (role.equalsIgnoreCase("staff") || role.equalsIgnoreCase("worker")) {
                    intent = new Intent(this, WorkerDashboardActivity.class);
                }

                if (intent != null) {
                    Log.d(TAG, "Redirecting to " + role + " dashboard");
                    startActivity(intent);
                    finish();
                    return;
                }
            }

            // 2. Load UI if no session
            Log.d(TAG, "No session found, loading landing page");
            setContentView(R.layout.activity_main);

            btnStudent = findViewById(R.id.btnStudent);
            btnStaff = findViewById(R.id.btnStaff);
            btnWarden = findViewById(R.id.btnWarden);

            if (btnStudent != null) {
                btnStudent.setOnClickListener(v -> navigateToLogin("student"));
            }
            if (btnStaff != null) {
                btnStaff.setOnClickListener(v -> navigateToLogin("staff"));
            }
            if (btnWarden != null) {
                btnWarden.setOnClickListener(v -> navigateToLogin("warden"));
            }

            // Handle system insets
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "CRITICAL ERROR in MainActivity", e);
            Toast.makeText(this, "Startup Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin(String role) {
        try {
            Intent intent = new Intent(this, LoginForm.class);
            intent.putExtra("role", role);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to login", e);
        }
    }
}
