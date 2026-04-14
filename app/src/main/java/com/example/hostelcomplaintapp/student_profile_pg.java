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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class student_profile_pg extends AppCompatActivity {

    Switch switchTheme;
    ImageView btnBack, gotohomepg, imgprof, btnNotification, staff_manage, backBtn;
    EditText etName;
    Button btnSave;
    TextView tvName, EditProfileinformation, security;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_profile_pg);


        //toggle button start//
        switchTheme = findViewById(R.id.switchTheme);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        //toggle button end//


        /// back button click --> to previous page code start here////

        btnBack=findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        /// back button click --> to previous page code end here////

        /// to change the name / edit profile info code starts  here////

        tvName = findViewById(R.id.tvName1);
        EditProfileinformation = findViewById(R.id.EditProfileinformation);

        SharedPreferences prefs1 = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedName = prefs1.getString("username", "Default Name");
        tvName.setText(savedName);



        EditProfileinformation.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.center_popup_editprofileinfo);

            etName = dialog.findViewById(R.id.etName);
            btnSave = dialog.findViewById(R.id.btnSave);

            btnSave.setOnClickListener(view -> {
                String newName = etName.getText().toString().trim();

                if (!newName.isEmpty()) {

                    // ✅ Save in SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", newName);
                    editor.apply();

                    tvName.setText(newName);
                    dialog.dismiss();

                } else {
                    etName.setError("Enter name");
                }
            });

            dialog.show();
        });
        /// to change the name / edit profile info code ends  here////

        /// after clicking security popup show code starts here////

        security = findViewById(R.id.security);

        security.setOnClickListener(v -> {

            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.security_popup);
            dialog.show();

            backBtn = dialog.findViewById(R.id.btnBackPopup);

            backBtn.setOnClickListener(v1 -> {
                dialog.dismiss(); // close popup
            });

        });
        // close popup

        /// after clicking security popup show code ends here////


        /// after clicking help and security popup show code starts here////

        TextView contactus = findViewById(R.id.contactus);

        contactus.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.help_popup);
            dialog.show();

            ImageView backBtn = dialog.findViewById(R.id.btnBackhelpPopup);

            backBtn.setOnClickListener(v1 -> {
                dialog.dismiss(); // close popup
            });
        });


        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(student_profile_pg.this, HomePage_Student.class);
                startActivity(intent1);
            }
        });

        imgprof = findViewById(R.id.imgprof);

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(student_profile_pg.this, HomePage_Student.class);
                startActivity(intent);

            }
        });

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(student_profile_pg.this, HomePage_Student.class);
                startActivity(intent1);
            }
        });


        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(student_profile_pg.this, HomePage_Student.class);
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