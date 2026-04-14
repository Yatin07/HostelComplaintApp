package com.example.hostelcomplaintapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePage_Warden extends AppCompatActivity {

    TextView tv1WardenName, tv2WardenId;
    ImageView imgProfile1, imgprof, staff_manage, imgnotification;
    LinearLayout cardAnnouncement, cardpending, cardtotalcomplaint;

    // for auto sliding of announcement card on home page //
    Handler handler = new Handler(Looper.getMainLooper());
    int currentPage = 0;

    View dot1, dot2, dot3;    /// FOR 3 DOTS OF AUTOSLIDING ANNOUNCEMENT CARD ON HOME PAGE


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page_warden);

       tv1WardenName = findViewById(R.id.tv1WardenName);
       tv2WardenId = findViewById(R.id.tv2WardenId);

        // temporary data
        String name = "Rahul Patil";
        String id = "W123";

        tv1WardenName.setText("Warden: " + name);
        tv2WardenId.setText("ID: " + id);


        imgProfile1 = findViewById(R.id.imgProfile1);

        imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Warden.this, imgProfile_click.class);
                startActivity(intent);

            }
        });



        //Announcement card clickable code starts here//
        cardAnnouncement = findViewById(R.id.cardAnnouncement);

        cardAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Warden.this, AddAnnouncementActivity.class);
                startActivity(intent);
            }
        });
        //Announcement card clickable code Ends here//


        //Pending card clickable code starts here//
        cardpending = findViewById(R.id.cardpending);

        cardpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Warden.this, pendingcomplaint.class);
                startActivity(intent);
            }
        });
        //Pending card clickable code Ends here//


        /// total complaint card code starts here ///
        cardtotalcomplaint = findViewById(R.id.cardtotalcomplaint);
        cardtotalcomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Warden.this, Totalcomplaint.class);
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
                Intent intent = new Intent(HomePage_Warden.this, imgProfile_click.class);
                startActivity(intent);

            }
        });


        staff_manage = findViewById(R.id.staff_manage);
        staff_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Warden.this, staff_manage.class);
                startActivity(intent);
            }
        });

        imgnotification = findViewById(R.id.imgnotification);
        imgnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage_Warden.this, Notification.class);
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