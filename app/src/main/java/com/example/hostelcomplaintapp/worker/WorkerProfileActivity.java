package com.example.hostelcomplaintapp.worker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hostelcomplaintapp.MainActivity;
import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.worker.Worker_notification_clk;
import com.example.hostelcomplaintapp.worker_guide_clk;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WorkerProfileActivity extends AppCompatActivity {

    private TextView tvWorkerName, tvWorkerEmail, tvWorkerRole, tvWorkerId;
    private EditText etWorkerName, etWorkerEmail, etWorkerRole, etWorkerId;
    private Button btnLogout, btnBack, btnEditProfile;
    ImageView prof, rules, notify, home;
    
    private boolean isEditing = false;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        prof = findViewById(R.id.prof);
        rules = findViewById(R.id.rules);
        notify = findViewById(R.id.notify);
        home = findViewById(R.id.home);

        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerProfileActivity.this, WorkerProfileActivity.class);
                startActivity(intent);
            }
        });

        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerProfileActivity.this, worker_guide_clk.class);
                startActivity(intent);
            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerProfileActivity.this, Worker_notification_clk.class);
                startActivity(intent);
            }
        });

     home.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent(WorkerProfileActivity.this, WorkerDashboardActivity.class);
             startActivity(intent);
         }
     });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        db = FirebaseFirestore.getInstance();

        tvWorkerName = findViewById(R.id.tvWorkerName);
        tvWorkerEmail = findViewById(R.id.tvWorkerEmail);
        tvWorkerRole = findViewById(R.id.tvWorkerRole);
        tvWorkerId = findViewById(R.id.tvWorkerId);
        
        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkerEmail = findViewById(R.id.etWorkerEmail);
        etWorkerRole = findViewById(R.id.etWorkerRole);
        etWorkerId = findViewById(R.id.etWorkerId);

        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadWorkerData();

        btnEditProfile.setOnClickListener(v -> toggleEditMode());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void toggleEditMode() {
        if (!isEditing) {
            // Enable editing
            isEditing = true;
            btnEditProfile.setText("Save Changes");

            // Bring existing data to EditTexts
            etWorkerName.setText(tvWorkerName.getText().toString());
            etWorkerEmail.setText(tvWorkerEmail.getText().toString());
            etWorkerRole.setText(tvWorkerRole.getText().toString());
            etWorkerId.setText(tvWorkerId.getText().toString());

            // Switch visibility
            tvWorkerName.setVisibility(View.GONE);
            tvWorkerEmail.setVisibility(View.GONE);
            tvWorkerRole.setVisibility(View.GONE);
            tvWorkerId.setVisibility(View.GONE);

            etWorkerName.setVisibility(View.VISIBLE);
            etWorkerEmail.setVisibility(View.VISIBLE);
            etWorkerRole.setVisibility(View.VISIBLE);
            etWorkerId.setVisibility(View.VISIBLE);
            
            // Ensure focusability
            etWorkerName.setEnabled(true);
            etWorkerEmail.setEnabled(true);
            etWorkerRole.setEnabled(true);
            etWorkerId.setEnabled(true);
            
        } else {
            // Validate and Save
            String updatedName = etWorkerName.getText().toString().trim();
            String updatedEmail = etWorkerEmail.getText().toString().trim();
            String updatedRole = etWorkerRole.getText().toString().trim();
            String updatedId = etWorkerId.getText().toString().trim();

            if (updatedName.isEmpty() || updatedRole.isEmpty()) {
                Toast.makeText(this, "Name and Role cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("Name", updatedName);
            updates.put("Email", updatedEmail);
            updates.put("Role", updatedRole);
            updates.put("Work_id", updatedId);

            db.collection("workers")
              .document("W-12345")
              .update(updates)
              .addOnSuccessListener(aVoid -> {
                  Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();

                  // Update TextViews
                  tvWorkerName.setText(updatedName);
                  tvWorkerEmail.setText(updatedEmail);
                  tvWorkerRole.setText(updatedRole);
                  tvWorkerId.setText(updatedId);

                  // Revert editing state
                  isEditing = false;
                  btnEditProfile.setText("Edit Profile");

                  // Switch visibility back
                  etWorkerName.setVisibility(View.GONE);
                  etWorkerEmail.setVisibility(View.GONE);
                  etWorkerRole.setVisibility(View.GONE);
                  etWorkerId.setVisibility(View.GONE);

                  tvWorkerName.setVisibility(View.VISIBLE);
                  tvWorkerEmail.setVisibility(View.VISIBLE);
                  tvWorkerRole.setVisibility(View.VISIBLE);
                  tvWorkerId.setVisibility(View.VISIBLE);
              })
              .addOnFailureListener(e -> {
                  Toast.makeText(this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
              });



        }
    }

    private void loadWorkerData() {
        db.collection("workers").document("W-12345")
          .get()
          .addOnSuccessListener(document -> {
              if (document != null && document.exists()) {
                  String name = document.getString("Name");
                  String email = document.getString("Email");
                  String role = document.getString("Role");
                  String workId = document.getString("Work_id");

                  tvWorkerName.setText(name != null ? name : "");
                  tvWorkerEmail.setText(email != null ? email : "");
                  tvWorkerRole.setText(role != null ? role : "");
                  tvWorkerId.setText(workId != null ? workId : "");

                  etWorkerName.setText(name != null ? name : "");
                  etWorkerEmail.setText(email != null ? email : "");
                  etWorkerRole.setText(role != null ? role : "");
                  etWorkerId.setText(workId != null ? workId : "");
              } else {
                  Toast.makeText(this, "Worker document not found.", Toast.LENGTH_SHORT).show();
              }
          })
          .addOnFailureListener(e -> {
              Toast.makeText(this, "Failed to load data.", Toast.LENGTH_SHORT).show();
          });
    }

    private void performLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(WorkerProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
