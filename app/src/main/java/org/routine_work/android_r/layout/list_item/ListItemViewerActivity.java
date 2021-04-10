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
package org.routine_work.android_r.layout.list_item;

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

public class ListItemViewerActivity extends ResourceListActivity
        implements ListItemDBConstants, OnItemClickListener {

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
    private ListItemDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v(LOG_TAG, "Hello");
        Log.d(LOG_TAG, "parent => " + parent + ", view => " + view
                + ", position => " + position + ", id => " + id);

        TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
        TextView resourceIDTextView = (TextView) view.findViewById(R.id.resource_id_textview);
        String name = nameTextView.getText().toString();
        String layoutIdText = resourceIDTextView.getText().toString();
        try {
            int layoutID = Integer.parseInt(layoutIdText);
            Log.d(LOG_TAG, "clicked item. name => " + name + ", layoutID => " + layoutID);
            openListItemLayoutExample(name, layoutID);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "resourceID is not a number : " + layoutIdText);
        }
    }

    @Override
    protected void initializeListData() {
        Log.v(LOG_TAG, "Hello");

        if (dbHelper == null) {
            dbHelper = new ListItemDBHelper(this);
        }

        // init list adapter
        db = dbHelper.getReadableDatabase();
        cursor = dbHelper.selectByName(db, "");

        adapter = new SimpleCursorAdapter(this,
                R.layout.list_item_list_item, cursor, MAPPING_FROM, MAPPING_TO);
        setListAdapter(adapter);

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void finalizeListData() {
        Log.v(LOG_TAG, "Hello");

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        if (db != null) {
            db.close();
            db = null;
        }

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void updateListData() {
        Log.v(LOG_TAG, "Hello");

        // update ListView
        if (db != null) {
            TextView searchWordTextView = (TextView) findViewById(R.id.search_word_textview);
            String searchWord = searchWordTextView.getText().toString();

            Cursor newCursor = dbHelper.selectByName(db, searchWord);
            adapter.changeCursor(newCursor);
            cursor.close();
            cursor = newCursor;
        }

        Log.v(LOG_TAG, "Bye");
    }

    private void openListItemLayoutExample(String layoutName, int layoutID) {
        Intent listItemExampleIntent;
        if (layoutName.contains("_expandable_")) {
            listItemExampleIntent = new Intent(this, ExpandableListItemExampleActivity.class);
        } else {
            listItemExampleIntent = new Intent(this, ListItemExampleActivity.class);
        }
        listItemExampleIntent.putExtra(ListItemExampleConstants.EXTRA_LIST_ITEM_LAYOUT_NAME, layoutName);
        listItemExampleIntent.putExtra(ListItemExampleConstants.EXTRA_LIST_ITEM_LAYOUT_ID, layoutID);
        startActivity(listItemExampleIntent);
    }

    @Override
    protected String getResourceType() {
        return "layout";
    }
}
