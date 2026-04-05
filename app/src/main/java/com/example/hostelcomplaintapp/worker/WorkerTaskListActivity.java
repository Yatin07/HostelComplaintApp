package com.example.hostelcomplaintapp.worker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelcomplaintapp.R;
import com.example.hostelcomplaintapp.adapters.TaskAdapter;
import com.example.hostelcomplaintapp.models.DataRepository;
import com.example.hostelcomplaintapp.models.Task;

import java.util.ArrayList;
import java.util.List;

public class WorkerTaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Spinner spinnerFilter;
    private List<Task> allTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_task_list);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Assigned Tasks");
        }
        spinnerFilter = findViewById(R.id.spinnerFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        android.widget.Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        allTasks = DataRepository.getInstance().getTasks();
        taskAdapter = new TaskAdapter(this, allTasks);
        recyclerView.setAdapter(taskAdapter);

        setupFilter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        allTasks = DataRepository.getInstance().getTasks();
        filterTasks(spinnerFilter.getSelectedItem().toString());
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFilter() {
        String[] filters = {"All", "Pending", "In Progress", "Completed", "Overdue"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTasks(filters[position]);
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

    private void filterTasks(String status) {
        if (status.equals("All")) {
            taskAdapter.updateTasks(allTasks);
        } else {
            List<Task> filtered = new ArrayList<>();
            for (Task task : allTasks) {
                if (task.getStatus().equalsIgnoreCase(status)) {
                    filtered.add(task);
                }
            }
            taskAdapter.updateTasks(filtered);
        }
    }
}
