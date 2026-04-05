package com.example.hostelcomplaintapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class staff_manage extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout staffContainer;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_manage);

        btnBack = findViewById(R.id.btnBack);
        staffContainer = findViewById(R.id.staffContainer);

        btnBack.setOnClickListener(v -> finish());

        // 🔥 Initialize Firestore ONCE
        db = FirebaseFirestore.getInstance();

        // 🔥 LOAD DATA FROM FIRESTORE
        db.collection("Staff").addSnapshotListener((snapshot, error) -> {
            if (error != null || snapshot == null) {
                return;
            }

            staffContainer.removeAllViews();

            for (DocumentSnapshot data : snapshot.getDocuments()) {

                String key = data.getId();
                String name = data.getString("name");
                String id = data.getString("id");
                String dept = data.getString("dept");
                String email = data.getString("email");
                String phone = data.getString("phone");

                View cardView = getLayoutInflater().inflate(R.layout.newstaff_card, staffContainer, false);

                TextView nameTv = cardView.findViewById(R.id.tvName);
                TextView idTv = cardView.findViewById(R.id.tvId);
                TextView deptTv = cardView.findViewById(R.id.tvDept);
                ImageView arrowBtn = cardView.findViewById(R.id.arrowBtn);
                Button removeBtn = cardView.findViewById(R.id.removeBtn);

                nameTv.setText(name != null ? name : "");
                idTv.setText("Staff ID: " + (id != null ? id : ""));
                deptTv.setText("Department: " + (dept != null ? dept : ""));

                // 🔹 DETAILS POPUP
                arrowBtn.setOnClickListener(v -> {
                    new AlertDialog.Builder(staff_manage.this)
                            .setTitle("Staff Details")
                            .setMessage(
                                    "Name: " + name + "\n\n" +
                                            "Staff ID: " + id + "\n\n" +
                                            "Department: " + dept + "\n\n" +
                                            "Email: " + email + "\n\n" +
                                            "Phone: " + phone
                            )
                            .show();
                });

                // 🔥 REMOVE BUTTON
                removeBtn.setOnClickListener(v -> {
                    db.collection("Staff").document(key).delete();
                });

                staffContainer.addView(cardView);
            }
        });

        // 🔹 ADD STAFF BUTTON
        ImageView addBtn = findViewById(R.id.addStaffBtn);

        addBtn.setOnClickListener(v -> {

            View view = getLayoutInflater().inflate(R.layout.staff_info_popup, null);

            EditText name = view.findViewById(R.id.etName);
            EditText id = view.findViewById(R.id.etId);
            EditText dept = view.findViewById(R.id.etDept);
            EditText email = view.findViewById(R.id.etEmail);
            EditText phone = view.findViewById(R.id.etPhone);

            new AlertDialog.Builder(this)
                    .setTitle("Add Staff")
                    .setView(view)
                    .setPositiveButton("Save", (dialog, which) -> {

                        String nameStr = name.getText().toString();
                        String idStr = id.getText().toString();
                        String deptStr = dept.getText().toString();
                        String emailStr = email.getText().toString();
                        String phoneStr = phone.getText().toString();

                        Map<String, Object> staffData = new HashMap<>();
                        staffData.put("name", nameStr);
                        staffData.put("id", idStr);
                        staffData.put("dept", deptStr);
                        staffData.put("email", emailStr);
                        staffData.put("phone", phoneStr);

                        db.collection("Staff").add(staffData);

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}