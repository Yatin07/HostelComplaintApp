package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.worker.WorkerDashboardActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginForm extends AppCompatActivity {

    private static final String TAG = "LoginForm_Log";
    EditText Email, Password;
    Button btnLogin;
    TextView tvTitle, tvForgotPassword;

    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginform);

        tvTitle = findViewById(R.id.tvTitle);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        role = getIntent().getStringExtra("role");
        if (role == null || role.isEmpty()) {
            role = "Student";
        }

        tvTitle.setText(role + " Login");

        btnLogin.setOnClickListener(v -> {

            String emailInput = Email.getText().toString().trim();
            String passwordInput = Password.getText().toString().trim();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Attempting login for role: " + role + " with email: " + emailInput);

            // ──────────────────────────────────────────
            // 🔵 WARDEN LOGIN
            // ──────────────────────────────────────────
            if (role.equalsIgnoreCase("Warden")) {
                db.collection("wardens")
                    .get() // Using get() and manual check because email might be stored in different cases
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean found = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String dbEmail = doc.getString("email");
                                String dbPass  = doc.getString("password");
                                if (dbEmail != null && dbEmail.equalsIgnoreCase(emailInput) && dbPass != null && dbPass.equals(passwordInput)) {
                                    found = true;
                                    saveSessionAndRedirect(doc.getString("name"), doc.getString("id"), emailInput, "warden", HomePage_Warden.class, null);
                                    break;
                                }
                            }
                            if (!found) Toast.makeText(this, "Invalid warden credentials", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
            }

            // ──────────────────────────────────────────
            // 🟠 STAFF / WORKER LOGIN
            // ──────────────────────────────────────────
            else if (role.equalsIgnoreCase("Staff") || role.equalsIgnoreCase("Worker")) {
                db.collection("workers")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean found = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String dbEmail = doc.getString("Email"); // Capital E
                                String dbPass  = doc.getString("password");
                                if (dbEmail != null && dbEmail.equalsIgnoreCase(emailInput) && dbPass != null && dbPass.equals(passwordInput)) {
                                    found = true;
                                    saveSessionAndRedirect(doc.getString("Name"), doc.getString("Work_id"), emailInput, "worker", WorkerDashboardActivity.class, doc.getId());
                                    break;
                                }
                            }
                            if (!found) Toast.makeText(this, "Invalid staff credentials", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
            }

            // ──────────────────────────────────────────
            // 🟢 STUDENT LOGIN
            // ──────────────────────────────────────────
            else {
                db.collection("IT_Students_data")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean found = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String dbEmail = doc.getString("email");
                                String dbPass  = doc.getString("password");
                                if (dbEmail != null && dbEmail.equalsIgnoreCase(emailInput) && dbPass != null && dbPass.equals(passwordInput)) {
                                    found = true;
                                    saveSessionAndRedirect(doc.getString("name"), doc.getString("sapId"), emailInput, "student", HomePage_Student.class, null);
                                    break;
                                }
                            }
                            if (!found) Toast.makeText(this, "Invalid student credentials", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot Password - Coming Soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveSessionAndRedirect(String name, String id, String email, String role, Class<?> targetActivity, String docId) {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name != null ? name : "");
        editor.putString("id", id != null ? id : "");
        editor.putString("email", email);
        editor.putString("role", role);
        if (docId != null) editor.putString("docId", docId);
        editor.apply();

        Log.d(TAG, "Login successful, redirecting to " + targetActivity.getSimpleName());
        startActivity(new Intent(this, targetActivity));
        finish();
    }
}