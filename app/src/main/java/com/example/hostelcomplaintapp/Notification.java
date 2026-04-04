package com.example.hostelcomplaintapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class Notification extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Notificationpgdatastore> list;
    NotificationAdapter adapter;
    FirebaseFirestore db;
    ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 🔗 Connect RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new NotificationAdapter(this, list);

        recyclerView.setAdapter(adapter);
        // 🔥 Firestore instance
        db = FirebaseFirestore.getInstance();

        // 📡 Fetch data
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {
                        list.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);

                            if (model != null) {
                                model.setId(doc.getId()); // ⭐ IMPORTANT
                                list.add(model);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}