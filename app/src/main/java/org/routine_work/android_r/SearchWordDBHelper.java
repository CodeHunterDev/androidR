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
package org.routine_work.android_r;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.routine_work.utils.Log;

/**
 * @author sawai
 */
public class SearchWordDBHelper extends SQLiteOpenHelper implements SearchWordDBConstants {
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
            + "  " + Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
            + ", " + Columns.RESOURCE_TYPE + " TEXT"
            + ", " + Columns.SEARCH_WORD + " TEXT"
            + ", " + Columns.COUNT + " INTEGER"
            + ");";
    static final String DROP_TABLE_SQL = "DROP TABLE " + TABLE_NAME + ";";
    private static final String LOG_TAG = "android.R";
    private Context context;

    public SearchWordDBHelper(Context context) {
        // create database on internal storage
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(LOG_TAG, "Hello");
        Log.d(LOG_TAG, "CREATE_TABLE_SQL => " + CREATE_TABLE_SQL);
        db.execSQL(CREATE_TABLE_SQL);
        initializeData(db);
        Log.v(LOG_TAG, "Bye");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_TAG, "Hello");
        Log.d(LOG_TAG, "DROP_TABLE_SQL => " + DROP_TABLE_SQL);
        db.execSQL(DROP_TABLE_SQL);
        onCreate(db);
        Log.v(LOG_TAG, "Bye");
    }

    public Cursor selectSearchWords(SQLiteDatabase db, String resourceType, CharSequence searchWord) {
        Log.v(LOG_TAG, "Hello");
        Log.i(LOG_TAG, "resourceType => " + resourceType + ", searchWord => " + searchWord);

        String[] columns = new String[]
                {
                        Columns.ID,
                        Columns.RESOURCE_TYPE,
                        Columns.SEARCH_WORD,
                        Columns.COUNT,
                };
        String where = Columns.RESOURCE_TYPE + " = ? "
                + "AND "
                + Columns.SEARCH_WORD + " like ? ";
        String[] whereArgs = new String[]
                {
                        resourceType,
                        searchWord + "%"
                };

        Cursor cursor = db.query(TABLE_NAME,
                columns, where, whereArgs,
                null, null, Columns.COUNT + " DESC");

        Log.v(LOG_TAG, "Bye");
        return cursor;
    }

    public void insertOrUpdateSearchWord(SQLiteDatabase db, String resourceType, String searchWord) {
        Log.v(LOG_TAG, "Hello");
        Log.i(LOG_TAG, "insert resourceType => " + resourceType + ", searchWord => " + searchWord);
        db.beginTransaction();
        try {
            String[] columns = new String[]
                    {
                            Columns.ID,
                            Columns.SEARCH_WORD,
                            Columns.COUNT,
                    };
            String where = Columns.RESOURCE_TYPE + " = ? "
                    + "AND "
                    + Columns.SEARCH_WORD + " = ?";
            String[] whereArgs = new String[]
                    {
                            resourceType,
                            searchWord,
                    };

            Cursor cursor = db.query(TABLE_NAME,
                    columns, where, whereArgs,
                    null, null, null);
            long id = -1;
            int count = 0;
            try {
                if (cursor.moveToFirst()) { // already exist
                    int idIndex = cursor.getColumnIndex(Columns.ID);
                    int countIndex = cursor.getColumnIndex(Columns.COUNT);
                    id = cursor.getLong(idIndex);
                    count = cursor.getInt(countIndex);
                    Log.d(LOG_TAG, searchWord + " is already exist : id => " + id);
                    Log.d(LOG_TAG, searchWord + " : count => " + count);
                }

            } finally {
                cursor.close();
            }

            if (id == -1) { // insert
                ContentValues values = new ContentValues();
                values.put(Columns.RESOURCE_TYPE, resourceType);
                values.put(Columns.SEARCH_WORD, searchWord);
                values.put(Columns.COUNT, 1);
                db.insert(TABLE_NAME, null, values);
            } else { // update
                ContentValues values = new ContentValues();
                values.put(Columns.COUNT, count + 1);
                db.update(TABLE_NAME, values, Columns.ID + " = ?",
                        new String[]
                                {
                                        String.valueOf(id)
                                });
            }

            db.setTransactionSuccessful();
        } catch (IllegalArgumentException ex) {
            Log.e(LOG_TAG, "insert or update data failed.", ex);
        } finally {
            db.endTransaction();
        }

        Log.v(LOG_TAG, "Bye");
    }

    private void initializeData(SQLiteDatabase db) {
        Resources resources = context.getResources();
        String[] initialData = resources.getStringArray(R.array.search_word_db_initial_data);

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (String data : initialData) {
                String[] s = data.split(",");
                if (s != null && s.length == 2) {
                    String resourceType = s[0];
                    String searchWord = s[1];

                    values.clear();
                    values.put(Columns.RESOURCE_TYPE, resourceType);
                    values.put(Columns.SEARCH_WORD, searchWord);
                    values.put(Columns.COUNT, 1);
                    db.insert(TABLE_NAME, null, values);
                }

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(LOG_TAG, "The data initializing failed.", e);
        } finally {
            db.endTransaction();
        }
    }
}
