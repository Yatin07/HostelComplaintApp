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

public class pendingcomplaint extends AppCompatActivity {

    ImageView btnBack, gotohomepg, btnNotification, staff_manage, imgProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendingcomplaint);


        /// back button click --> to previous page code start here////

        btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /// back button click --> to previous page code start here////

        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, HomePage_Warden.class);
                startActivity(intent1);
            }
        });

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, Notification.class);
                startActivity(intent1);
            }
        });

        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, staff_manage.class);
                startActivity(intent1);
            }
        });

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(pendingcomplaint.this, imgProfile_click.class);
                startActivity(intent1);
            }
        });









        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}