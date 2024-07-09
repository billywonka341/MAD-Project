package com.example.madprojectnew;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    ExtendedFloatingActionButton fabEx, fabExpense;
    FloatingActionButton fabAdd;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    FirebaseAuth mAuth;
    DBHelper dbHelper;

    DBHelperForIncome dbHelperForIncome;

    TextView tvfood, tvTravel, tvBills, tvOthers;

    FloatingActionButton btnProfile;

    PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pieChartView = findViewById(R.id.PieChart);


        fabEx = findViewById(R.id.fabEx);
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        tvfood = findViewById(R.id.tvfood);
        tvTravel = findViewById(R.id.tvTravel);
        tvBills = findViewById(R.id.tvBills);
        tvOthers = findViewById(R.id.tvOthers);
        btnProfile = findViewById(R.id.btnProfile);
        fabExpense = findViewById(R.id.fabExpense); // Initialize your TextView
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            dbHelper = new DBHelper(this, userId + "_items.db");

            dbHelperForIncome = new DBHelperForIncome(this, userId+"_income.db");

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            itemAdapter = new ItemAdapter(dbHelper.getAllItems(), dbHelper,dbHelperForIncome, this, this::updateExpense);
            recyclerView.setAdapter(itemAdapter);



            fabAdd.setOnClickListener(v -> showAddItemDialog());
            fabEx.setOnClickListener(v -> showAddIncomeDialog());
            btnProfile.setOnClickListener(v -> movetoProfile());

            updateExpense();
        }
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText inputAmount = viewInflated.findViewById(R.id.input_amount);
        final EditText inputCategory = viewInflated.findViewById(R.id.input_category);
        final EditText inputDescription = viewInflated.findViewById(R.id.input_description);

        builder.setView(viewInflated);
        builder.setPositiveButton("Add", null); // Set to null initially
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the onClick listener for the positive button to handle validation
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

            // If all validations pass, add the item to the database
            if (isValid) {
                dbHelper.addItem(String.valueOf(Double.parseDouble(amountStr)), category, description);
                itemAdapter.updateData(dbHelper.getAllItems());
                updateExpense();
                dialog.dismiss();
            }
        });
    }





    private void movetoProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void showAddIncomeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_income, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextIncome = dialogView.findViewById(R.id.edit_text_income);

        dialogBuilder.setTitle("Add Income");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String incomeStr = editTextIncome.getText().toString().trim();
                if (!incomeStr.isEmpty()) {
                    double income = Double.parseDouble(incomeStr);
                    long result = dbHelperForIncome.addIncome(income);
                    if (result != -1) {
                        Toast.makeText(MainActivity.this, "Income added successfully", Toast.LENGTH_SHORT).show();
                        updateExpense();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to add income", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter an income amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    void updateExpense() {
        double totalAmount = dbHelper.getSumOfAmounts();
        double totalfoodsum = dbHelper.getSumOfAmountsForFood();
        double totaltravelsum = dbHelper.getSumOfAmountsForTravel();
        double totalbillssum = dbHelper.getSumOfAmountsForBills();
        double TotalIncomeSum = dbHelperForIncome.getTotalIncome();
        double totalSumExCat = dbHelper.getTotalSumExcludingCategories();

        String exp = "Expense: " + totalAmount;
        String foodsum = "Food: "+totalfoodsum;
        String travelsum = "Travel: "+totaltravelsum;
        String billssum = "Bills: "+totalbillssum;
        String TotalIncome = "Income: "+TotalIncomeSum;
        String OthersSums = "Others: "+totalSumExCat;

        tvfood.setText(foodsum);
        tvfood.setText(foodsum);

        tvTravel.setText(travelsum);
        tvTravel.setText(travelsum);

        tvBills.setText(billssum);
        tvBills.setText(billssum);

        fabExpense.setText(exp);
        fabExpense.setText(exp);

        tvOthers.setText(OthersSums);
        tvOthers.setText(OthersSums);

        fabEx.setText(TotalIncome);
        fabEx.setText(TotalIncome);

        int PerFood = (int) ((dbHelper.getSumOfAmountsForFood()/dbHelper.getSumOfAmounts())*100);
        int PerTravel = (int) ((dbHelper.getSumOfAmountsForTravel()/dbHelper.getSumOfAmounts())*100);
        int PerBill = (int) ((dbHelper.getSumOfAmountsForBills()/dbHelper.getSumOfAmounts())*100);
        int PerOthers = (int) ((dbHelper.getTotalSumExcludingCategories()/dbHelper.getSumOfAmounts())*100);

        float[] newData = {PerFood, PerTravel, PerBill, PerOthers};
        pieChartView.setData(newData);


        // Request a layout update
        fabExpense.requestLayout();
        tvfood.requestLayout();
        tvTravel.requestLayout();
        tvBills.requestLayout();
        fabEx.requestLayout();
        tvOthers.requestLayout();
    }
}

