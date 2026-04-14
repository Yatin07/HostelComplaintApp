package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.util.Log;
=======
>>>>>>> main
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    Button btnStudent,btnStaff,btnWarden;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnStaff = findViewById(R.id.btnStaff);
        Button btnWarden = findViewById(R.id.btnWarden);

<<<<<<< HEAD
        btnStudent.setOnClickListener(v -> navigateBasedOnRole("Student"));

        btnStaff.setOnClickListener(v -> navigateBasedOnRole("Worker"));

        btnWarden.setOnClickListener(v -> navigateBasedOnRole("Warden"));
=======
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
>>>>>>> main



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
<<<<<<< HEAD

    private void navigateBasedOnRole(String role) {
        Log.d("ROLE_CHECK", role);
        if (role.equals("Student")) {
            startActivity(new Intent(MainActivity.this, HomePage_Student.class));
        } else if (role.equals("Warden")) {
            startActivity(new Intent(MainActivity.this, HomePage_Warden.class));
        } else if (role.equals("Worker")) {
            startActivity(new Intent(MainActivity.this, com.example.hostelcomplaintapp.worker.WorkerDashboardActivity.class));
        }
    }
=======
>>>>>>> main
}
