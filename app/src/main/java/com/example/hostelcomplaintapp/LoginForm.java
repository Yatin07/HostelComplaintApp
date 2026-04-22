package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.worker.WorkerDashboardActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginForm extends AppCompatActivity {

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

            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // ──────────────────────────────────────────
            // 🔵 WARDEN LOGIN → collection: "wardens"
            //    Fields: email (lowercase), password, name, id
            // ──────────────────────────────────────────
            if (role.equalsIgnoreCase("Warden")) {

                db.collection("wardens").get().addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean found = false;
                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        String dbEmail = doc.getString("email");
                        String dbPass  = doc.getString("password");

                        // Case-insensitive email match
                        if (dbEmail != null && dbEmail.equalsIgnoreCase(email)
                                && dbPass != null && dbPass.equals(password)) {

                            found = true;

                            String name = doc.getString("name");
                            String id   = doc.getString("id");

                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("name",  name  != null ? name  : "")
                                    .putString("id",    id    != null ? id    : "")
                                    .putString("email", email)
                                    .putString("role",  "warden")
                                    .apply();

                            startActivity(new Intent(this, HomePage_Warden.class));
                            finish();
                            break;
                        }
                    }

                    if (!found) {
                        Toast.makeText(this, "Invalid warden credentials", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            // ──────────────────────────────────────────
            // 🟠 STAFF / WORKER LOGIN → collection: "workers"
            //    Fields: Email (capital E), password, Name (capital N), Work_id, Role
            //    NOTE: The "Staff" collection has no password field — it is NOT used for login.
            //          Staff and Workers share the same login via the "workers" collection.
            // ──────────────────────────────────────────
            else if (role.equalsIgnoreCase("Staff") || role.equalsIgnoreCase("Worker")) {

                // Fetch all workers and do case-insensitive email match
                // (Firestore whereEqualTo is case-sensitive on values, so we handle it client-side)
                db.collection("workers").get().addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean found = false;
                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        String dbEmail = doc.getString("Email");   // capital E in workers collection
                        String dbPass  = doc.getString("password");

                        if (dbEmail != null && dbEmail.equalsIgnoreCase(email)
                                && dbPass != null && dbPass.equals(password)) {

                            found = true;

                            String name   = doc.getString("Name");    // capital N in workers collection
                            String workId = doc.getString("Work_id");
                            String docId  = doc.getId();              // Firestore document ID

                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("name",   name   != null ? name   : "")
                                    .putString("id",     workId != null ? workId : "")
                                    .putString("docId",  docId)       // save doc ID for profile edits
                                    .putString("email",  email)
                                    .putString("role",   "worker")
                                    .apply();

                            startActivity(new Intent(this, WorkerDashboardActivity.class));
                            finish();
                            break;
                        }
                    }

                    if (!found) {
                        Toast.makeText(this, "Invalid staff credentials", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            // ──────────────────────────────────────────
            // 🟢 STUDENT LOGIN → collection: "IT_Students_data"
            //    Fields: email (lowercase), password, name, sapId
            //    Note: emails in DB may be mixed/uppercase (e.g. SOUMY.RAGHUVANSHI116@nmims.in)
            // ──────────────────────────────────────────
            else {

                db.collection("IT_Students_data").get().addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Login failed. Try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean found = false;
                    for (QueryDocumentSnapshot doc : task.getResult()) {

                        String dbEmail = doc.getString("email");
                        String dbPass  = doc.getString("password");

                        // Case-insensitive email match handles UPPERCASE emails stored in DB
                        if (dbEmail != null && dbEmail.equalsIgnoreCase(email)
                                && dbPass != null && dbPass.equals(password)) {

                            found = true;

                            String name  = doc.getString("name");
                            String sapId = doc.getString("sapId");

                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("name",  name  != null ? name  : "")
                                    .putString("id",    sapId != null ? sapId : "")
                                    .putString("email", email)
                                    .putString("role",  "student")
                                    .apply();

                            startActivity(new Intent(this, HomePage_Student.class));
                            finish();
                            break;
                        }
                    }

                    if (!found) {
                        Toast.makeText(this, "Invalid student credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot Password - Coming Soon", Toast.LENGTH_SHORT).show()
        );
    }
}