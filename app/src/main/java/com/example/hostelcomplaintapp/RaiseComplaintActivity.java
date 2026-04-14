package com.example.hostelcomplaintapp;

import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RaiseComplaintActivity extends AppCompatActivity {

    Spinner spinnerCategory;
    EditText etRoom, etTitle, etDescription, etStudentId;
    Button btnSubmit, btnUploadImage;
    ImageView imagePreview;

    static final int CAMERA_REQUEST = 100;
    static final int GALLERY_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_complaint);

        // Bind Views
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etRoom = findViewById(R.id.etRoom);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imagePreview = findViewById(R.id.imagePreview);
        Button btnLocation = findViewById(R.id.btnLocation);

        // Spinner Data
        String[] categories = {
                "Electrical", "Plumbing", "Cleaning",
                "Wi-Fi", "Furniture", "Mess", "Others"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories);
        spinnerCategory.setAdapter(adapter);

        // Submit Button Logic
        btnSubmit.setOnClickListener(v -> {

            String category = spinnerCategory.getSelectedItem().toString();
            String room = etRoom.getText().toString();
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();

            // Validation
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 FIRESTORE CODE
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", description);
            data.put("room", room);
            data.put("category", category);
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String studentId = prefs.getString("sapid", "Unknown");
            data.put("studentId", studentId);
            data.put("timestamp", FieldValue.serverTimestamp());
            data.put("status", "Pending");

            db.collection("complaints")
                    .add(data)
                    .addOnSuccessListener(doc -> {

                        String docId = doc.getId();

                        // save docId inside document
                        db.collection("complaints")
                                .document(docId)
                                .update("docId", docId);

                        Toast.makeText(this, "Complaint Submitted ", Toast.LENGTH_LONG).show();

                        // Clear form
                        etTitle.setText("");
                        etDescription.setText("");
                        etRoom.setText("");
                        imagePreview.setImageDrawable(null);

                        // Redirect to home page
                        Intent intent = new Intent(RaiseComplaintActivity.this, HomePage_Student.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error ❌: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // Image Button Logic (Camera Only)
        btnUploadImage.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        // Location Logic
        btnLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            } else {
                Location location = new Location("dummy");
                location.setLatitude(19.9975);
                location.setLongitude(75.7764);

                Toast.makeText(this, "Location: " + location.getLatitude() + ", " + location.getLongitude(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagePreview.setImageBitmap(photo);
            } else if (requestCode == GALLERY_REQUEST && data.getData() != null) {
                imagePreview.setImageURI(data.getData());
            }
        }
    }
}