package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.worker.WorkerDashboardActivity;

public class LoginForm extends AppCompatActivity {

    EditText Email, Password;
    Button btnLogin;
    TextView tvTitle, tvForgotPassword;

    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginform);

        // 🔹 Initialize views
        tvTitle = findViewById(R.id.tvTitle);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // 🔹 Get role safely
        role = getIntent().getStringExtra("role");

        if (role == null || role.isEmpty()) {
            role = "Student"; // default
        }

        // 🔹 Set title
        tvTitle.setText(role + " Login");

        // 🔥 LOGIN BUTTON
//        btnLogin.setOnClickListener(v -> {
//
//            String email = Email.getText().toString().trim();
//            String password = Password.getText().toString().trim();
//
//            // ✅ Debug (remove later if you want)
//            Toast.makeText(this, "Role: " + role, Toast.LENGTH_SHORT).show();
//
//            // ✅ Validation
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(LoginForm.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // 🔵 WARDEN LOGIN
//            if (role.equalsIgnoreCase("Warden")) {
//
//                if (email.equalsIgnoreCase("warden@gmail.com") && password.equals("123")) {
//                    startActivity(new Intent(LoginForm.this, HomePage_Warden.class));
//                    finish();
//                } else {
//                    showError();
//                }
//
//            }
//
//            // 🟠 STAFF LOGIN
//            else if (role.equalsIgnoreCase("Staff")) {
//
//                if (email.equalsIgnoreCase("staff@gmail.com") && password.equals("123")) {
//                    startActivity(new Intent(LoginForm.this, WorkerDashboardActivity.class));
//                    finish();
//                } else {
//                    showError();
//                }
//
//            }
//
//            // 🟢 STUDENT LOGIN
//            else {
//
//                if (email.equalsIgnoreCase("student@gmail.com") && password.equals("123")) {
//                    startActivity(new Intent(LoginForm.this, HomePage_Student.class));
//                    finish();
//                } else {
//                    showError();
//                }
//            }
//        });

        // 🔹 Forgot Password
        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(LoginForm.this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    // 🔴 Error Message
    private void showError() {
        Toast.makeText(this, "Invalid credentials for selected role", Toast.LENGTH_SHORT).show();
    }
}