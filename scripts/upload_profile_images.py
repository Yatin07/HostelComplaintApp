import os
import base64
from PIL import Image
import io
import re
import firebase_admin
from firebase_admin import credentials, firestore

# ⚠️ CRITICAL SETUP:
# To use this, you must download your 'serviceAccountKey.json' from Firebase Console -> Project Settings -> Service Accounts -> Generate New Private Key
# Replace the path below with where you saved that JSON file on your computer.
CREDENTIALS_PATH = "serviceAccountKey.json"

IMAGE_FOLDER = r"C:\Users\yatin\Downloads\OneDrive_1_4-22-2026"

def compress_to_base64_for_firestore(image_path, max_dim=800):
    """
    Compresses image to fit well within Firestore's strict 1 MiB document limit.
    A full 6MB Base64 image will instantly crash a Firestore Document write!
    """
    with Image.open(image_path) as img:
        img = img.convert('RGB')
        
        # Calculate ratio to maintain aspect
        w, h = img.size
        if w > max_dim or h > max_dim:
            ratio = min(max_dim / w, max_dim / h)
            img = img.resize((int(w * ratio), int(h * ratio)), Image.Resampling.LANCZOS)
            
        # Compress into JPEG
        buffer = io.BytesIO()
        img.save(buffer, format="JPEG", quality=85)
        
        # Convert to Android-compatible Base64 Data URL
        b64_string = base64.b64encode(buffer.getvalue()).decode("utf-8")
        return f"data:image/jpeg;base64,{b64_string}"

def upload_images_to_database():
    print("Initialising Firebase...")
    try:
        cred = credentials.Certificate(CREDENTIALS_PATH)
        firebase_admin.initialize_app(cred)
    except FileNotFoundError:
        print(f"ERROR: Could not find '{CREDENTIALS_PATH}'. Please download it from Firebase Console.")
        return
        
    db = firestore.client()
    
    print(f"Scanning folder: {IMAGE_FOLDER}")
    
    # Example format: "YATIN PATIL_A201.jpg"
    for filename in os.listdir(IMAGE_FOLDER):
        if filename.lower().endswith(('.png', '.jpg', '.jpeg')):
            
            # Step 1: Extract roll number using Regex (e.g. A201, A272)
            match = re.search(r'(A\d{3})', filename, re.IGNORECASE)
            if match:
                roll_no = match.group(1).upper()
            else:
                print(f" [ERROR] Could not detect Roll No in filename: {filename}. Skipping.")
                continue
                
            print(f"Processing Roll No [{roll_no}] - File: {filename}...")
            
            # Step 2: Convert to Base64 safe limit
            image_path = os.path.join(IMAGE_FOLDER, filename)
            base64_str = compress_to_base64_for_firestore(image_path)
            
            # Step 3: Find the student's Document ID (sapId) using their RollNo
            # In your database, documents use 'sapId' as ID, but contain a 'rollNo' field.
            users_ref = db.collection("IT_Students_data").where("rollNo", "==", roll_no).get()
            
            if not users_ref:
                print(f" [NOT FOUND] Student with RollNo {roll_no} not found in DB! Skipping.")
                continue
                
            for doc in users_ref:
                doc_id = doc.id
                print(f" [SUCCESS] Found Student {doc_id} -> Uploading Base64...")
                
                # Push the compressed string into their profileImage field
                db.collection("IT_Students_data").document(doc_id).update({
                    "profileImage": base64_str
                })
                print("    Success!")

if __name__ == "__main__":
    upload_images_to_database()
