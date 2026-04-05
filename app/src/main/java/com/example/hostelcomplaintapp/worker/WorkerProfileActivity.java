package com.example.hostelcomplaintapp.worker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.MainActivity;
import com.example.hostelcomplaintapp.R;

public class WorkerProfileActivity extends AppCompatActivity {

    private TextView tvWorkerName, tvWorkerEmail, tvWorkerRole, tvWorkerId;
    private EditText etWorkerName, etWorkerRole;
    private Button btnLogout, btnBack, btnEditProfile;
    
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        tvWorkerName = findViewById(R.id.tvWorkerName);
        tvWorkerEmail = findViewById(R.id.tvWorkerEmail);
        tvWorkerRole = findViewById(R.id.tvWorkerRole);
        tvWorkerId = findViewById(R.id.tvWorkerId);
        
        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkerRole = findViewById(R.id.etWorkerRole);

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
            etWorkerRole.setText(tvWorkerRole.getText().toString());

            // Switch visibility
            tvWorkerName.setVisibility(View.GONE);
            tvWorkerRole.setVisibility(View.GONE);
            etWorkerName.setVisibility(View.VISIBLE);
            etWorkerRole.setVisibility(View.VISIBLE);
        } else {
            // Validate and Save
            String updatedName = etWorkerName.getText().toString().trim();
            String updatedRole = etWorkerRole.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (updatedRole.isEmpty()) {
                Toast.makeText(this, "Role cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("WorkerPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", updatedName);
            editor.putString("role", updatedRole);
            editor.apply();

            // Update TextViews
            tvWorkerName.setText(updatedName);
            tvWorkerRole.setText(updatedRole);

            // Revert editing state
            isEditing = false;
            btnEditProfile.setText("Edit Profile");

            // Switch visibility back
            etWorkerName.setVisibility(View.GONE);
            etWorkerRole.setVisibility(View.GONE);
            tvWorkerName.setVisibility(View.VISIBLE);
            tvWorkerRole.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWorkerData() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkerPrefs", Context.MODE_PRIVATE);
        
        String name = sharedPreferences.getString("name", "John Doe");
        String email = sharedPreferences.getString("email", "john.doe@hostel.com");
        String role = sharedPreferences.getString("role", "Electrician");
        String id = sharedPreferences.getString("worker_id", "W-12345");

        tvWorkerName.setText(name);
        tvWorkerEmail.setText(email);
        tvWorkerRole.setText(role);
        tvWorkerId.setText(id);
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
