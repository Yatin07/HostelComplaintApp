package com.example.hostelcomplaintapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class imgProfile_click extends AppCompatActivity {
    Switch switchTheme;
    ImageView btnBack, gotohomepg, imgprof, btnNotification, staff_manage;
    EditText etName;
    Button btnSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgprofile_click);



        /// back button click --> to previous page code start here////
        btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /// back button click --> to previous page code end here////

        /// after clicking help and security popup show code starts here////

        View contactus = findViewById(R.id.contactus);

        contactus.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.help_popup);
            dialog.show();

            ImageView backBtn = dialog.findViewById(R.id.btnBackhelpPopup);

            backBtn.setOnClickListener(v1 -> {
                dialog.dismiss(); // close popup
            });
        });

        /// after clicking help and support popup show code ends here////

        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(imgProfile_click.this, HomePage_Warden.class);
                startActivity(intent1);
            }
        });


        imgprof = findViewById(R.id.imgprof);

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(imgProfile_click.this, imgProfile_click.class);
                startActivity(intent);

            }
        });

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(imgProfile_click.this, Notification.class);
                startActivity(intent1);
            }
        });


        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(imgProfile_click.this, staff_manage.class);
                startActivity(intent1);
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(imgProfile_click.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        /// battery level,wifi,time visible ///
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });





    }
}
