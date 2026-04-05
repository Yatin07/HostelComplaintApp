package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import android.widget.LinearLayout;

public class HomePage_Student extends AppCompatActivity {

    private MaterialCardView cardRaiseComplaint;
    private MaterialCardView cardMyComplaints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_student);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvPending = findViewById(R.id.tvPendingCount);
        TextView tvInProgress = findViewById(R.id.tvInProgressCount);
        TextView tvResolved = findViewById(R.id.tvResolvedCount);


        // Initialize Views
        cardRaiseComplaint = findViewById(R.id.cardRaiseComplaint);
        cardMyComplaints = findViewById(R.id.cardMyComplaints);

        // Card Click Listeners
        cardRaiseComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage_Student.this, "Opening Raise Complaint form...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePage_Student.this, RaiseComplaintActivity.class);
                startActivity(intent);
            }
        });

        cardMyComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage_Student.this, "Navigate to MyComplaintsActivity", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(HomePage_Student.this, MyComplaintsActivity.class);
                // startActivity(intent);
            }
        });

        LinearLayout navComplaints = findViewById(R.id.nav_complaints);
        LinearLayout navProfile = findViewById(R.id.imgprof); // using the id defined in XML

        if (navComplaints != null) {
            navComplaints.setOnClickListener(v -> {
                Toast.makeText(this, "Go to My Complaints", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, MyComplaintsActivity.class));
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            });
        }




    }
} 
