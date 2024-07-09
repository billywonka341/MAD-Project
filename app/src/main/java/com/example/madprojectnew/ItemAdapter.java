package com.example.madprojectnew;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Cursor cursor;
    DBHelper dbHelper;
    Context context;
    OnItemDeletedListener onItemDeletedListener;

    DBHelperForIncome dbHelperForIncome;

    public ItemAdapter(Cursor cursor, DBHelper dbHelper, DBHelperForIncome dbHelperForIncome, MainActivity context, OnItemDeletedListener onItemDeletedListener) {
        this.cursor = cursor;
        this.dbHelper = dbHelper;
        this.dbHelperForIncome = dbHelperForIncome; // Initialize dbHelperForIncome
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

            holder.itemView.setOnLongClickListener(v -> {
                showUpdateDialog(id, amount, category, description);
                return true;
            });
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

    private void showUpdateDialog(long id, String currentAmount, String currentCategory, String currentDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Item");

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_add_item, (ViewGroup) ((MainActivity) context).findViewById(android.R.id.content), false);
        final EditText inputAmount = viewInflated.findViewById(R.id.input_amount);
        final EditText inputCategory = viewInflated.findViewById(R.id.input_category);
        final EditText inputDescription = viewInflated.findViewById(R.id.input_description);

        // Set current values
        inputAmount.setText(currentAmount);
        inputCategory.setText(currentCategory);
        inputDescription.setText(currentDescription);

        builder.setView(viewInflated);
        builder.setPositiveButton("Update", null); // Set to null initially
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the onClick listener for the positive button to handle validation and update
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String amountStr = inputAmount.getText().toString();
            String category = inputCategory.getText().toString();
            String description = inputDescription.getText().toString();

            double TotalIncomeSum = dbHelperForIncome.getTotalIncome();
            double totalAmount = dbHelper.getSumOfAmounts();

            int check = (int) (TotalIncomeSum - totalAmount);

            boolean isValid = true;

            // Validate amount
            if (amountStr.isEmpty()) {
                inputAmount.setError("Amount is required");
                isValid = false;
            } else {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount < 0) {
                        inputAmount.setError("Amount cannot be negative");
                        isValid = false;
                    } else if (check - amount < 0) {
                        inputAmount.setError("Insufficient income. Add more income first.");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    inputAmount.setError("Invalid amount format");
                    isValid = false;
                }
            }

            // Validate category
            if (category.isEmpty()) {
                inputCategory.setError("Category is required");
                isValid = false;
            }

            // Validate description
            if (description.isEmpty()) {
                inputDescription.setError("Description is required");
                isValid = false;
            }

            // If all validations pass, update the item in the database
            if (isValid) {
                dbHelper.updateItem(id, amountStr, category, description);
                updateData(dbHelper.getAllItems());
                ((MainActivity) context).updateExpense(); // Update expense in MainActivity
                dialog.dismiss();
            }
        });
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
