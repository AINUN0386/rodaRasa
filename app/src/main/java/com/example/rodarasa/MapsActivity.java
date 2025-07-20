package com.example.rodarasa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final LatLng MALAYSIA_CENTER = new LatLng(4.2105, 101.9758);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Button to find nearby trucks
        Button btnFind = findViewById(R.id.btnFindNearbyTrucks);
        btnFind.setOnClickListener(view -> loadFoodTrucks());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Center the map on Malaysia
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MALAYSIA_CENTER, 6f));
    }

    private void loadFoodTrucks() {
        String url = "http://192.168.174.1/get_trucks.php"; // Replace with your API URL
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        mMap.clear(); // Clear previous markers

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject truck = response.getJSONObject(i);

                            String type = truck.getString("type");
                            double lat = truck.getDouble("latitude");
                            double lng = truck.getDouble("longitude");
                            String reporter = truck.getString("reporter");
                            String time = truck.getString("timestamp");

                            LatLng location = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title(type)
                                    .snippet("Reported by: " + reporter + "\nAt: " + time)
                            );

                            // Optional: move camera to first truck
                            if (i == 0) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f));
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing truck data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace(); // add this
                    Toast.makeText(this, "Failed to load truck data", Toast.LENGTH_SHORT).show();
                }        );

        queue.add(request);
    }
}
