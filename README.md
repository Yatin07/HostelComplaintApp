<div align="center">

# 🏠 Hostel Complaint Management System

**A role-based Android application for streamlined hostel infrastructure management**

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Firestore](https://img.shields.io/badge/Database-Firestore-FF6F00?style=for-the-badge&logo=firebase&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2024%20(Android%207.0)-blue?style=for-the-badge)

</div>

---

## 📖 About

The **Hostel Complaint Management System** is a native Android application that digitizes the end-to-end complaint lifecycle inside a college hostel — from a student filing a complaint with photo evidence and GPS location, to a staff worker resolving it, to a warden monitoring everything in real time.

Three distinct portals — **Student**, **Warden**, and **Staff/Worker** — each with scoped data access, dedicated dashboards, and role-specific features backed by Firebase Firestore with live snapshot listeners.

---

## ✨ Features at a Glance

| Feature | Student | Warden | Staff/Worker |
|---|:---:|:---:|:---:|
| File complaint (camera + GPS) | ✅ | — | — |
| View own complaint status | ✅ | — | — |
| View all complaints (real-time) | — | ✅ | ✅ |
| Mark complaint as Completed | — | — | ✅ |
| Live metrics dashboard | — | — | ✅ |
| Post targeted announcements | — | ✅ | — |
| Manage staff accounts | — | ✅ | — |
| Edit emergency contacts | — | ✅ | — |
| View emergency contacts | ✅ | ✅ | — |
| Role-scoped notifications | ✅ | ✅ | ✅ |
| Profile photo (from Firestore) | ✅ | — | — |
| Dark mode toggle | ✅ | — | — |

---

## 🏗️ Architecture Overview

```
Landing Screen (MainActivity)
│
├── Session check (SharedPreferences)
│       └── Returning users → direct route to role dashboard
│
├── Student  ──→ HomePage_Student
│       ├── Raise Complaint  (camera · GPS · Firestore write)
│       ├── Pending Complaints  (real-time listener)
│       ├── Total Complaints    (real-time listener)
│       ├── Emergency Contacts  (read-only)
│       ├── Notifications       (student_announcements + announcements)
│       └── Profile             (Firestore Base64 photo · dark mode · logout)
│
├── Warden  ──→ HomePage_Warden
│       ├── Add Announcement  (role-targeted: All / Staff / Students)
│       ├── Pending Complaints  (real-time listener)
│       ├── Total Complaints    (real-time listener)
│       ├── Emergency Contacts  (editable)
│       ├── Staff Management    (CRUD on workers collection)
│       └── Notifications       (all 3 collections · merged + sorted)
│
└── Staff/Worker  ──→ WorkerDashboardActivity
        ├── Live metrics panel  (Total · Pending · In Progress · Completed · Overdue)
        ├── Task list           (filterable by status · real-time)
        ├── Complaint detail + Resolve action
        └── Notifications       (staff_announcements + announcements)
```

---

## 🗄️ Firestore Database Schema

```
Firestore Root
│
├── IT_Students_data/           # Student records
│       └── {sapId}
│               ├── name, email, password, rollNo, sapId
│               └── profileImage  (Base64 data URL)
│
├── wardens/                    # Warden credentials
│       └── {docId}
│               └── name, email, password, id
│
├── workers/                    # Staff/Worker credentials
│       └── {docId}
│               └── Name, Email, password, Work_id, Role, phone
│
├── complaints/                 # All filed complaints
│       └── {docId}
│               ├── title, description, room, category
│               ├── studentId, imageUrl (Base64)
│               ├── status  →  "Pending" | "In Progress" | "Completed" | "Overdue"
│               ├── timestamp  (Firestore server timestamp)
│               └── docId
│
├── announcements/              # Broadcast to all roles
├── staff_announcements/        # Staff-only announcements
├── student_announcements/      # Student-only announcements
│
└── emergency_contacts/
        └── contacts
                ├── warden1_name, warden1_phone
                ├── warden2_name, warden2_phone
                ├── warden3_name, warden3_phone
                └── doctor_phone
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Platform | Android (Native Java) |
| Build System | Gradle with Kotlin DSL |
| Backend | Firebase Firestore (NoSQL, real-time) |
| Auth | Custom credential auth via Firestore |
| Image Handling | Android `Bitmap` API · Base64 encoding · Glide 4.16.0 |
| Session | Android `SharedPreferences` |
| UI | RecyclerView · ViewPager2 · MaterialCardView · AlertDialog |
| Automation | Python 3 (`firebase-admin` · `Pillow`) |
| Min / Target SDK | API 24 / API 36 |
| Java Version | Java 11 |

---

## 📁 Project Structure

```
HostelComplaintApp/
│
├── app/src/main/java/com/example/hostelcomplaintapp/
│   │
│   ├── MainActivity.java                  # Landing · session-aware router
│   ├── LoginForm.java                     # Unified login for all 3 roles
│   │
│   ├── HomePage_Student.java              # Student dashboard
│   ├── HomePage_Warden.java               # Warden dashboard
│   │
│   ├── RaiseComplaintActivity.java        # File complaint (camera + GPS + Firestore)
│   ├── Totalcomplaint.java                # All complaints (Warden view)
│   ├── Totalcomplaint_student.java        # All complaints (Student view)
│   ├── pendingcomplaint.java              # Pending complaints (Warden)
│   ├── pendingcomplaint_student.java      # Pending complaints (Student)
│   │
│   ├── AddAnnouncementActivity.java       # Role-targeted announcement creation
│   ├── Notification.java                  # Notification feed (Warden)
│   ├── Notification_student.java          # Notification feed (Student)
│   │
│   ├── staff_manage.java                  # Staff CRUD (Warden)
│   ├── student_profile_pg.java            # Student profile + dark mode
│   ├── emergency_issue_student.java       # Emergency contacts board
│   │
│   ├── ComplaintAdapter.java              # RecyclerView adapter (Warden/Student)
│   ├── ComplaintModel.java                # Complaint data model
│   ├── AnnouncementAdapter.java           # ViewPager2 announcement adapter
│   ├── NotificationAdapter.java           # Notification feed adapter
│   │
│   └── worker/                            # Worker (Staff) module
│       ├── WorkerDashboardActivity.java   # Live metrics dashboard
│       ├── WorkerTaskListActivity.java    # Filterable task list
│       ├── WorkerComplaintAdapter.java    # Worker-specific complaint adapter
│       ├── WorkerTaskDetailsActivity.java # Complaint detail + resolve
│       ├── WorkerProfileActivity.java     # Worker profile
│       └── WorkerNavigationHelper.java    # Centralized bottom nav wiring
│
├── scripts/
│   └── upload_profile_images.py          # Bulk profile photo upload automation
│
└── app/build.gradle.kts                  # Dependencies + Firebase BOM
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Hedgehog or newer recommended)
- Android device / emulator running API 24+
- A Firebase project with Firestore enabled
- Python 3.8+ (only needed for bulk image upload script)

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/HostelComplaintApp.git
cd HostelComplaintApp
```

### 2. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/) → Create or select a project
2. Add an Android app with package name `com.example.hostelcomplaintapp`
3. Download `google-services.json` and place it at `app/google-services.json`
4. Enable **Cloud Firestore** in the Firebase Console (start in test mode for development)

### 3. Seed the Database

Create the following collections in Firestore with at least one document each:

- `IT_Students_data` — fields: `name`, `email`, `password`, `sapId`, `rollNo`
- `wardens` — fields: `name`, `email`, `password`, `id`
- `workers` — fields: `Name`, `Email`, `password`, `Work_id`, `Role`, `phone`

### 4. Build and Run

Open the project in Android Studio and click **Run** (▶️), or:

```bash
./gradlew assembleDebug
```

---

## 🐍 Bulk Profile Image Upload Script

The `scripts/upload_profile_images.py` script automates loading student profile photos directly into Firestore.

### What It Does

- Scans a local folder for student images
- Extracts roll numbers from filenames using regex (`A201.jpg`, `YATIN PATIL_A201.jpg`, etc.)
- Compresses each image to 800px max (85% JPEG quality) using **Pillow** to respect Firestore's 1 MiB document limit
- Encodes to a Base64 data URL and writes it to the matching student's `profileImage` field in Firestore

### Setup

```bash
cd scripts
pip install firebase-admin pillow
```

1. Download your Firebase service account key:
   **Firebase Console → Project Settings → Service Accounts → Generate New Private Key**
2. Save it as `scripts/serviceAccountKey.json`
3. Update `IMAGE_FOLDER` in the script to point to your images folder

```bash
python upload_profile_images.py
```

> **Expected filename format:** `STUDENTNAME_A201.jpg` — the script extracts the roll number `A201` using regex.

---

## 📸 Image Handling

Complaint photos go through a compression pipeline before being stored in Firestore:

```
Camera capture (raw Bitmap)
    │
    ▼
compressBitmap()
    ├── Downscale to max 1024px (maintaining aspect ratio)
    └── JPEG re-encode at 85% quality
    │
    ▼
bitmapToBase64DataUrl()
    └── Encodes as "data:image/jpeg;base64,..."
    │
    ▼
Stored in Firestore complaints document (imageUrl field)
```

This keeps stored images well under Firestore's 1 MiB per-document limit while maintaining acceptable visual quality.

---

## ⚡ Real-Time Updates

The app uses Firestore `addSnapshotListener()` for live data across key screens:

| Screen | Listener |
|---|---|
| Worker Dashboard | Counts recomputed on every complaint write |
| Worker Task List | Full list rebuilt on any status change |
| Warden Pending / Total Complaints | Filtered/full list rebuilt live |
| Home Screen Carousel | Refreshes on new announcement |
| Warden Notification Feed | Listens on `announcements` collection |

All listeners are deregistered in `onDestroy()` via `ListenerRegistration.remove()` or `Handler.removeCallbacks()` to prevent memory leaks.

---

## 🔒 Security Notes

- Authentication is handled via credential matching against Firestore collections. Passwords are currently stored in plain text — **do not use this as-is in a production environment**. The recommended upgrade path is to migrate to [Firebase Authentication](https://firebase.google.com/docs/auth).
- The Python service account key (`serviceAccountKey.json`) is excluded from version control via `.gitignore`. **Never commit this file.**
- Runtime permissions (Camera, Location) are requested with `ActivityCompat.requestPermissions()` before use.
- All Firestore field reads are null-checked before use to prevent `NullPointerExceptions`.

---

## 📦 Dependencies

```kotlin
// Firebase BOM 33.1.2
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-database")
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-storage")

// Image loading
implementation("com.github.bumptech.glide:glide:4.16.0")

// AndroidX
implementation(libs.appcompat)
implementation(libs.material)
implementation(libs.constraintlayout)
implementation(libs.activity)
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature-name`)
3. Commit your changes (`git commit -m 'Add: your feature description'`)
4. Push to the branch (`git push origin feature/your-feature-name`)
5. Open a Pull Request

---

## 📄 License

This project is for educational purposes. No license has been applied — feel free to use and adapt with attribution.

---

<div align="center">

Built with ❤️ using Android · Java · Firebase Firestore

</div>
