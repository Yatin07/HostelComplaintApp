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
    ImageView imagePreview, btnBack, gotohomepg, btnNotification, howtouseapp, imgprof;
    private String base64ImageUrl = "";

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
        btnBack = findViewById(R.id.btnBack);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ✅ BOTTOM NAV NAVIGATION
        gotohomepg = findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(v -> finish());

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(v -> {
            startActivity(new Intent(RaiseComplaintActivity.this, Notification_student.class));
        });

        howtouseapp = findViewById(R.id.howtouseapp);
        howtouseapp.setOnClickListener(v -> {
            startActivity(new Intent(RaiseComplaintActivity.this, Guide_pg_student.class));
        });

        imgprof = findViewById(R.id.imgprof);
        imgprof.setOnClickListener(v -> {
            startActivity(new Intent(RaiseComplaintActivity.this, student_profile_pg.class));
        });










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
            if (title.isEmpty() || description.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields (Title, Room, Description)", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 FIRESTORE CODE
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("description", description);
            data.put("room", room);
            data.put("category", category);
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            String studentId = prefs.getString("id", "Unknown");
            data.put("studentId", studentId);
            data.put("timestamp", FieldValue.serverTimestamp());
            data.put("status", "Pending");
            data.put("imageUrl", base64ImageUrl);

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
                        base64ImageUrl = "";

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
                Bitmap raw = (Bitmap) data.getExtras().get("data");
                if (raw != null) {
                    Bitmap compressed = compressBitmap(raw);
                    base64ImageUrl = bitmapToBase64DataUrl(compressed);
                    imagePreview.setImageBitmap(compressed);
                }
            }
        }
    }

    // ────────────────────────────────────────────────────────
    //  IMAGE COMPRESSION & BASE64 ENCODING
    // ────────────────────────────────────────────────────────
    private Bitmap compressBitmap(Bitmap original) {
        int maxDim = 1024;
        int w = original.getWidth();
        int h = original.getHeight();

        if (w > maxDim || h > maxDim) {
            float ratio = Math.min((float) maxDim / w, (float) maxDim / h);
            w = Math.round(w * ratio);
            h = Math.round(h * ratio);
            original = Bitmap.createScaledBitmap(original, w, h, true);
        }

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] compressedBytes = baos.toByteArray();
        return android.graphics.BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);
    }

    private String bitmapToBase64DataUrl(Bitmap bitmap) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] bytes = baos.toByteArray();
        return "data:image/jpeg;base64," + android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }
}