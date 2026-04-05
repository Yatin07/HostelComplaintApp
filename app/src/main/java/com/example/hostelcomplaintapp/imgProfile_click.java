package com.example.hostelcomplaintapp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class imgProfile_click extends AppCompatActivity {
    Switch switchTheme;
    ImageView btnBack;
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

        /// after clicking help and security popup show code starts here////

        TextView contactus = findViewById(R.id.contactus);

        contactus.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.help_popup);
            dialog.show();
        });

        /// after clicking help and security popup show code starts here////





    }
}
