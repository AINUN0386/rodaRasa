package com.example.rodarasa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnProfile;
    private Button btnAboutUs;
    private Button btnMaps;
    private Button btnReportForm; // <--- ADD THIS LINE: Declare the new Report Form button

    // Placeholder for the logged-in user's email.
    private String loggedInUserEmail = "ali@gmail.com"; // IMPORTANT: Placeholder email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Assuming this is your main menu/map layout

        // Initialize buttons (ensure these IDs exist in activity_home.xml)
        btnAboutUs = findViewById(R.id.btnAboutUs);
        btnMaps = findViewById(R.id.btnMaps);
        btnProfile = findViewById(R.id.btnProfile);
        btnReportForm = findViewById(R.id.btnReportForm); // <--- ADD THIS LINE: Initialize the new button

        btnAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
        });

        btnMaps.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MapsActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("USER_EMAIL", loggedInUserEmail);
            startActivity(intent);
        });

        // Set the OnClickListener for the Report Form button
        btnReportForm.setOnClickListener(v -> { // <--- ADD THIS BLOCK
            startActivity(new Intent(HomeActivity.this, ReportFormActivity.class));
        });
    }
}
