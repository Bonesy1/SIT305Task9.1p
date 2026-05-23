package com.example.lostandfoundapp;

import java.io.Serializable;

public class Advert implements Serializable {

    private int id;
    private String postType; // "Lost" or "Found"
    private String date;
    private String location;
    private String itemName;
    private String itemDescription;
    private String contactPhone;
    private String timestamp;
    private String category;
    private String imageUri;
    private double latitude;
    private double longitude;

    // Constructor with ID (for reading from DB)
    public Advert(int id, String postType, String itemName, String contactPhone,
                  String itemDescription, String date, String location, String timestamp, String category, String imageUri,
                  double latitude, double longitude) {
        this.id = id;
        this.postType = postType;
        this.itemName = itemName;
        this.contactPhone = contactPhone;
        this.itemDescription = itemDescription;
        this.date = date;
        this.location = location;
        this.timestamp = timestamp;
        this.category = category;
        this.imageUri = imageUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor without ID (for creating new)
    public Advert(String postType, String itemName, String contactPhone,
                  String itemDescription, String date, String location, String timestamp, String category, String imageUri,
                  double latitude, double longitude) {
        this.postType = postType;
        this.itemName = itemName;
        this.contactPhone = contactPhone;
        this.itemDescription = itemDescription;
        this.date = date;
        this.location = location;
        this.timestamp = timestamp;
        this.category = category;
        this.imageUri = imageUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id; }
    public String getPostType() { return postType; }
    public String getItemName() { return itemName; }
    public String getDate() { return date; }
    public String getItemDescription() { return itemDescription; }
    public String getLocation() { return location; }
    public String getContactPhone() { return contactPhone; }
    public String getTimestamp() { return timestamp; }
    public String getCategory() { return category; }
    public String getImageUri() { return imageUri; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
