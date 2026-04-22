package com.example.hostelcomplaintapp.worker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelcomplaintapp.ComplaintModel;
import com.example.hostelcomplaintapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkerTaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner spinnerFilter;
    private WorkerComplaintAdapter adapter;
    private List<ComplaintModel> allComplaints = new ArrayList<>();
    private ListenerRegistration complaintsListener;
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_task_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewTasks);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Student Complaints");
        }

        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WorkerComplaintAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        setupFilter();
        WorkerNavigationHelper.setupNavigation(this);

        fetchComplaintsRealtime();
    }

    private void fetchComplaintsRealtime() {
        // Prevent multiple listeners from being registered
        if (complaintsListener != null) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        complaintsListener = db.collection("complaints").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("WORKER_COMPLAINTS", "Error active listener query", error);
                return;
            }

            if (value != null) {
                allComplaints.clear();
                for (QueryDocumentSnapshot document : value) {
                    try {
                        ComplaintModel model = new ComplaintModel();
                        model.setDocId(document.getId());
                        model.setTitle(document.getString("title"));
                        model.setDescription(document.getString("description"));

                        // ✅ Consistency: Handle both room and roomNumber fields
                        Object roomObj = document.get("room");
                        if (roomObj == null) roomObj = document.get("roomNumber");
                        String roomStr = roomObj != null ? String.valueOf(roomObj) : "N/A";
                        model.setRoomNumber(roomStr);
                        model.setRoom(roomStr);

                        model.setStatus(document.getString("status"));
                        model.setCategory(document.getString("category"));
                        model.setStudentId(document.getString("studentId"));
                        model.setImageUrl(document.getString("imageUrl")); 

                        allComplaints.add(model);
                    } catch (Exception e) {
                        Log.e("WORKER_COMPLAINTS", "Doc parse fail", e);
                    }
                }
                isDataLoaded = true;
                applyFilter();
            }
        });
    }

    private void setupFilter() {
        String[] filters = {"All", "Pending", "In Progress", "Completed", "Overdue"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(arrayAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Parse filter type passed dynamically via intent from WorkerDashboard Activity Cards
        String passedFilter = getIntent().getStringExtra("FILTER_TYPE");
        if (passedFilter != null) {
            int spinnerIndex = 0;
            if (passedFilter.equals("PENDING")) spinnerIndex = 1;
            else if (passedFilter.equals("IN_PROGRESS")) spinnerIndex = 2;
            else if (passedFilter.equals("COMPLETED")) spinnerIndex = 3;
            else if (passedFilter.equals("OVERDUE")) spinnerIndex = 4;

            spinnerFilter.setSelection(spinnerIndex);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove Firestore listener to prevent memory leaks and duplicate triggers
        if (complaintsListener != null) {
            complaintsListener.remove();
            complaintsListener = null;
        }
    }

    private void applyFilter() {
        // Do not apply filter until data has been loaded at least once
        if (!isDataLoaded) return;
        if (spinnerFilter == null || spinnerFilter.getSelectedItem() == null) return;
        String filterString = spinnerFilter.getSelectedItem().toString().toLowerCase();

        List<ComplaintModel> filteredList = new ArrayList<>();

        if (filterString.equals("all")) {
            filteredList.addAll(allComplaints);
        } else {
            for (ComplaintModel complaint : allComplaints) {
                String status = complaint.getStatus() != null ? complaint.getStatus().toLowerCase() : "pending";
                if (status.equals(filterString)) {
                    filteredList.add(complaint);
                }
            }
        }

        adapter.updateComplaints(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
