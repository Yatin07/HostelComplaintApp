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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home); // default selected

        // Click Listeners
        cardRaiseComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RaiseComplaintActivity (Using Toast temporarily if activity is not created yet)
                Toast.makeText(HomePage_Student.this, "Opening Raise Complaint form...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomePage_Student.this, RaiseComplaintActivity.class);
                startActivity(intent);
            }
        });

        cardMyComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MyComplaintsActivity (Using Toast temporarily if activity is not created yet)
                Toast.makeText(HomePage_Student.this, "Navigate to MyComplaintsActivity", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(HomePage_Student.this, MyComplaintsActivity.class);
                // startActivity(intent);
            }
        });


        bottomNav.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                return true;
            }

            else if (item.getItemId() == R.id.nav_complaints) {
                Toast.makeText(this, "Go to My Complaints", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, MyComplaintsActivity.class));
                return true;
            }

            else if (item.getItemId() == R.id.nav_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });




    }
} 
