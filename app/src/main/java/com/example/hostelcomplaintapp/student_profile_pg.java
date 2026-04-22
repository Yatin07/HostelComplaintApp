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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;
import android.app.Activity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class student_profile_pg extends AppCompatActivity {

    Switch switchTheme;
    ImageView btnBack, gotohomepg, imgprof, btnNotification, howtouseapp, backBtn;
    EditText etName;
    Button btnSave;
    TextView tvName;
    View EditProfileinformation, security;
    ImageView profileImage;

    static final int CAMERA_REQUEST = 100;
    private String base64ImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_profile_pg);

        TextView tvName = findViewById(R.id.tvName1);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvRoomno = findViewById(R.id.tvRoomno);

        SharedPreferences preffs = getSharedPreferences("user", MODE_PRIVATE);

        String name = preffs.getString("name", "Student");
        String email = preffs.getString("email", "student@gmail.com");
        String id = preffs.getString("id", "70012300049");

// ✅ SET DATA
        if (tvName != null) tvName.setText(name);
        if (tvEmail != null) tvEmail.setText(email);
        if (tvRoomno != null) tvRoomno.setText(id);

        profileImage = findViewById(R.id.profileImage);

        // Fetch Base64 Profile Image from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("IT_Students_data").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("profileImage")) {
                        String b64 = documentSnapshot.getString("profileImage");
                        if (b64 != null && !b64.isEmpty()) {
                            Bitmap bitmap = base64ToBitmap(b64);
                            if (bitmap != null) {
                                profileImage.setImageBitmap(bitmap);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                });

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


        EditProfileinformation = findViewById(R.id.EditProfileinformation);





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
                Intent intent = new Intent(student_profile_pg.this, student_profile_pg.class);
                startActivity(intent);

            }
        });

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(student_profile_pg.this, Notification_student.class);
                startActivity(intent1);
            }
        });


        howtouseapp = findViewById(R.id.howtouseapp);
        howtouseapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(student_profile_pg.this, Guide_pg_student.class);
                startActivity(intent1);
            }
        });


        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Clear any user preferences if necessary
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Redirect back to role selection and clear backstack
            Intent intent = new Intent(student_profile_pg.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ────────────────────────────────────────────────────────
    //  BASE64 DECODING
    // ────────────────────────────────────────────────────────
    private Bitmap base64ToBitmap(String b64) {
        if (b64 != null && b64.startsWith("data:image")) {
            b64 = b64.substring(b64.indexOf(",") + 1);
        }
        byte[] imageAsBytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}