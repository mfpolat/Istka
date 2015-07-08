package com.okuu.istkaafet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Fatih on 6.7.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "istka.db";
    public static final String TABLE_NAME = "hospitals";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HOSPITAL_ID = "hospital_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table hospitals " +
                        "(id integer primary key,hospital_id integer, name text,address text,latitude double, longitude double)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(sqLiteDatabase);
    }

    public void insertHospital(Hospital hospital) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hospital_id", hospital.getId());
        contentValues.put("name", hospital.getName());
        contentValues.put("address", hospital.getAddress());
        contentValues.put("latitude", hospital.getLatitude());
        contentValues.put("longitude", hospital.getLongitude());
        db.insert("hospitals", null, contentValues);
    }

    public Hospital getHospitalById(int hospitalId) {
        Hospital hospital = new Hospital();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from hospitals where hospital_id=" + hospitalId + "", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                hospital.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_HOSPITAL_ID)));
                hospital.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                hospital.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                hospital.setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)));
                hospital.setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));
            }
        }
        return hospital;
    }

    public boolean didHospitalsSave() {
        boolean didHospitalSaved = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from hospitals ", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                didHospitalSaved = true;
            }
        }
        return didHospitalSaved;
    }
}
