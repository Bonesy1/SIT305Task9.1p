package com.example.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found_db";
    private static final int DATABASE_VERSION = 5;

    // Table Name
    private static final String TABLE_ADVERTS = "adverts";

    // Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_POST_TYPE = "post_type";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_IMAGE_URI = "image_uri";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ADVERTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_TYPE + " TEXT,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_IMAGE_URI + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADVERTS);
        onCreate(db);
    }

    // Method to insert a new advert
    public long insertAdvert(Advert advert) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST_TYPE, advert.getPostType());
        values.put(COLUMN_ITEM_NAME, advert.getItemName());
        values.put(COLUMN_PHONE, advert.getContactPhone());
        values.put(COLUMN_DESCRIPTION, advert.getItemDescription());
        values.put(COLUMN_DATE, advert.getDate());
        values.put(COLUMN_LOCATION, advert.getLocation());
        values.put(COLUMN_TIMESTAMP, advert.getTimestamp());
        values.put(COLUMN_CATEGORY, advert.getCategory());
        values.put(COLUMN_IMAGE_URI, advert.getImageUri());
        values.put(COLUMN_LATITUDE, advert.getLatitude());
        values.put(COLUMN_LONGITUDE, advert.getLongitude());

        long id = db.insert(TABLE_ADVERTS, null, values);
        db.close();
        return id;
    }

    // Method to fetch all adverts
    public List<Advert> getAllAdverts() {
        List<Advert> advertList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ADVERTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                );
                advertList.add(advert);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return advertList;
    }

    // Method to delete an advert
    public void deleteAdvert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADVERTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
