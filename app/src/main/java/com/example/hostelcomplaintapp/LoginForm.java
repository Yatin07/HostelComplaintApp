package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.worker.WorkerDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginForm extends AppCompatActivity {

    EditText Email, Password;
    Button btnLogin;
    TextView tvTitle, tvForgotPassword;
    FirebaseAuth auth;

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

            // 🔵 WARDEN LOGIN
            if (role.equalsIgnoreCase("Warden")) {

                db.collection("wardens").get().addOnCompleteListener(task -> {

                    boolean found = false;

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {

                            String dbEmail = doc.getString("email");
                            String dbPass = doc.getString("password");

                            if (dbEmail != null && dbEmail.equals(email)
                                    && dbPass != null && dbPass.equals(password)) {

                                found = true;

                                String name = doc.getString("name");
                                String id = doc.getString("id");

                                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                                prefs.edit()
                                        .putString("name", name)
                                        .putString("id", id)
                                        .putString("email", email)
                                        .apply();

                                startActivity(new Intent(this, HomePage_Warden.class));
                                finish();
                                break;
                            }
                        }

                        if (!found) {
                            Toast.makeText(this, "Warden not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // 🟠 STAFF LOGIN
            else if (role.equalsIgnoreCase("Staff")) {

                db.collection("workers")
                        .whereEqualTo("Email", email)
                        .whereEqualTo("password", password)
                        .get()
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful() && !task.getResult().isEmpty()) {

                                for (QueryDocumentSnapshot doc : task.getResult()) {

                                    String name = doc.getString("Name");
                                    String id = doc.getString("Work_id");

                                    SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                                    prefs.edit()
                                            .putString("name", name)
                                            .putString("id", id)
                                            .putString("email", email)
                                            .apply();

                                    startActivity(new Intent(this, WorkerDashboardActivity.class));
                                    finish();
                                }

                            } else {
                                Toast.makeText(this, "Staff not found", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            // 🟢 STUDENT LOGIN
            else {

                db.collection("IT_Students_data").get().addOnCompleteListener(task -> {

                    boolean found = false;

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot doc : task.getResult()) {

                            String dbEmail = doc.getString("email");
                            String dbPass = doc.getString("password");

                            if (dbEmail != null && dbEmail.equals(email)
                                    && dbPass != null && dbPass.equals(password)) {

                                found = true;

                                String name = doc.getString("name");
                                String id = doc.getString("sapId");

                                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                                prefs.edit()
                                        .putString("name", name)
                                        .putString("id", id)
                                        .putString("email", email)
                                        .apply();

                                startActivity(new Intent(this, HomePage_Student.class));
                                finish();
                                break;
                            }
                        }

                        if (!found) {
                            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        );
    }








//    private void sendResetEmail(String email) {
//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(this,
//                                "Reset link sent to your email",
//                                Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(this,
//                                "Error: " + task.getException().getMessage(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
}