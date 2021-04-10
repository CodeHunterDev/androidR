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
package org.routine_work.android_r.style.theme;

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
import android.widget.Toast;

import org.routine_work.android_r.R;
import org.routine_work.android_r.ResourceListActivity;
import org.routine_work.utils.Log;

public class ThemeViewerActivity extends ResourceListActivity
        implements ThemeDBConstants, OnItemClickListener {

    private static final String LOG_TAG = "android.R";
    private static final String[] MAPPING_FROM = new String[]
            {
                    Columns.NAME,
                    Columns.RESOURCE_ID,
            };
    private static final int[] MAPPING_TO = new int[]
            {
                    R.id.name_textview,
                    R.id.resource_id_textview,
            };
    private ThemeDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private String THEME_NO_DISPLAY_NAME = "Theme_NoDisplay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG, "item clicked. parent => " + parent + ", view => " + view + ", position => " + position + ", id => " + id);

        TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
        TextView resourceIDTextView = (TextView) view.findViewById(R.id.resource_id_textview);
        String themeName = nameTextView.getText().toString();
        String themeIDText = resourceIDTextView.getText().toString();
        try {
            int themeID = Integer.parseInt(themeIDText);
            openThemeExampleActivity(themeName, themeID);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "themeID is not a number : " + themeIDText);
        }
    }

    @Override
    protected void initializeListData() {
        if (dbHelper == null) {
            dbHelper = new ThemeDBHelper(this);
        }

        // init db
        db = dbHelper.getReadableDatabase();
        cursor = dbHelper.selectTheme(db, "");

        // init list adapter
        adapter = new SimpleCursorAdapter(this,
                R.layout.theme_list_item, cursor, MAPPING_FROM, MAPPING_TO);
        setListAdapter(adapter);
    }

    @Override
    protected void finalizeListData() {
        // finalize db
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

            Cursor newCursor = dbHelper.selectTheme(db, searchWord);
            adapter.changeCursor(newCursor);
            cursor.close();
            cursor = newCursor;
        }
    }

    private void openThemeExampleActivity(String themeName, int themeID) {
        if (THEME_NO_DISPLAY_NAME.equals(themeName)) {
            Toast.makeText(this, R.string.not_supported, Toast.LENGTH_SHORT).show();
        } else {
            Intent themeExampleIntent = new Intent(this, ThemeExampleActivity.class);
            themeExampleIntent.putExtra(ThemeExampleActivity.EXTRA_KEY_THEME_NAME, themeName);
            themeExampleIntent.putExtra(ThemeExampleActivity.EXTRA_KEY_THEME_ID, themeID);
            startActivity(themeExampleIntent);
        }
    }

    @Override
    protected String getResourceType() {
        return "style";
    }
}
