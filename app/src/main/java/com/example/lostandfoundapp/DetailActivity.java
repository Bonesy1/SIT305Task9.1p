package com.example.lostandfoundapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvName, tvDate, tvLocation, tvDescription, tvPhone, tvTimestamp, tvCategory;
    ImageView ivDetailImage;
    Button btnRemove;
    DatabaseHelper db;
    Advert selectedAdvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = new DatabaseHelper(this);

        tvName = findViewById(R.id.tvDetailName);
        tvDate = findViewById(R.id.tvDetailDate);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvPhone = findViewById(R.id.tvDetailPhone);
        tvTimestamp = findViewById(R.id.tvDetailTimestamp);
        tvCategory = findViewById(R.id.tvDetailCategory);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        btnRemove = findViewById(R.id.btnRemove);

        // Retrieve the selected advert from the Intent
        selectedAdvert = (Advert) getIntent().getSerializableExtra("selected_advert");

        if (selectedAdvert != null) {
            tvName.setText(getString(R.string.detail_name_format, selectedAdvert.getItemName(), selectedAdvert.getPostType()));
            tvDate.setText(getString(R.string.detail_date_format, selectedAdvert.getDate()));
            tvLocation.setText(getString(R.string.detail_location_format, selectedAdvert.getLocation()));
            tvDescription.setText(selectedAdvert.getItemDescription());
            tvPhone.setText(getString(R.string.detail_contact_format, selectedAdvert.getContactPhone()));
            tvTimestamp.setText(getString(R.string.timestamp_format, selectedAdvert.getTimestamp()));
            tvCategory.setText(getString(R.string.detail_category_format, selectedAdvert.getCategory()));

            if (selectedAdvert.getImageUri() != null && !selectedAdvert.getImageUri().isEmpty()) {
                ivDetailImage.setImageURI(Uri.parse(selectedAdvert.getImageUri()));
                ivDetailImage.setVisibility(View.VISIBLE);
            } else {
                ivDetailImage.setVisibility(View.GONE);
            }
        }

        btnRemove.setOnClickListener(v -> {
            if (selectedAdvert != null) {
                db.deleteAdvert(selectedAdvert.getId());
                Toast.makeText(this, R.string.advert_removed, Toast.LENGTH_SHORT).show();
                finish(); // Close activity and return to the list
            }
        });
    }
}
