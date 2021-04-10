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
package org.routine_work.android_r.string;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.routine_work.android_r.R;
import org.routine_work.android_r.ResourceListActivity;

public class StringViewerActivity extends ResourceListActivity
        implements StringDBConstants, SimpleCursorAdapter.ViewBinder {
    private StringDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    /**
     * Override SimpleCursorAdapter.ViewBinder
     *
     * @param view
     * @param cursor
     * @param columnIndex
     * @return
     */
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex(Columns.RESOURCE_ID)) {
            int resourceID = cursor.getInt(columnIndex);
            String textValue = getString(resourceID);
            TextView textView = (TextView) view;
            textView.setText(textValue);
        } else {
            String textValue = cursor.getString(columnIndex);
            TextView textView = (TextView) view;
            textView.setText(textValue);
        }

        return true;
    }

    @Override
    protected void initializeListData() {
        if (dbHelper == null) {
            dbHelper = new StringDBHelper(this);
        }

        db = dbHelper.getReadableDatabase();
        cursor = dbHelper.getStrings(db, "");

        String[] from = new String[]
                {
                        Columns.NAME,
                        Columns.RESOURCE_ID,
                };
        int[] to = new int[]
                {
                        R.id.string_name_textview,
                        R.id.string_string_textview,
                };

        adapter = new SimpleCursorAdapter(this,
                R.layout.string_list_item, cursor, from, to);
        adapter.setViewBinder(this);
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

            Cursor newCursor = dbHelper.getStrings(db, searchWord);
            adapter.changeCursor(newCursor);
            cursor.close();
            cursor = newCursor;
        }
    }

    @Override
    protected String getResourceType() {
        return "string";
    }
}
