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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class staff_manage extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout staffContainer;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_manage);

        btnBack = findViewById(R.id.btnBack);
        staffContainer = findViewById(R.id.staffContainer);

        btnBack.setOnClickListener(v -> finish());

        // 🔥 Initialize Firebase ONCE
        databaseRef = FirebaseDatabase.getInstance().getReference("Staff");

        // 🔥 LOAD DATA FROM FIREBASE
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                staffContainer.removeAllViews();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String key = data.getKey();
                    String name = data.child("name").getValue(String.class);
                    String id = data.child("id").getValue(String.class);
                    String dept = data.child("dept").getValue(String.class);
                    String email = data.child("email").getValue(String.class);
                    String phone = data.child("phone").getValue(String.class);

                    View cardView = getLayoutInflater().inflate(R.layout.newstaff_card, staffContainer, false);

                    TextView nameTv = cardView.findViewById(R.id.tvName);
                    TextView idTv = cardView.findViewById(R.id.tvId);
                    TextView deptTv = cardView.findViewById(R.id.tvDept);
                    ImageView arrowBtn = cardView.findViewById(R.id.arrowBtn);
                    Button removeBtn = cardView.findViewById(R.id.removeBtn);

                    nameTv.setText(name);
                    idTv.setText("Staff ID: " + id);
                    deptTv.setText("Department: " + dept);

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
                        databaseRef.child(key).removeValue();
                    });

                    staffContainer.addView(cardView);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
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

                        String staffKey = databaseRef.push().getKey();

                        databaseRef.child(staffKey).child("name").setValue(nameStr);
                        databaseRef.child(staffKey).child("id").setValue(idStr);
                        databaseRef.child(staffKey).child("dept").setValue(deptStr);
                        databaseRef.child(staffKey).child("email").setValue(emailStr);
                        databaseRef.child(staffKey).child("phone").setValue(phoneStr);

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