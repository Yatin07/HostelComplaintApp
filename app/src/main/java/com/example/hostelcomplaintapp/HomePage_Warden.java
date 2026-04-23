package com.example.hostelcomplaintapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomePage_Warden extends AppCompatActivity {

    private static final String TAG = "HomePage_Warden_Log";
    private View imgProfile1, imgprof, staff_manage, imgnotification, home;
    private View cardAnnouncement, cardpending, cardtotalcomplaint, cardemergencyissue;
    private View dot1, dot2, dot3;
    private ViewPager2 viewPager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    private List<String> announcements = new ArrayList<>();
    private Runnable slidingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");

        try {
            setContentView(R.layout.activity_home_page_warden);
            Log.d(TAG, "setContentView success");

            // 1. Session Data
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            String name = prefs.getString("name", "Warden");
            String id = prefs.getString("id", "");

            TextView tvName = findViewById(R.id.tv1WardenName);
            TextView tvId = findViewById(R.id.tv2WardenId);
            if (tvName != null) tvName.setText(name);
            if (tvId != null) tvId.setText("ID: " + id);

            // 2. Initialize Views
            imgProfile1 = findViewById(R.id.imgProfile1);
            cardAnnouncement = findViewById(R.id.cardAnnouncement);
            cardpending = findViewById(R.id.cardpending);
            cardtotalcomplaint = findViewById(R.id.cardtotalcomplaint);
            cardemergencyissue = findViewById(R.id.cardemergencyissue);
            dot1 = findViewById(R.id.dot1);
            dot2 = findViewById(R.id.dot2);
            dot3 = findViewById(R.id.dot3);
            viewPager = findViewById(R.id.viewPagerAnnouncements);
            imgprof = findViewById(R.id.imgprof);
            staff_manage = findViewById(R.id.staff_manage);
            imgnotification = findViewById(R.id.imgnotification);
            home = findViewById(R.id.home);

            // 3. Click Listeners
            if (imgProfile1 != null) imgProfile1.setOnClickListener(v -> startActivity(new Intent(this, imgProfile_click.class)));
            if (cardAnnouncement != null) cardAnnouncement.setOnClickListener(v -> startActivity(new Intent(this, AddAnnouncementActivity.class)));
            if (cardpending != null) cardpending.setOnClickListener(v -> startActivity(new Intent(this, pendingcomplaint.class)));
            if (cardtotalcomplaint != null) cardtotalcomplaint.setOnClickListener(v -> startActivity(new Intent(this, Totalcomplaint.class)));
            if (cardemergencyissue != null) cardemergencyissue.setOnClickListener(v -> {
                Intent intent = new Intent(this, emergency_issue_student.class);
                intent.putExtra("role", "warden");
                startActivity(intent);
            });
            // Bottom Navigation - use LinearLayout parents for better touch response
            LinearLayout llHome = findViewById(R.id.llHome);
            LinearLayout llNotification = findViewById(R.id.llNotification);
            LinearLayout llStaff = findViewById(R.id.llStaff);
            LinearLayout llProfile = findViewById(R.id.llProfile);

            if (llHome != null) llHome.setOnClickListener(v -> Log.d(TAG, "Already on Home"));
            if (llNotification != null) llNotification.setOnClickListener(v -> startActivity(new Intent(this, Notification.class)));
            if (llStaff != null) llStaff.setOnClickListener(v -> startActivity(new Intent(this, staff_manage.class)));
            if (llProfile != null) llProfile.setOnClickListener(v -> startActivity(new Intent(this, imgProfile_click.class)));

            // 4. ViewPager & Announcements
            if (viewPager != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("announcements")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(3)
                        .addSnapshotListener((value, error) -> {
                            if (error != null || value == null) {
                                Log.e(TAG, "Firestore error", error);
                                return;
                            }
                            announcements.clear();
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                String text = doc.getString("text");
                                if (text != null) announcements.add(text);
                            }
                            if (!announcements.isEmpty()) {
                                AnnouncementAdapter adapter = new AnnouncementAdapter(announcements);
                                viewPager.setAdapter(adapter);
                            }
                        });

                slidingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!announcements.isEmpty()) {
                            if (currentPage >= announcements.size()) currentPage = 0;
                            viewPager.setCurrentItem(currentPage++, true);
                        }
                        handler.postDelayed(this, 5000);
                    }
                };
                handler.postDelayed(slidingRunnable, 5000);

                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updateDots(position);
                    }
                });
            }

            // 5. Window Insets
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Crash in HomePage_Warden onCreate", e);
            Toast.makeText(this, "Warden Dashboard Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateDots(int position) {
        if (dot1 != null) dot1.setBackgroundColor(position == 0 ? Color.RED : Color.GRAY);
        if (dot2 != null) dot2.setBackgroundColor(position == 1 ? Color.RED : Color.GRAY);
        if (dot3 != null) dot3.setBackgroundColor(position == 2 ? Color.RED : Color.GRAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && slidingRunnable != null) {
            handler.removeCallbacks(slidingRunnable);
        }
    }
}