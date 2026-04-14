package com.example.hostelcomplaintapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.widget.Button;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<Notificationpgdatastore> list;

    // Constructor
    public NotificationAdapter(Context context, ArrayList<Notificationpgdatastore> list) {
        this.context = context;
        this.list = list;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtText, txtWarden, txtTime;
        ImageView arrowBtn;
        Button removeBtn;

        public ViewHolder(View itemView) {
            super(itemView);

            txtText = itemView.findViewById(R.id.txtText);
            txtWarden = itemView.findViewById(R.id.txtWarden);
            txtTime = itemView.findViewById(R.id.txtTime);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.notificationpg_card, parent, false); // ✅ correct layout

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Notificationpgdatastore model = list.get(position);

        // Safe text
        holder.txtText.setText(
                model.getText() != null ? model.getText() : "No message"
        );

        holder.txtWarden.setText("Warden: " +
                (model.getWardenId() != null ? model.getWardenId() : "Unknown")
        );

        // Safe time formatting
        String formattedTime = "No time";
        if (model.getTimestamp() != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            formattedTime = sdf.format(new Date(model.getTimestamp()));
        }

        holder.txtTime.setText(formattedTime);

        /// remove btn clickable ///
        holder.removeBtn.setOnClickListener(v -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String docId = model.getId();

            db.collection("announcements")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener(unused -> {

                        // remove from screen
                        list.remove(position);
                        notifyItemRemoved(position);

                    });
        });

        // 🔥 CLICK LISTENER FOR POPUP
        holder.arrowBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Notification Details");

            builder.setMessage(
                    "Message: " + holder.txtText.getText() + "\n\n" +
                            "Warden: " + holder.txtWarden.getText() + "\n\n" +
                            "Time: " + holder.txtTime.getText()
            );


            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}