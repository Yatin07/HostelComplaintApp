package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class emergency_issue_student extends AppCompatActivity {

    // ── View refs ──
    private EditText etWarden1Name, etWarden1Phone;
    private EditText etWarden2Name, etWarden2Phone;
    private EditText etWarden3Name, etWarden3Phone;
    private EditText etDoctorPhone;
    private Button   btnSave;
    private ImageView btnBack, gotohomepg, btnNotification, howtouseapp, imgprof;

    // ── State ──
    private boolean isEditMode = false;
    private boolean isWarden   = false;  // set from Intent extra

    // ── Firestore ──
    private static final String COLLECTION = "emergency_contacts";
    private static final String DOCUMENT   = "contacts";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_issue_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ── Bind views ──
        etWarden1Name  = findViewById(R.id.etWarden1Name);
        etWarden1Phone = findViewById(R.id.etWarden1Phone);
        etWarden2Name  = findViewById(R.id.etWarden2Name);
        etWarden2Phone = findViewById(R.id.etWarden2Phone);
        etWarden3Name  = findViewById(R.id.etWarden3Name);
        etWarden3Phone = findViewById(R.id.etWarden3Phone);
        etDoctorPhone  = findViewById(R.id.etDoctorPhone);
        btnSave        = findViewById(R.id.btnSaveContacts);
        btnBack        = findViewById(R.id.btnBack);

        // ✅ BOTTOM NAV NAVIGATION
        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(v -> finish());

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(emergency_issue_student.this, Notification_student.class));
        });

        howtouseapp = findViewById(R.id.howtouseapp);
        howtouseapp.setOnClickListener(v -> {
            startActivity(new Intent(emergency_issue_student.this, Guide_pg_student.class));
        });

        imgprof = findViewById(R.id.imgprof);
        imgprof.setOnClickListener(v -> {
            if (isWarden) {
                startActivity(new Intent(emergency_issue_student.this, imgProfile_click.class));
            } else {
                startActivity(new Intent(emergency_issue_student.this, student_profile_pg.class));
            }
        });

        db = FirebaseFirestore.getInstance();

        // ── Determine role from calling activity ──
        String role = getIntent().getStringExtra("role");
        isWarden = "warden".equalsIgnoreCase(role);

        if (!isWarden) {
            // Student: always view-only — hide save icon
            btnSave.setVisibility(View.GONE);
            setEditableAll(false);
        }

        // ── Load data from Firestore on open ──
        loadContacts();

        // ── Back button ──
        btnBack.setOnClickListener(v -> finish());

        // ── Save button (Warden only) ──
        btnSave.setOnClickListener(v -> {
            if (!isWarden) return;
            saveContacts();
        });
    }

    // ────────────────────────────────────────────
    //  LOAD
    // ────────────────────────────────────────────
    private void loadContacts() {
        db.collection(COLLECTION).document(DOCUMENT)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        setField(etWarden1Name,  doc.getString("warden1_name"),  "Warden 1");
                        setField(etWarden1Phone, doc.getString("warden1_phone"), "+91 000000000");
                        setField(etWarden2Name,  doc.getString("warden2_name"),  "Warden 2");
                        setField(etWarden2Phone, doc.getString("warden2_phone"), "+91 000000000");
                        setField(etWarden3Name,  doc.getString("warden3_name"),  "Warden 3");
                        setField(etWarden3Phone, doc.getString("warden3_phone"), "+91 000000000");
                        setField(etDoctorPhone,  doc.getString("doctor_phone"),  "+91 000000000");
                    }
                    // If doc doesn't exist yet — keep XML defaults shown
                })
                .addOnFailureListener(e -> {
                    Log.e("EMERGENCY", "Load failed: " + e.getMessage());
                    // Keep XML defaults on failure — no crash
                });
    }

    // ────────────────────────────────────────────
    //  SAVE
    // ────────────────────────────────────────────
    private void saveContacts() {
        Map<String, Object> data = new HashMap<>();
        data.put("warden1_name",  etWarden1Name.getText().toString().trim());
        data.put("warden1_phone", etWarden1Phone.getText().toString().trim());
        data.put("warden2_name",  etWarden2Name.getText().toString().trim());
        data.put("warden2_phone", etWarden2Phone.getText().toString().trim());
        data.put("warden3_name",  etWarden3Name.getText().toString().trim());
        data.put("warden3_phone", etWarden3Phone.getText().toString().trim());
        data.put("doctor_phone",  etDoctorPhone.getText().toString().trim());

        DocumentReference ref = db.collection(COLLECTION).document(DOCUMENT);

        // set() with merge = true creates or updates without wiping other fields
        ref.set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Contacts saved ✅", Toast.LENGTH_SHORT).show();
                    disableEditing();
                })
                .addOnFailureListener(e -> {
                    Log.e("EMERGENCY", "Save failed: " + e.getMessage());
                    Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ────────────────────────────────────────────
    //  EDIT MODE HELPERS
    // ────────────────────────────────────────────
    private void enableEditing() {
        isEditMode = true;
        setEditableAll(true);
        btnSave.setVisibility(View.VISIBLE);
        // Switch icon to a "close/cancel" icon to signal tapping again = cancel
        etWarden1Name.requestFocus();
    }

    private void disableEditing() {
        isEditMode = false;
        setEditableAll(false);
        btnSave.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
    }


    private void setEditableAll(boolean enabled) {
        etWarden1Name.setEnabled(enabled);
        etWarden1Phone.setEnabled(enabled);
        etWarden2Name.setEnabled(enabled);
        etWarden2Phone.setEnabled(enabled);
        etWarden3Name.setEnabled(enabled);
        etWarden3Phone.setEnabled(enabled);
        etDoctorPhone.setEnabled(enabled);
    }

    // ── Helper: set text, falling back to default if value is null/empty ──
    private void setField(EditText et, String value, String defaultValue) {
        if (value != null && !value.isEmpty()) {
            et.setText(value);
        } else {
            et.setText(defaultValue);
        }
    }
}