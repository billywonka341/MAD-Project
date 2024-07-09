package com.example.madprojectnew;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;



public class DBHelper extends SQLiteOpenHelper {
    //private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DESCRIPTION = "description";

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int updateItem(long id, String amount, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DESCRIPTION, description);

        // Updating row
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public long addItem(String amount, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DESCRIPTION, description);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public static SQLiteDatabase getDatabase(Context context, String uid) {
        DBHelper dbHelper = new DBHelper(context, uid + "_items.db");
        return dbHelper.getWritableDatabase();
    }

    public double getSumOfAmounts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(CAST(" + COLUMN_AMOUNT + " AS REAL)) as total FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        double sum = 0;
        if (cursor.moveToFirst()) {
            int totalIndex = cursor.getColumnIndex("total");
            if (totalIndex != -1 && !cursor.isNull(totalIndex)) {
                sum = cursor.getDouble(totalIndex);
            }
        }
        cursor.close();
        return sum;
    }

    public double getSumOfAmountsForFood() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(CAST(" + COLUMN_AMOUNT + " AS REAL)) as total FROM " + TABLE_NAME +
                " WHERE LOWER(" + COLUMN_CATEGORY + ") = 'food'";
        Cursor cursor = db.rawQuery(query, null);
        double sum = 0;
        if (cursor.moveToFirst()) {
            int totalIndex = cursor.getColumnIndex("total");
            if (totalIndex != -1 && !cursor.isNull(totalIndex)) {
                sum = cursor.getDouble(totalIndex);
            }
        }
        cursor.close();
        return sum;
    }

    public double getSumOfAmountsForTravel() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(CAST(" + COLUMN_AMOUNT + " AS REAL)) as total FROM " + TABLE_NAME +
                " WHERE LOWER(" + COLUMN_CATEGORY + ") = 'travel'";
        Cursor cursor = db.rawQuery(query, null);
        double sum = 0;
        if (cursor.moveToFirst()) {
            int totalIndex = cursor.getColumnIndex("total");
            if (totalIndex != -1 && !cursor.isNull(totalIndex)) {
                sum = cursor.getDouble(totalIndex);
            }
        }
        cursor.close();
        return sum;
    }

    public double getSumOfAmountsForBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(CAST(" + COLUMN_AMOUNT + " AS REAL)) as total FROM " + TABLE_NAME +
                " WHERE LOWER(" + COLUMN_CATEGORY + ") = 'bills'";
        Cursor cursor = db.rawQuery(query, null);
        double sum = 0;
        if (cursor.moveToFirst()) {
            int totalIndex = cursor.getColumnIndex("total");
            if (totalIndex != -1 && !cursor.isNull(totalIndex)) {
                sum = cursor.getDouble(totalIndex);
            }
        }
        cursor.close();
        return sum;
    }

    public double getSumOfAmountsForDrinks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(CAST(" + COLUMN_AMOUNT + " AS REAL)) as total FROM " + TABLE_NAME +
                " WHERE LOWER(" + COLUMN_CATEGORY + ") = 'drinks'";
        Cursor cursor = db.rawQuery(query, null);
        double sum = 0;
        if (cursor.moveToFirst()) {
            int totalIndex = cursor.getColumnIndex("total");
            if (totalIndex != -1 && !cursor.isNull(totalIndex)) {
                sum = cursor.getDouble(totalIndex);
            }
        }
        cursor.close();
        return sum;
    }

    public double getTotalSumExcludingCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalSum = 0.0; // Default value

        // Construct the SQL query
        String query = "SELECT SUM(CAST(" + COLUMN_AMOUNT + " AS REAL)) AS total_sum " +
                "FROM " + TABLE_NAME + " " +
                "WHERE LOWER(" + COLUMN_CATEGORY + ") NOT IN ('food', 'travel', 'bills')";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int totalSumIndex = cursor.getColumnIndex("total_sum");
            if (totalSumIndex != -1) {
                totalSum = cursor.getDouble(totalSumIndex);
            } else {
                // Handle case where "total_sum" column is not found
                // Log.e(TAG, "Column 'total_sum' not found in the result set");
            }
        }

        cursor.close();
        return totalSum;
    }

    public void deleteAllExpenses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }


}
