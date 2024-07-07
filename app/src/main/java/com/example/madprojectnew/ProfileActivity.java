package com.example.madprojectnew;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView UserID, Email;

    FirebaseAuth mAuth;

    ExtendedFloatingActionButton btnResetPassword, btnIncomeReset, btnResetExpense;

    FloatingActionButton btnBack;

    Button btnLogout;

    AdView mAdView;

    DBHelper dbHelper;
    DBHelperForIncome dbHelperForIncome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnIncomeReset = findViewById(R.id.btnIncomeReset);
        btnResetExpense = findViewById(R.id.btnResetExpense);
        UserID = findViewById(R.id.UserID);
        Email = findViewById(R.id.Email);

        if (currentUser != null) {

            String userId = currentUser.getUid();
            dbHelper = new DBHelper(this, userId + "_items.db");
            dbHelperForIncome = new DBHelperForIncome(this, userId + "_income.db");

            //Buttons
            btnLogout.setOnClickListener(v -> logout());
            btnBack.setOnClickListener(v -> BacktoMain());
            btnIncomeReset.setOnClickListener(v -> ResetIncome());
            btnResetExpense.setOnClickListener(v -> ResetExpense());
            btnResetPassword.setOnClickListener(v -> ResetPass());

            //Setting TV
            UserID.setText("UserID: "+currentUser.getUid());
            Email.setText("Email: "+ currentUser.getEmail());

            MobileAds.initialize(this, initializationStatus -> {});

            // Find AdView and load an ad
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }


    }

    private void logout()
    {
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginSignupSelect.class);
        startActivity(intent);
        finish();
    }

    private void BacktoMain()
    {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void ResetIncome()
    {
        dbHelperForIncome.deleteAllIncome();
        Toast.makeText(this, "Income Has Been Reset", Toast.LENGTH_SHORT).show();
    }

    private void ResetExpense()
    {
        dbHelper.deleteAllExpenses();
        Toast.makeText(this, "All Expense Has Been Deleted", Toast.LENGTH_SHORT).show();
    }



    private void ResetPass()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null && !email.isEmpty()) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error sending password reset email", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "No email found for the current user", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
        }
    }


}