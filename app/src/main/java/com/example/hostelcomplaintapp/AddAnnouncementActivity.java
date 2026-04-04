package com.example.hostelcomplaintapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAnnouncementActivity extends AppCompatActivity {

    ImageView btnBack, imgProfile;
    TextView tv1WardenName;
    EditText addannouncement;
    Button submitBtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_announcement);

        // 🔙 Back button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🔗 Connect views
        addannouncement = findViewById(R.id.addannouncement); // ✅ correct ID
        submitBtn = findViewById(R.id.submitBtn);

        db = FirebaseFirestore.getInstance();

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
    }

    // ✅ FIXED FUNCTION
    private void saveAnnouncement(String text) {

        Map<String, Object> data = new HashMap<>(); // ✅ CREATE FIRST

        data.put("text", text);
        data.put("timestamp", System.currentTimeMillis());
        data.put("wardenId", "warden123"); // ✅ simple value

        db.collection("announcements")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddAnnouncementActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    addannouncement.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAnnouncementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}