package com.example.nils.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

class DBhelper extends SQLiteOpenHelper {

    // set fields of database schema
    private static final String DATABASE_NAME = "myDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "contacts";

    private String todo_id = "toDo";

    // constructor
    DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // onCreate: create new table (this function will be executed automatically)
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                todo_id + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    // onUpgrade: delete existing table & create new one (will be executed automatically)
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    // CRUD methods (create, read, update, delete)

    // create
    void create(String doable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(todo_id, doable);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // read
    ArrayList<HashMap<String, String>> read() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT _id , " + todo_id + " FROM " + TABLE_NAME;

        ArrayList<HashMap<String, String>> phonebook = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> persondata = new HashMap<>();
                persondata.put("id", cursor.getString(cursor.getColumnIndex("_id")));
                persondata.put("toDo", cursor.getString(cursor.getColumnIndex(todo_id)));
                phonebook.add(persondata);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return phonebook;
    }

    // update
    void update(int id, String doable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(todo_id, doable);
        db.update(TABLE_NAME, values, "_id = ? ", new String[] {
                String.valueOf(id)
        });
        db.close();
    }

    // delete
    void delete(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, " _id = ? ", new String[]{
                String.valueOf(id)
        });
        db.close();
    }
}
