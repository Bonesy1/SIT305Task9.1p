package com.example.lostandfoundapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AdvertAdapter adapter;
    List<Advert> advertList;
    List<Advert> allAdverts;
    DatabaseHelper db;
    Spinner spinnerFilter;
    EditText etRadius;
    Button btnApplyRadius, btnShowMap;

    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all);

        db = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        spinnerFilter = findViewById(R.id.spinnerFilter);
        etRadius = findViewById(R.id.etRadius);
        btnApplyRadius = findViewById(R.id.btnApplyRadius);
        btnShowMap = findViewById(R.id.btnShowMap);

        setupFilterSpinner();

        // Fetch data from database
        allAdverts = db.getAllAdverts();
        advertList = new ArrayList<>(allAdverts);

        adapter = new AdvertAdapter(advertList, advert -> {
            Intent intent = new Intent(ShowAllActivity.this, DetailActivity.class);
            intent.putExtra("selected_advert", advert);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(ShowAllActivity.this, MapActivity.class);
            startActivity(intent);
        });

        btnApplyRadius.setOnClickListener(v -> requestLocationAndFilter());
    }

    private void setupFilterSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add(getString(R.string.filter_all));
        String[] items = getResources().getStringArray(R.array.categories_array);
        for (String item : items) {
            categories.add(item);
        }

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void requestLocationAndFilter() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                userLocation = location;
                applyFilters();
            } else {
                Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String category = spinnerFilter.getSelectedItem().toString();
        String radiusStr = etRadius.getText().toString().trim();
        double radius = radiusStr.isEmpty() ? -1 : Double.parseDouble(radiusStr);

        advertList.clear();
        for (Advert advert : allAdverts) {
            boolean matchesCategory = category.equals(getString(R.string.filter_all)) || advert.getCategory().equals(category);
            boolean matchesRadius = true;

            if (radius > 0) {
                if (userLocation != null && advert.getLatitude() != 0 && advert.getLongitude() != 0) {
                    float[] results = new float[1];
                    Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                            advert.getLatitude(), advert.getLongitude(), results);
                    float distanceInKm = results[0] / 1000;
                    matchesRadius = distanceInKm <= radius;
                } else {
                    matchesRadius = false;
                }
            }

            if (matchesCategory && matchesRadius) {
                advertList.add(advert);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationAndFilter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        allAdverts.clear();
        allAdverts.addAll(db.getAllAdverts());
        applyFilters();
    }
}
