package com.example.hostelcomplaintapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnStudent, btnStaff, btnWarden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ SESSION MANAGEMENT: Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if (!role.isEmpty()) {
            Intent intent;
            switch (role.toLowerCase()) {
                case "student":
                    intent = new Intent(MainActivity.this, HomePage_Student.class);
                    break;
                case "warden":
                    intent = new Intent(MainActivity.this, HomePage_Warden.class);
                    break;
                case "worker":
                case "staff":
                    intent = new Intent(MainActivity.this, com.example.hostelcomplaintapp.worker.WorkerDashboardActivity.class);
                    break;
                default:
                    intent = null;
            }

            if (intent != null) {
                startActivity(intent);
                finish(); // Close MainActivity so user can't go back to role selection
                return;
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnStaff = findViewById(R.id.btnStaff);
        Button btnWarden = findViewById(R.id.btnWarden);

        btnStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginForm.class);
            intent.putExtra("role", "student");
            startActivity(intent);
        });

        btnStaff.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginForm.class);
            intent.putExtra("role", "staff");
            startActivity(intent);
        });

        btnWarden.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginForm.class);
            intent.putExtra("role", "warden");
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
