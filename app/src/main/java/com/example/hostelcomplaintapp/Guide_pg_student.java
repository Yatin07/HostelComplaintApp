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

public class Guide_pg_student extends AppCompatActivity {

    ImageView btnNotification, howtouseapp, imgprof, gotohomepg, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guide_pg_student);


        btnNotification = findViewById(R.id.btnNotification);
        howtouseapp = findViewById(R.id.howtouseapp);
        imgprof = findViewById(R.id.imgprof);
        gotohomepg = findViewById(R.id.gotohomepg);

btnNotification.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Guide_pg_student.this, Notification_student.class);
        startActivity(intent);
    }
});


btnBack=findViewById(R.id.btnBack);
btnBack.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Guide_pg_student.this, HomePage_Student.class);
        startActivity(intent);
    }
});


        howtouseapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide_pg_student.this, Guide_pg_student.class);
                startActivity(intent);
            }
        });


        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide_pg_student.this, student_profile_pg.class);
                startActivity(intent);
            }
        });


        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide_pg_student.this, HomePage_Student.class);
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