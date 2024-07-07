package com.example.madprojectnew;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DBHelperForIncome extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "income_table";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_INCOME = "income";

    public DBHelperForIncome(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INCOME + " REAL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addIncome(double income) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME, income);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void deleteIncome(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public static SQLiteDatabase getDatabase(Context context, String uid) {
        DBHelperForIncome dbHelper = new DBHelperForIncome(context, uid + "_income.db");
        return dbHelper.getWritableDatabase();
    }

    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_INCOME + ") as total FROM " + TABLE_NAME;
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

    public void deleteAllIncome() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}
