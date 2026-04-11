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

public class RaiseComplaintActivity extends AppCompatActivity {

    Spinner spinnerCategory;
    EditText etRoom, etTitle, etDescription;
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
                categories
        );
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

            // Save to Database
            java.util.Map<String, Object> complaint = new java.util.HashMap<>();
            complaint.put("text", category + ": " + title + " - " + description);
            complaint.put("room", room);
            complaint.put("timestamp", System.currentTimeMillis());
            complaint.put("status", "Pending");
            complaint.put("bedNumber", "N/A"); // Default fallback
            complaint.put("studentId", "Student-01"); // Dummy student user
            complaint.put("assignedWorkerId", "W-12345"); // Auto-assign to default worker for testing staff UI

            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
            db.collection("complaints").add(complaint)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show();
                        // Clear form
                        etTitle.setText("");
                        etDescription.setText("");
                        etRoom.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Submission failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Image Button Logic
        btnUploadImage.setOnClickListener(v -> {
            String[] options = {"Camera", "Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Image")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } else {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                            galleryIntent.setType("image/*");
                            startActivityForResult(galleryIntent, GALLERY_REQUEST);
                        }
                    }).show();
        });

        // Location Logic
        btnLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Location location = new Location("dummy");
                location.setLatitude(19.9975);
                location.setLongitude(75.7764);

                Toast.makeText(this, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
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