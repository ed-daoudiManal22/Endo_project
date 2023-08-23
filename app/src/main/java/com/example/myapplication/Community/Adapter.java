package com.example.myapplication.Community;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ArrayList<Model> list;
    private final Context context;

    public Adapter(Context context,ArrayList<Model> list) {
        this.context = context;
        this.list = list;
        this.notifyDataSetChanged();
    }
    public void filter_list(ArrayList<Model> filter_list){
        list = filter_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        holder.description.setText(model.getDesc());
        holder.share_count.setText(model.getShare_count() + " " + context.getString(R.string.shared));
        holder.author.setText(model.getAuthor());

        Glide.with(holder.author.getContext()).load(model.getImg()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.author.getContext(), BlogDetail.class);
            intent.putExtra("id", model.getId());
            holder.author.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.author.getContext());
            builder.setTitle("What you want to do?");
            builder.setPositiveButton("UPDATE", (dialog, which) -> {
                final Dialog u_dialog = new Dialog(holder.author.getContext());
                u_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                u_dialog.setCancelable(false);
                u_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                u_dialog.setContentView(R.layout.update_dialog);
                u_dialog.show();

                EditText title = u_dialog.findViewById(R.id.b_tittle);
                EditText desc = u_dialog.findViewById(R.id.b_desc);

                title.setText(model.getTitle());
                desc.setText(model.getDesc());

                TextView dialogbutton = u_dialog.findViewById(R.id.btn_publish);
                TextView cancelbutton = u_dialog.findViewById(R.id.btn_cancel);
                dialogbutton.setOnClickListener(v1 -> {
                    if (title.getText().toString().equals("")) {
                        title.setError("Field is Required!!");
                    } else if (desc.getText().toString().equals("")) {
                        desc.setError("Field is Required!!");
                    } else {


                        HashMap<String, Object> map = new HashMap<>();
                        map.put("tittle", title.getText().toString());
                        map.put("desc", desc.getText().toString());

                        FirebaseFirestore.getInstance().collection("Blogs").document(model.getId()).update(map)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        u_dialog.dismiss();
                                    }
                                });
                    }
                });
                cancelbutton.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    u_dialog.dismiss();
                });
            });
            builder.setNegativeButton("DELETE", (dialog, which) -> {
                AlertDialog.Builder builders = new AlertDialog.Builder(holder.author.getContext());
                builders.setTitle("Are you sure to Delete it??");
                builders.setPositiveButton("Yes! I am Sure", (dialog1, which1) -> {
                    // Delete the subcollection first
                    deleteSubcollections(model.getId(), new OnSubcollectionDeletedListener() {
                        @Override
                        public void onSubcollectionDeleted() {
                            // Delete the main blog document after successful subcollection deletion
                            FirebaseFirestore.getInstance().collection("Blogs")
                                    .document(model.getId()).delete();
                            dialog1.dismiss();
                        }

                        @Override
                        public void onSubcollectionDeletionFailed() {
                            Log.d("Blogs Deletion", "Error getting documents: Deletion Failed");
                            dialog1.dismiss(); // Dismiss the dialog on deletion failure
                        }
                    });
                });
                AlertDialog dialogs = builders.create();
                dialogs.show();
            });

            // Create and show the main dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, share_count, author, description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            date = itemView.findViewById(R.id.t_date);
            title = itemView.findViewById(R.id.textView9);
            description = itemView.findViewById(R.id.textViewDescription);
            share_count = itemView.findViewById(R.id.textView10);
            author = itemView.findViewById(R.id.textView8);

        }
    }
    private void deleteSubcollections(String blogId, OnSubcollectionDeletedListener listener) {
        CollectionReference subcollectionRef = FirebaseFirestore.getInstance()
                .collection("Blogs").document(blogId).collection("Comments");

        subcollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Delete each document in the subcollection
                    document.getReference().delete();
                }
                listener.onSubcollectionDeleted(); // Notify listener on successful deletion
            } else {
                Log.d("Blogs Deletion", "Error getting documents: ", task.getException());
                listener.onSubcollectionDeletionFailed(); // Notify listener on deletion failure
            }
        });
    }

    interface OnSubcollectionDeletedListener {
        void onSubcollectionDeleted();
        void onSubcollectionDeletionFailed();
    }

}