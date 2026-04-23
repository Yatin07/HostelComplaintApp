package com.example.hostelcomplaintapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


        TextView tvName = findViewById(R.id.tvName1);
        TextView tvEmail = findViewById(R.id.tvEmail);

        SharedPreferences prefss = getSharedPreferences("user", MODE_PRIVATE);

        String name = prefss.getString("name", "Warden");
        String email = prefss.getString("email", "email@gmail.com");

// ✅ SET DATA
        if (tvName != null) tvName.setText(name);
        if (tvEmail != null) tvEmail.setText(email);



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

        // Bottom Navigation - use LinearLayout parents for better touch response
        LinearLayout llHome = findViewById(R.id.llHome);
        LinearLayout llNotification = findViewById(R.id.llNotification);
        LinearLayout llStaff = findViewById(R.id.llStaff);
        LinearLayout llProfile = findViewById(R.id.llProfile);

        llHome.setOnClickListener(v -> {
            Intent intent1 = new Intent(imgProfile_click.this, HomePage_Warden.class);
            startActivity(intent1);
        });

        llNotification.setOnClickListener(v -> {
            Intent intent1 = new Intent(imgProfile_click.this, Notification.class);
            startActivity(intent1);
        });

        llStaff.setOnClickListener(v -> {
            Intent intent1 = new Intent(imgProfile_click.this, staff_manage.class);
            startActivity(intent1);
        });

        llProfile.setOnClickListener(v -> {
            // Already on Profile page
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(imgProfile_click.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        /// battery level,wifi,time visible ///
        androidx.core.view.WindowInsetsControllerCompat controller = 
            new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}
