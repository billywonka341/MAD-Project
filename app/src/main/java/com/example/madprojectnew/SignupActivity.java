package com.example.madprojectnew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText TIPassword, TIEmail, TICPassword;

    Button btnsignup, btnloginsign;

    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if(user != null)
        {
            movetomain();
        }

        btnloginsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetologin();
            }
        });


        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = TIEmail.getText().toString().trim();
                String pass = TIPassword.getText().toString();
                String cpass = TICPassword.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    TIEmail.setError("Email can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(pass))
                {
                    TIPassword.setError("Password can't be empty");
                    return;
                }

                if(TextUtils.isEmpty(cpass))
                {
                    TICPassword.setError("Confirm password can't be empty");
                    return;
                }

                if(!TextUtils.equals(pass, cpass))
                {
                    TICPassword.setError("password mis-matched");
                    return;
                }

                ProgressDialog processing = new ProgressDialog(SignupActivity.this);
                processing.setMessage("Registration in process...");
                processing.show();

                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                processing.dismiss();
                                movetomain();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        });
    }


    private void movetomain()
    {
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
        finish();
    }
    private void movetologin()
    {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }
    private void init()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TIEmail = findViewById(R.id.TIEmail);
        TIPassword = findViewById(R.id.TIPassword);
        TICPassword = findViewById(R.id.TICPassword);
        btnsignup = findViewById(R.id.btnSignup);
        btnloginsign = findViewById(R.id.btnloginsign);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }
}