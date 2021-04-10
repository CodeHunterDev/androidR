/*
 *  The MIT License
 *
 *  Copyright 2011-2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
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
package org.routine_work.android_r.array;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.routine_work.android_r.R;
import org.routine_work.android_r.ResourceListActivity;
import org.routine_work.utils.Log;

public class ArrayViewerActivity extends ResourceListActivity
        implements ArrayDBConstants, OnItemClickListener {

    public static final String LOG_TAG = "android.R";
    private ArrayDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void initializeListData() {
        if (dbHelper == null) {
            dbHelper = new ArrayDBHelper(this);
        }

        db = dbHelper.getReadableDatabase();

        // init list adapter
        cursor = dbHelper.selectByName(db, "");

        final String[] from = new String[]
                {
                        Columns.NAME,
                };
        final int[] to = new int[]
                {
                        android.R.id.text1,
                };

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);
        setListAdapter(adapter);
    }

    @Override
    protected void finalizeListData() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        if (db != null) {
            db.close();
            db = null;
        }
    }

    @Override
    protected void updateListData() {
        // update ListView
        if (db != null) {
            TextView searchWordTextView = (TextView) findViewById(R.id.search_word_textview);
            String searchWord = searchWordTextView.getText().toString();
            Log.d(LOG_TAG, "searchWord => " + searchWord);

            Cursor newCursor = dbHelper.selectByName(db, searchWord);
            adapter.changeCursor(newCursor);
            cursor.close();
            cursor = newCursor;
        }
    }

    @Override
    protected String getResourceType() {
        return "array";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(LOG_TAG, "item click. parent => " + parent + ", view => " + view + ", position => " + position + ", id => " + id);
        TextView nameTextView = (TextView) view;
        String arrayName = nameTextView.getText().toString();

        Intent intent = new Intent(this, ArrayResourceListActivity.class);
        intent.putExtra(ArrayResourceListActivity.EXTRA_ARRAY_FIELD_NAME, arrayName);
        startActivity(intent);
    }
}
