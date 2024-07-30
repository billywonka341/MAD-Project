package com.example.madprojectnew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignupSelect extends AppCompatActivity {

    FirebaseAuth auth;

   FirebaseUser user;

    Button btnlogin, btnsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        if(user != null)
        {
            movetomain();
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetologin();
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetosignup();
            }
        });


    }

    private void movetosignup()
    {
        startActivity(new Intent(LoginSignupSelect.this, SignupActivity.class));
        finish();
    }

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    private void movetologin()
    {
        startActivity(new Intent(LoginSignupSelect.this, LoginActivity.class));
        finish();
    }

    private void movetomain()
    {
        startActivity(new Intent(LoginSignupSelect.this, MainActivity.class));
        finish();
    }


    private void init()
    {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_signup_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnlogin = findViewById(R.id.btnlogin);
        btnsignup = findViewById(R.id.btnsignup);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}