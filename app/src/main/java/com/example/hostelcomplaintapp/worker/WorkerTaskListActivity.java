package com.example.hostelcomplaintapp.worker;

import android.os.Bundle;
<<<<<<< HEAD
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
=======
import android.view.View;
import android.widget.Button;

>>>>>>> main
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
=======
import com.example.hostelcomplaintapp.ComplaintAdapter;
>>>>>>> main
import com.example.hostelcomplaintapp.ComplaintModel;
import com.example.hostelcomplaintapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> main

public class WorkerTaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
<<<<<<< HEAD
    private Spinner spinnerFilter;
    private WorkerComplaintAdapter adapter;
    private List<ComplaintModel> allComplaints = new ArrayList<>();
=======
    private ComplaintAdapter adapter;
    private ArrayList<ComplaintModel> list;
>>>>>>> main

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_task_list);

        recyclerView = findViewById(R.id.recyclerViewTasks);
<<<<<<< HEAD
        spinnerFilter = findViewById(R.id.spinnerFilter);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Student Complaints");
        }
        
        android.widget.Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // Restore visibility and mapping logic gracefully
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new WorkerComplaintAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        setupFilter();
        WorkerNavigationHelper.setupNavigation(this);
        
        fetchComplaintsRealtime();
    }

    private void fetchComplaintsRealtime() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        db.collection("complaints").addSnapshotListener((value, error) -> {
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
                        model.setRoomNumber(document.getString("roomNumber"));
                        model.setStatus(document.getString("status"));
                        model.setCategory(document.getString("category"));
                        model.setStudentId(document.getString("studentId"));
                        
                        allComplaints.add(model);
                    } catch (Exception e) {
                        Log.e("WORKER_COMPLAINTS", "Doc parse fail", e);
                    }
                }
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
            int spinnerIndex = 0; // Default matches "ALL"
            if (passedFilter.equals("PENDING")) spinnerIndex = 1;
            else if (passedFilter.equals("IN_PROGRESS")) spinnerIndex = 2;
            else if (passedFilter.equals("COMPLETED")) spinnerIndex = 3;
            else if (passedFilter.equals("OVERDUE")) spinnerIndex = 4;
            
            spinnerFilter.setSelection(spinnerIndex);
        }
    }

    private void applyFilter() {
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
=======
        Button btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        // 🔥 SAME adapter + worker mode ON
        adapter = new ComplaintAdapter(list);
        adapter.setWorker(true); // 👈 IMPORTANT

        recyclerView.setAdapter(adapter);

        // 🔙 Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 🔥 Fetch data from Firebase
        fetchComplaints();
    }

    private void fetchComplaints() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("complaints")
                .whereEqualTo("status", "pending") // 👈 only pending complaints
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    list.clear();

                    for (QueryDocumentSnapshot doc : value) {

                        ComplaintModel model = doc.toObject(ComplaintModel.class);

                        // 🔥 VERY IMPORTANT (for resolve button)
                        model.setDocId(doc.getId());

                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}
>>>>>>> main
