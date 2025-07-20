package com.example.rodarasa;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.rodarasa.model.ReportedTruck; // Import the new model

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFormActivity extends AppCompatActivity implements LocationListener {

    private EditText etFoodTruckNameType, etLocationDescription, etLatitude, etLongitude;
    private Button btnGetCurrentLocation, btnSubmitReport;
    private LocationManager locationManager;
    private SharedPreferences sharedPreferences; // SharedPreferences instance
    private Gson gson; // Gson instance for JSON serialization/deserialization

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "RodaRasaPrefs";
    private static final String KEY_REPORTED_TRUCKS = "reported_trucks_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        // Initialize UI elements
        etFoodTruckNameType = findViewById(R.id.etFoodTruckNameType);
        etLocationDescription = findViewById(R.id.etLocationDescription);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Initialize SharedPreferences
        gson = new GsonBuilder().setPrettyPrinting().create(); // Initialize Gson

        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());
        btnSubmitReport.setOnClickListener(v -> submitReport());
    }

    private void getCurrentLocation() {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Check if GPS is enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS in your device settings.", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Getting current location...", Toast.LENGTH_SHORT).show();
        // Request location updates (min time 0, min distance 0 for immediate update)
        // Ensure you have ACCESS_FINE_LOCATION permission for GPS_PROVIDER
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(); // Try getting location again if permission granted
            } else {
                Toast.makeText(this, "Location permission denied. Cannot get current location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Display location in EditText fields
        etLatitude.setText(String.valueOf(location.getLatitude()));
        etLongitude.setText(String.valueOf(location.getLongitude()));
        Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();

        // Stop listening for updates once location is received (unless continuous tracking is desired)
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Deprecated, but can be overridden for provider status changes
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(this, "GPS enabled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "GPS disabled. Please enable it.", Toast.LENGTH_LONG).show();
    }

    private void submitReport() {
        String nameType = etFoodTruckNameType.getText().toString().trim();
        String description = etLocationDescription.getText().toString().trim();
        String latitudeStr = etLatitude.getText().toString().trim();
        String longitudeStr = etLongitude.getText().toString().trim();

        if (nameType.isEmpty() || description.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);

            // Get current date for reporting
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // Create a ReportedTruck object
            ReportedTruck newReport = new ReportedTruck(nameType, currentDate);

            // Save the report to SharedPreferences
            saveReportedTruck(newReport);

            Toast.makeText(this, "Report Submitted!", Toast.LENGTH_LONG).show();

            finish(); // Close the form after submission
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid latitude or longitude format.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves a new ReportedTruck to SharedPreferences.
     * It retrieves the existing list, adds the new report, and saves the updated list.
     */
    private void saveReportedTruck(ReportedTruck newReport) {
        List<ReportedTruck> history = getReportedTrucksHistory(); // Get existing history
        history.add(0, newReport); // Add new report to the beginning of the list

        // Convert the list to a JSON string
        String json = gson.toJson(history);

        // Save the JSON string to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_REPORTED_TRUCKS, json);
        editor.apply(); // Use apply() for async save, commit() for sync save
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


    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening for location updates when the activity is paused
        // to save battery, unless continuous tracking is required.
        locationManager.removeUpdates(this);
    }
}
