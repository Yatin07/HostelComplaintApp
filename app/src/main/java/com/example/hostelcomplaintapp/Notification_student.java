package com.example.hostelcomplaintapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Notification_student extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Notificationpgdatastore> list;

    NotificationAdapter_student adapter;
    FirebaseFirestore db;
    ImageView btnBack, gotohomepg, imgprof, btnNotification, howtouseapp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_student);


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
        adapter = new NotificationAdapter_student(this, list);
        recyclerView.setAdapter(adapter);
        // 🔥 Firestore instance
        db = FirebaseFirestore.getInstance();

        android.util.Log.d("StudentNotification", "Fetching from student_announcements and announcements collections");

        // 📡 Fetch data from student_announcements
        db.collection("student_announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        android.util.Log.e("StudentNotification", "Firestore error (student_announcements)", error);
                        return;
                    }

                    if (value != null) {
                        list.clear();
                        android.util.Log.d("StudentNotification", "Student documents fetched: " + value.size());

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);

                            if (model != null) {
                                model.setId(doc.getId()); // ⭐ IMPORTANT
                                list.add(model);
                            }
                        }

                        // Also fetch from announcements (for "All" target)
                        db.collection("announcements")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .get()
                                .addOnSuccessListener(allValue -> {
                                    android.util.Log.d("StudentNotification", "All announcements fetched: " + allValue.size());
                                    for (DocumentSnapshot doc : allValue.getDocuments()) {
                                        Notificationpgdatastore model = doc.toObject(Notificationpgdatastore.class);
                                        if (model != null) {
                                            model.setId(doc.getId());
                                            list.add(model);
                                        }
                                    }
                                    // Sort by timestamp
                                    list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    android.util.Log.e("StudentNotification", "Error fetching announcements", e);
                                    adapter.notifyDataSetChanged();
                                });
                    }
                });

        /// bottom navbar profile icon click to go profile page ///
        imgprof = findViewById(R.id.imgprof);

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification_student.this, student_profile_pg.class);
                startActivity(intent);

            }
        });


        howtouseapp = findViewById(R.id.howtouseapp);
        howtouseapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification_student.this, Guide_pg_student.class);
                startActivity(intent);
            }
        });

        btnNotification = findViewById(R.id.btnNotification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification_student.this, Notification_student.class);
                startActivity(intent);
            }
        });

        gotohomepg=findViewById(R.id.gotohomepg);
        gotohomepg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Notification_student.this, HomePage_Student.class);
                startActivity(intent1);
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ⭐ Inner Class Adapter to prevent 'symbol not found' issues
    private static class NotificationAdapter_student extends RecyclerView.Adapter<NotificationAdapter_student.ViewHolder> {

        Context context;
        ArrayList<Notificationpgdatastore> list;

        public NotificationAdapter_student(Context context, ArrayList<Notificationpgdatastore> list) {
            this.context = context;
            this.list = list;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtText, txtWarden, txtTime;
            ImageView arrowBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                txtText = itemView.findViewById(R.id.txtText);
                txtWarden = itemView.findViewById(R.id.txtWarden);
                txtTime = itemView.findViewById(R.id.txtTime);
                arrowBtn = itemView.findViewById(R.id.arrowBtn);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.notificationpg_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Notificationpgdatastore model = list.get(position);
            holder.txtText.setText(model.getText() != null ? model.getText() : "No message");
            holder.txtWarden.setText("Posted by: " + (model.getWardenName() != null ? model.getWardenName() : "Warden"));

            String formattedTime = "No time";
            if (model.getTimestamp() != 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
                formattedTime = sdf.format(new Date(model.getTimestamp()));
            }
            final String displayTime = formattedTime;
            holder.txtTime.setText(displayTime);

            holder.arrowBtn.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Notification Details");
                builder.setMessage("Message: " + model.getText() + "\n\n" +
                                   "Posted by: " + (model.getWardenName() != null ? model.getWardenName() : "Warden") + "\n\n" +
                                   "Time: " + displayTime);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.show();
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}