package com.example.madprojectnew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class LoginActivity extends AppCompatActivity {

    TextInputEditText TIPassword, TIEmail;

    TextView tvForget;

    ProgressBar progress_bar;

    Button btnmovetosignup, btnlogin;

    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        progress_bar.setVisibility(View.GONE);

        if(user != null)
        {
            movetomain();
        }

        btnmovetosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetosignup();
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = TIEmail.getText().toString().trim();
                String pass = TIPassword.getText().toString();

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

                progress_bar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progress_bar.setVisibility(View.GONE);
                                movetomain();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder fpDialog = new AlertDialog.Builder(LoginActivity.this);
                EditText etRegEmail = new EditText(LoginActivity.this);
                etRegEmail.setHint("Enter registered email...");
                fpDialog.setView(etRegEmail);

                fpDialog.setPositiveButton("Send email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = etRegEmail.getText().toString().trim();

                        if(TextUtils.isEmpty(email))
                        {
                            etRegEmail.setError("Email can't be empty");
                            return;
                        }

                        ProgressDialog processing = new ProgressDialog(LoginActivity.this);
                        processing.setMessage("sending password reset email...");
                        processing.show();

                        auth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        processing.dismiss();
                                        Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        processing.dismiss();
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                fpDialog.show();
            }
        });

    }


    private void movetosignup()
    {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }
    private void movetomain()
    {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    private void init()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TIEmail = findViewById(R.id.TIEmail);
        TIPassword = findViewById(R.id.TIPassword);
        btnmovetosignup = findViewById(R.id.btnmovetosignup);
        btnlogin = findViewById(R.id.btnlogin);
        tvForget = findViewById(R.id.tvForget);
        progress_bar = findViewById(R.id.progress_bar);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}