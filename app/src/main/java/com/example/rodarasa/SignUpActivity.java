package com.example.rodarasa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView txtLogin = findViewById(R.id.loginPrompt);

        // Sign Up Button Click
        btnSignUp.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Perform your sign-up logic here (e.g. Firebase or SharedPreferences)
                Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to login or main screen
            }
        });

        // Login link click
        txtLogin.setOnClickListener(v -> {
            finish(); // Return to MainActivity
        });
    }
}


