package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hostelcomplaintapp.worker.WorkerDashboardActivity;
import com.example.hostelcomplaintapp.worker.WorkerProfileActivity;
import com.example.hostelcomplaintapp.worker.Worker_notification_clk;

public class worker_guide_clk extends AppCompatActivity {

    ImageView notify, staff_manage, imgprof, home, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_worker_guide_clk);


        notify = findViewById(R.id.notify);
        staff_manage = findViewById(R.id.staff_manage);
        imgprof = findViewById(R.id.imgprof);
        home = findViewById(R.id.home);
        btnBack=findViewById(R.id.btnBack);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(worker_guide_clk.this, Worker_notification_clk.class);
                startActivity(intent);
            }
        });

        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(worker_guide_clk.this, worker_guide_clk.class);
                startActivity(intent);
            }
        });

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(worker_guide_clk.this, WorkerProfileActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(worker_guide_clk.this, WorkerDashboardActivity.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}