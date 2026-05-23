package com.example.lostandfoundapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.net.Uri;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class CreateNewAdvertActivity extends AppCompatActivity {

    EditText etName, etPhone, etDescription, etDate;
    RadioGroup rgPostType;
    Spinner spinnerCategory;
    Button btnSave, btnSelectPhoto;
    ImageButton btnGetCurrentLocation;
    ImageView ivSelectedPhoto;
    DatabaseHelper db;
    String selectedImageUri = "";
    String selectedLocationName = "";
    double selectedLat = 0, selectedLng = 0;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new_advert);

        db = new DatabaseHelper(this);

        // Initialize Places
        if (!Places.isInitialized()) {
            try {
                String apiKey = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getString("com.google.android.geo.API_KEY");
                if (apiKey != null) {
                    Places.initialize(getApplicationContext(), apiKey);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        rgPostType = findViewById(R.id.radioGroup);
        etName = findViewById(R.id.NamePlainText);
        etPhone = findViewById(R.id.PhonePlainText);
        etDescription = findViewById(R.id.DescriptionTextMultiLine);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etDate = findViewById(R.id.editTextDate);
        btnSave = findViewById(R.id.NewAdvertSaveButton);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        ivSelectedPhoto = findViewById(R.id.ivSelectedPhoto);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);

        // Initialize Autocomplete fragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION, Place.Field.FORMATTED_ADDRESS));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedLocationName = place.getFormattedAddress();
                    if (place.getLocation() != null) {
                        selectedLat = place.getLocation().latitude;
                        selectedLng = place.getLocation().longitude;
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(CreateNewAdvertActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        // Photo Picker launcher
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        selectedImageUri = uri.toString();
                        ivSelectedPhoto.setImageURI(uri);
                        ivSelectedPhoto.setVisibility(View.VISIBLE);
                        try {
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                });

        btnSelectPhoto.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        // Populate Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            int selectedId = rgPostType.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, R.string.select_post_type_error, Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton rbSelected = findViewById(selectedId);
            String postType = rbSelected.getText().toString();

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String location = selectedLocationName;
            String category = spinnerCategory.getSelectedItem().toString();

            // 1. Check for empty fields
            if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields_error, Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Validate Name (No numbers or symbols - letters and spaces only)
            if (!name.matches("^[a-zA-Z\\s]+$")) {
                etName.setError(getString(R.string.invalid_name_error));
                return;
            }

            // 3. Validate Phone (Exactly 10 digits)
            if (!phone.matches("^\\d{10}$")) {
                etPhone.setError(getString(R.string.invalid_phone_error));
                return;
            }

            // 4. Validate Date (DD/MM/YYYY)
            if (!date.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                etDate.setError(getString(R.string.invalid_date_error));
                return;
            }

            String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

            Advert advert = new Advert(postType, name, phone, description, date, location, timestamp, category, selectedImageUri, selectedLat, selectedLng);
            long id = db.insertAdvert(advert);

            if (id > 0) {
                Toast.makeText(this, R.string.advert_saved_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.advert_save_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                selectedLat = location.getLatitude();
                selectedLng = location.getLongitude();
                
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(selectedLat, selectedLng, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        selectedLocationName = addresses.get(0).getAddressLine(0);
                        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
                        if (autocompleteFragment != null) {
                            autocompleteFragment.setText(selectedLocationName);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
}
