package com.example.hostelcomplaintapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Totalcomplaint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_totalcomplaint);

/// make card design start  ///
        RecyclerView recyclerView;
        ArrayList<ComplaintModel> list;
        ComplaintAdapter adapter;

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        adapter = new ComplaintAdapter(list);

        recyclerView.setAdapter(adapter);
/// make card design end  ///

        /// Load Data from Firestore///
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("complaints")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ComplaintModel model = doc.toObject(ComplaintModel.class);
                        list.add(model);
                    }

                    adapter.notifyDataSetChanged(); // VERY IMPORTANT
                });
        /// load data  from firestore end ///

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}