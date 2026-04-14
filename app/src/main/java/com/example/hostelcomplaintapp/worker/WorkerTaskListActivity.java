package com.example.hostelcomplaintapp.worker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelcomplaintapp.ComplaintAdapter;
import com.example.hostelcomplaintapp.ComplaintModel;
import com.example.hostelcomplaintapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class WorkerTaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComplaintAdapter adapter;
    private ArrayList<ComplaintModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_task_list);

        recyclerView = findViewById(R.id.recyclerViewTasks);
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