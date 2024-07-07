package com.example.madprojectnew;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Cursor cursor;
    DBHelper dbHelper;
    Context context;
    OnItemDeletedListener onItemDeletedListener;

    public ItemAdapter(Cursor cursor, DBHelper dbHelper, MainActivity context, OnItemDeletedListener onItemDeletedListener) {
        this.cursor = cursor;
        this.dbHelper = dbHelper;
        this.context = context;
        this.onItemDeletedListener = onItemDeletedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String amount = cursor.getString(cursor.getColumnIndexOrThrow("amount"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

            holder.amount.setText(amount);
            holder.category.setText(category);
            holder.description.setText(description);

            holder.itemView.setOnClickListener(v -> showDeleteDialog(id));
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void updateData(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    private void showDeleteDialog(long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.deleteItem(id);
            updateData(dbHelper.getAllItems());
            if (onItemDeletedListener != null) {
                onItemDeletedListener.onItemDeleted();
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView amount;
        public TextView category;
        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            category = itemView.findViewById(R.id.category);
            description = itemView.findViewById(R.id.description);
        }
    }

    public interface OnItemDeletedListener {
        void onItemDeleted();
    }
}
