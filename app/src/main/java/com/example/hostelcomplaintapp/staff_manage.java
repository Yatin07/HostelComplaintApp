package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

/**
 * Staff Management Screen (used by Warden).
 *
 * DATABASE: All staff/worker records are stored in a SINGLE collection: "workers"
 * The old "Staff" collection in Firestore is no longer used and can be deleted.
 *
 * Unified "workers" document schema:
 *   Name      (String)  – display name
 *   Email     (String)  – login email
 *   password  (String)  – login password
 *   Work_id   (String)  – their staff/worker ID  (e.g. "W-001")
 *   Role      (String)  – department / job role  (e.g. "Electrician", "Plumber")
 *   phone     (String)  – contact number
 */
public class staff_manage extends AppCompatActivity {

    ImageView btnBack, gotohomepg, btnNotification, imgprof, staff_manage;
    LinearLayout staffContainer;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manage);

        btnBack = findViewById(R.id.btnBack);
        staffContainer = findViewById(R.id.staffContainer);

        btnBack.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        // ──────────────────────────────────────────────────────────────
        // LOAD all staff/workers from the unified "workers" collection
        // ──────────────────────────────────────────────────────────────
        db.collection("workers").addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) {
                return;
            }

            staffContainer.removeAllViews();

            for (DocumentSnapshot data : snapshot.getDocuments()) {

                String key    = data.getId();
                String name   = data.getString("Name");    // capital N
                String workId = data.getString("Work_id");
                String role   = data.getString("Role");    // department / job role
                String email  = data.getString("Email");   // capital E
                String phone  = data.getString("phone");

                View cardView = getLayoutInflater().inflate(R.layout.newstaff_card, staffContainer, false);

                TextView nameTv  = cardView.findViewById(R.id.tvName);
                TextView idTv    = cardView.findViewById(R.id.tvId);
                TextView deptTv  = cardView.findViewById(R.id.tvDept);
                ImageView arrowBtn = cardView.findViewById(R.id.arrowBtn);
                Button removeBtn = cardView.findViewById(R.id.removeBtn);

                nameTv.setText(name   != null ? name   : "");
                idTv.setText("Work ID: " + (workId != null ? workId : ""));
                deptTv.setText("Role: " + (role   != null ? role   : ""));

                // Details popup
                arrowBtn.setOnClickListener(v ->
                        new AlertDialog.Builder(staff_manage.this)
                                .setTitle("Staff Details")
                                .setMessage(
                                        "Name: "    + name    + "\n\n" +
                                        "Work ID: " + workId  + "\n\n" +
                                        "Role: "    + role    + "\n\n" +
                                        "Email: "   + email   + "\n\n" +
                                        "Phone: "   + phone
                                )
                                .show()
                );

                // Delete from "workers"
                removeBtn.setOnClickListener(v ->
                        new AlertDialog.Builder(staff_manage.this)
                                .setTitle("Remove Staff")
                                .setMessage("Are you sure you want to remove " + name + "?")
                                .setPositiveButton("Remove", (dialog, which) ->
                                        db.collection("workers").document(key).delete()
                                                .addOnSuccessListener(a ->
                                                        Toast.makeText(this, name + " removed", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                                )
                                .setNegativeButton("Cancel", null)
                                .show()
                );

                staffContainer.addView(cardView);
            }
        });

        // ──────────────────────────────────────────────────────────────
        // ADD new staff/worker → writes to unified "workers" collection
        // ──────────────────────────────────────────────────────────────
        ImageView addBtn = findViewById(R.id.addStaffBtn);

        addBtn.setOnClickListener(v -> {

            View view = getLayoutInflater().inflate(R.layout.staff_info_popup, null);

            EditText etName     = view.findViewById(R.id.etName);
            EditText etId       = view.findViewById(R.id.etId);
            EditText etDept     = view.findViewById(R.id.etDept);
            EditText etEmail    = view.findViewById(R.id.etEmail);
            EditText etPhone    = view.findViewById(R.id.etPhone);
            EditText etPassword = view.findViewById(R.id.etPassword);

            new AlertDialog.Builder(this)
                    .setTitle("Add Staff / Worker")
                    .setView(view)
                    .setPositiveButton("Save", (dialog, which) -> {

                        String nameStr     = etName.getText().toString().trim();
                        String idStr       = etId.getText().toString().trim();
                        String roleStr     = etDept.getText().toString().trim();   // "dept" field = Role
                        String emailStr    = etEmail.getText().toString().trim();
                        String phoneStr    = etPhone.getText().toString().trim();
                        String passwordStr = etPassword.getText().toString().trim();

                        if (nameStr.isEmpty() || idStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty()) {
                            Toast.makeText(this, "Name, ID, Email and Password are required", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Match the exact field names used by WorkerProfileActivity & LoginForm
                        Map<String, Object> workerData = new HashMap<>();
                        workerData.put("Name",     nameStr);     // capital N — matches WorkerProfileActivity
                        workerData.put("Work_id",  idStr);       // matches LoginForm & WorkerProfileActivity
                        workerData.put("Role",     roleStr);     // matches WorkerProfileActivity
                        workerData.put("Email",    emailStr);    // capital E — matches LoginForm
                        workerData.put("phone",    phoneStr);
                        workerData.put("password", passwordStr); // matches LoginForm

                        db.collection("workers")
                                .add(workerData)
                                .addOnSuccessListener(ref ->
                                        Toast.makeText(this, "Staff added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        // Bottom Navigation - use LinearLayout parents for better touch response
        LinearLayout llHome = findViewById(R.id.llHome);
        LinearLayout llNotification = findViewById(R.id.llNotification);
        LinearLayout llStaff = findViewById(R.id.llStaff);
        LinearLayout llProfile = findViewById(R.id.llProfile);

        llHome.setOnClickListener(v -> startActivity(new Intent(staff_manage.this, HomePage_Warden.class)));

        llNotification.setOnClickListener(v -> startActivity(new Intent(staff_manage.this, Notification.class)));

        llStaff.setOnClickListener(v -> {
            // Already on Staff page
        });

        llProfile.setOnClickListener(v -> startActivity(new Intent(staff_manage.this, imgProfile_click.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}