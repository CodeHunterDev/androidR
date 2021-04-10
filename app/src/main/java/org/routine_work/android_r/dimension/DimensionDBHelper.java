/*
 *  The MIT License
 *
 *  Copyright 2011 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.routine_work.android_r.dimension;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.routine_work.utils.Log;

import java.lang.reflect.Field;

/**
 * @author sawai
 */
public class DimensionDBHelper extends SQLiteOpenHelper
        implements DimensionDBConstants {
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
            + "  " + Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
            + ", " + Columns.NAME + " TEXT"
            + ", " + Columns.RESOURCE_ID + " INTEGER"
            + ");";
    static final String DROP_TABLE_SQL = "DROP TABLE " + TABLE_NAME + ";";
    private static final String LOG_TAG = "android.R";

    public DimensionDBHelper(Context context) {
        super(context, null, null, DB_VERSION);
    }

    public Cursor getDimensions(SQLiteDatabase db, String searchWord) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]
                        {
                                Columns.ID,
                                Columns.NAME,
                                Columns.RESOURCE_ID,
                        },
                Columns.NAME + " like ?",
                new String[]
                        {
                                '%' + searchWord + '%'
                        },
                null, null, Columns.NAME + " ASC");

        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "CREATE_TABLE_SQL => " + CREATE_TABLE_SQL);
        db.execSQL(CREATE_TABLE_SQL);
        initializeStrings(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "DROP_TABLE_SQL => " + DROP_TABLE_SQL);
        db.execSQL(DROP_TABLE_SQL);
        onCreate(db);
    }

    private void initializeStrings(SQLiteDatabase db) {
        Log.v(LOG_TAG, "Hello");

        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            Class clazz = android.R.dimen.class;
            for (Field field : clazz.getDeclaredFields()) {
                String name;
                int resourceID;
                try {
                    name = field.getName();
                    resourceID = field.getInt(null);

                    values.clear();
                    values.put(Columns.NAME, name);
                    values.put(Columns.RESOURCE_ID, resourceID);
                    db.insert(TABLE_NAME, null, values);
                } catch (IllegalArgumentException ex) {
                    Log.e(LOG_TAG, "insert data failed.", ex);
                } catch (IllegalAccessException ex) {
                    Log.e(LOG_TAG, "insert data denied.", ex);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.v(LOG_TAG, "Bye");
    }
}
