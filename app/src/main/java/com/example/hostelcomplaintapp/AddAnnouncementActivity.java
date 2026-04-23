package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAnnouncementActivity extends AppCompatActivity {

    ImageView btnBack, imgProfile1, gotohomepg, staff_manage, btnNotification, imgProfile;
    TextView tv1WardenName;
    EditText addannouncement;
    Button submitBtn;
    RadioGroup radioGroupTarget;
    RadioButton radioAll, radioStaff, radioStudent;
    FirebaseFirestore db;
    private String targetRole = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_announcement);

        // 🔙 Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🔗 Connect views
        addannouncement = findViewById(R.id.addannouncement);
        submitBtn = findViewById(R.id.submitBtn);
        radioGroupTarget = findViewById(R.id.radioGroupTarget);
        radioAll = findViewById(R.id.radioAll);
        radioStaff = findViewById(R.id.radioStaff);
        radioStudent = findViewById(R.id.radioStudent);

        db = FirebaseFirestore.getInstance();

        // Radio group listener
        if (radioGroupTarget != null) {
            radioGroupTarget.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radioAll) {
                    targetRole = "all";
                } else if (checkedId == R.id.radioStaff) {
                    targetRole = "staff";
                } else if (checkedId == R.id.radioStudent) {
                    targetRole = "student";
                }
            });
        }

        // ✅ Submit button
        submitBtn.setOnClickListener(v -> {

            String text = addannouncement.getText().toString().trim();

            if (text.isEmpty()) {
                addannouncement.setError("Enter announcement");
                return;
            }

            saveAnnouncement(text);
        });

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddAnnouncementActivity.this, HomePage_Warden.class);
                startActivity(intent1);
            }
        });


        imgProfile1 = findViewById(R.id.imgProfile1);
        imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddAnnouncementActivity.this, imgProfile_click.class);
                startActivity(intent1);
            }
        });


        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddAnnouncementActivity.this, staff_manage.class);
                startActivity(intent1);
            }
        });


        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddAnnouncementActivity.this, Notification.class);
                startActivity(intent1);
            }
        });

        imgProfile1 = findViewById(R.id.imgProfile1);
        imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddAnnouncementActivity.this, imgProfile_click.class);
                startActivity(intent1);
            }
        });
    }


    // ✅ FIXED FUNCTION
    private void saveAnnouncement(String text) {

        Map<String, Object> data = new HashMap<>();

        // Get warden name from SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String wardenName = prefs.getString("name", "Warden");
        String wardenId = prefs.getString("id", "warden123");

        data.put("text", text);
        data.put("timestamp", System.currentTimeMillis());
        data.put("wardenId", wardenId);
        data.put("wardenName", wardenName);
        data.put("targetRole", targetRole);

        // Save to appropriate collection based on target role
        String collectionName;
        if (targetRole.equals("staff")) {
            collectionName = "staff_announcements";
        } else if (targetRole.equals("student")) {
            collectionName = "student_announcements";
        } else {
            collectionName = "announcements";
        }

        android.util.Log.d("AddAnnouncement", "Saving to collection: " + collectionName + " with targetRole: " + targetRole + " by warden: " + wardenName);

        db.collection(collectionName)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    android.util.Log.d("AddAnnouncement", "Successfully saved to " + collectionName + " with ID: " + documentReference.getId());
                    Toast.makeText(AddAnnouncementActivity.this, "Saved to " + collectionName + "!", Toast.LENGTH_SHORT).show();
                    addannouncement.setText("");
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("AddAnnouncement", "Error saving to " + collectionName, e);
                    Toast.makeText(AddAnnouncementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}