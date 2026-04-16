package com.example.hostelcomplaintapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class HomePage_Student extends AppCompatActivity {
    TextView tv1StudentName, tv2StudentId;
    ImageView imgProfile1, imgprof, howtouseapp, imgnotification;
    LinearLayout cardraisecomplaint, cardpending, cardtotalcomplaint,cardemergencyissue;

    // for auto sliding of announcement card on home page //
    Handler handler = new Handler(Looper.getMainLooper());
    int currentPage = 0;

    View dot1, dot2, dot3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_student);


        TextView tvName = findViewById(R.id.tv1StudentName);
        TextView tvId = findViewById(R.id.tv2StudentId);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String name = prefs.getString("name", "Student");
        String id = prefs.getString("id", "ID");

// ✅ IMPORTANT: null safety
        if (tvName != null) tvName.setText(name);
        if (tvId != null) tvId.setText(id);



        cardtotalcomplaint = findViewById(R.id.cardtotalcomplaint);
        cardpending = findViewById(R.id.cardpending);
        cardemergencyissue = findViewById(R.id.cardemergencyissue);
        cardraisecomplaint = findViewById(R.id.cardraisecomplaint);

        tv1StudentName = findViewById(R.id.tv1StudentName);
        tv2StudentId = findViewById(R.id.tv2StudentId);

        imgProfile1 = findViewById(R.id.imgProfile1);

        imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, student_profile_pg.class);
                startActivity(intent);
            }
        });


        //Announcement card clickable code starts here//
        cardraisecomplaint = findViewById(R.id.cardraisecomplaint);

        cardraisecomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, RaiseComplaintActivity.class);
                startActivity(intent);
            }
        });
        //Announcement card clickable code Ends here//

        //Pending card clickable code starts here//
        cardpending = findViewById(R.id.cardpending);

        cardpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, pendingcomplaint.class);
                startActivity(intent);
            }
        });
        //Pending card clickable code Ends here//


        /// total complaint card code starts here ///
        cardtotalcomplaint = findViewById(R.id.cardtotalcomplaint);
        cardtotalcomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, Totalcomplaint_student.class);
                startActivity(intent);
            }
        });


        // 3 DOTS BELOW THE AUTOSLIDING CARD ON HOMEPAGE
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);


        ViewPager2 viewPager = findViewById(R.id.viewPagerAnnouncements);

        /// announcement autosliding fetches only latest 3 from firestore code start ///
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> announcements = new ArrayList<>();

        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    announcements.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        String text = doc.getString("text");
                        announcements.add(text);
                    }

                    AnnouncementAdapter adapter = new AnnouncementAdapter(announcements);
                    viewPager.setAdapter(adapter);

                    currentPage = 0;
                });
        /// announcement autosliding fetches only latest 3 from firestore code end ///



        ///  For AUTO-SLIDING OF THE ANNOUNCEMENT CARD ///
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == announcements.size()) {
                    currentPage = 0;
                }

                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(runnable, 10000);

        cardemergencyissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), emergency_issue_student.class);
                intent.putExtra("role", "student");
                startActivity(intent);
            }
        });

        /// 3 DOTS BELOW THE AUTO SLIDING ANNOUNCEMENT CARD
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // reset all dots
                dot1.setBackgroundColor(Color.GRAY);
                dot2.setBackgroundColor(Color.GRAY);
                dot3.setBackgroundColor(Color.GRAY);

                // highlight active dot
                if (position == 0) {
                    dot1.setBackgroundColor(Color.RED);
                } else if (position == 1) {
                    dot2.setBackgroundColor(Color.RED);
                } else if (position == 2) {
                    dot3.setBackgroundColor(Color.RED);
                }
            }
        });


        /// bottom navbar profile icon click to go profile page ///
        imgprof = findViewById(R.id.imgprof);

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, student_profile_pg.class);
                startActivity(intent);
            }
        });


        howtouseapp = findViewById(R.id.howtouseapp);
        howtouseapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, Guide_pg_student.class);
                startActivity(intent);
            }
        });

        imgnotification = findViewById(R.id.imgnotification);
        imgnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Student.this, Notification_student.class);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    ///  For AUTO-SLIDING OF THE ANNOUNCEMENT CARD ///
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
