package com.example.hostelcomplaintapp;

import android.app.Dialog;
import android.content.Intent;
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

        TextView tvName = findViewById(R.id.tvName1);
        TextView EditProfileinformation = findViewById(R.id.EditProfileinformation);

        EditProfileinformation.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.center_popup_editprofileinfo);

            EditText etName = dialog.findViewById(R.id.etName);
            Button btnSave = dialog.findViewById(R.id.btnSave);

            btnSave.setOnClickListener(view -> {
                String newName = etName.getText().toString().trim();

                if (!newName.isEmpty()) {
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

        TextView security = findViewById(R.id.security);

        security.setOnClickListener(v -> {

            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.security_popup);
            dialog.show();

            // 👇 ADD THIS PART
            ImageView backBtn = dialog.findViewById(R.id.btnBackPopup);

            backBtn.setOnClickListener(v1 -> {
                dialog.dismiss(); // close popup
            });

        });
       // close popup

        /// after clicking  security popup show code ends here////

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

        /// battery level,wifi,time visible ///
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });





    }
}
