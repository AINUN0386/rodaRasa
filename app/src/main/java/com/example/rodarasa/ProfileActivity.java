package com.example.rodarasa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.rodarasa.model.ReportedTruck; // Import the new model

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView reportedTrucksListTextView; // Renamed for clarity
    private Button logoutButton;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private static final String PREFS_NAME = "RodaRasaPrefs";
    private static final String KEY_REPORTED_TRUCKS = "reported_trucks_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameTextView = findViewById(R.id.userName);
        reportedTrucksListTextView = findViewById(R.id.reportedTrucksList); // Initialize the TextView
        logoutButton = findViewById(R.id.logoutButton);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new GsonBuilder().setPrettyPrinting().create();

        // Retrieve the user email passed from HomeActivity
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        // Set the TextView to display the user's email
        if (userEmail != null && !userEmail.isEmpty()) {
            userNameTextView.setText("User ID: " + userEmail);
        } else {
            // Fallback if no email is passed (e.g., for testing or if flow changes)
            userNameTextView.setText("User ID: Not Available");
        }

        // Load and display reported food trucks history
        displayReportedFoodTrucks();

        logoutButton.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();

            // Create an Intent to go back to MainActivity (Login/Signup)
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);

            // Clear the activity stack so the user cannot go back to HomeActivity or ProfileActivity
            // using the back button after logging out.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish(); // Finish ProfileActivity
        });
    }

    /**
     * Loads the list of ReportedTrucks from SharedPreferences and displays them.
     */
    private void displayReportedFoodTrucks() {
        List<ReportedTruck> history = getReportedTrucksHistory();
        if (history.isEmpty()) {
            reportedTrucksListTextView.setText("No food trucks reported yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                ReportedTruck truck = history.get(i);
                sb.append(i + 1).append(". ")
                        .append(truck.getNameType())
                        .append(" (")
                        .append(truck.getReportedDate())
                        .append(")\n");
            }
            reportedTrucksListTextView.setText(sb.toString());
        }
    }

    /**
     * Retrieves the list of ReportedTrucks from SharedPreferences.
     * Returns an empty list if no history is found.
     */
    private List<ReportedTruck> getReportedTrucksHistory() {
        String json = sharedPreferences.getString(KEY_REPORTED_TRUCKS, null);
        if (json == null) {
            return new ArrayList<>();
        }

        // Define the type for Gson to deserialize the list
        Type type = new TypeToken<List<ReportedTruck>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
