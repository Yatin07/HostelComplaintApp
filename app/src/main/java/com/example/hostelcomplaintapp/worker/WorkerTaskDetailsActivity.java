package com.example.hostelcomplaintapp.worker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.models.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class WorkerTaskDetailsActivity extends AppCompatActivity {

    private TextView tvDetailTitle, tvDetailRoom, tvDetailStudent, tvDetailDesc, tvDetailStatus, tvProofDesc;
    private Button btnAcceptTask, btnCompleteTask;
    private ImageView ivProofImage;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_task_details);

        tvDetailTitle = findViewById(R.id.tvDetailTitle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Task Details");
        }
        tvDetailRoom = findViewById(R.id.tvDetailRoom);
        tvDetailStudent = findViewById(R.id.tvDetailStudent);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvProofDesc = findViewById(R.id.tvProofDesc);

        btnAcceptTask = findViewById(R.id.btnAcceptTask);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        ivProofImage = findViewById(R.id.ivProofImage);

        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        String taskId = getIntent().getStringExtra("TASK_ID");
        if (taskId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("complaints").document(taskId)
              .addSnapshotListener((doc, e) -> {
                  if (e != null || doc == null || !doc.exists()) return;
                  currentTask = new Task();
                  currentTask.setTaskId(doc.getId());
                  currentTask.setRoomNumber(doc.getString("roomNumber"));
                  currentTask.setStudentName(doc.getString("studentId"));
                  currentTask.setDescription(doc.getString("description"));
                  currentTask.setTitle(doc.getString("title") != null ? doc.getString("title") : "Complaint: Room " + doc.getString("roomNumber"));

                  String status = doc.getString("status");
                  currentTask.setStatus(status != null ? status : "Pending");

                  populateData();
              });
        }

        btnAcceptTask.setOnClickListener(v -> {
            if (currentTask != null) {
                FirebaseFirestore.getInstance().collection("complaints")
                    .document(currentTask.getTaskId())
                    .update("status", "In Progress");
                Toast.makeText(this, "Task Accepted", Toast.LENGTH_SHORT).show();
            }
        });

        btnCompleteTask.setOnClickListener(v -> {
            showProofUploadDialog();
        });
    }

    private void populateData() {
        tvDetailTitle.setText(currentTask.getTitle());
        tvDetailRoom.setText("Room: " + currentTask.getRoomNumber());
        tvDetailStudent.setText("Student: " + currentTask.getStudentName());
        tvDetailDesc.setText("Description:\n" + currentTask.getDescription());
        tvDetailStatus.setText("Status: " + currentTask.getStatus());

        btnAcceptTask.setVisibility(View.GONE);
        btnCompleteTask.setVisibility(View.GONE);

        String status = currentTask.getStatus();
        if (status.equalsIgnoreCase("Pending")) {
            btnAcceptTask.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("In Progress") || status.equalsIgnoreCase("Overdue")) {
            btnCompleteTask.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("Completed")) {
            ivProofImage.setVisibility(View.VISIBLE);
            tvProofDesc.setVisibility(View.VISIBLE);
            tvProofDesc.setText("Proof Description: Fan repaired and tested.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProofUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Proof of Work");
        builder.setMessage("You must provide proof before completing this task.");

        final EditText input = new EditText(this);
        input.setHint("Enter description of work done");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String desc = input.getText().toString().trim();
            if (desc.isEmpty()) {
                Toast.makeText(this, "Proof description is required!", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore.getInstance().collection("complaints")
                    .document(currentTask.getTaskId())
                    .update("status", "Completed");
                Toast.makeText(this, "Task Completed Successfully!", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
