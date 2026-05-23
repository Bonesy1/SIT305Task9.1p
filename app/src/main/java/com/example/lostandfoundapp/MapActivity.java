package com.example.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper db;
    private List<Advert> advertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = new DatabaseHelper(this);
        advertList = db.getAllAdverts();

        Button btnBack = findViewById(R.id.btnMapBack);
        btnBack.setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        for (Advert advert : advertList) {
            if (advert.getLatitude() != 0 || advert.getLongitude() != 0) {
                LatLng pos = new LatLng(advert.getLatitude(), advert.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(advert.getItemName())
                        .snippet(advert.getPostType() + ": " + advert.getLocation()));
                if (marker != null) {
                    marker.setTag(advert);
                }
            }
        }

        if (!advertList.isEmpty()) {
            Advert last = advertList.get(advertList.size() - 1);
            if (last.getLatitude() != 0 || last.getLongitude() != 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(last.getLatitude(), last.getLongitude()), 10));
            }
        }

        mMap.setOnInfoWindowClickListener(marker -> {
            Advert advert = (Advert) marker.getTag();
            if (advert != null) {
                Intent intent = new Intent(MapActivity.this, DetailActivity.class);
                intent.putExtra("selected_advert", advert);
                startActivity(intent);
            }
        });
    }
}
