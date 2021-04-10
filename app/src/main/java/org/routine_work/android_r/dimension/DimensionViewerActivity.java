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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.routine_work.android_r.R;
import org.routine_work.android_r.ResourceListActivity;
import org.routine_work.utils.Log;

public class DimensionViewerActivity extends ResourceListActivity
        implements DimensionDBConstants, SimpleCursorAdapter.ViewBinder {
    private static final String LOG_TAG = "android.R";
    private static final String[] MAPPING_FROM = new String[]
            {
                    Columns.NAME,
                    Columns.RESOURCE_ID,
                    Columns.RESOURCE_ID,
            };
    private static final int[] MAPPING_TO = new int[]
            {
                    R.id.dimension_name_textview,
                    R.id.dimension_value_textview,
                    R.id.dimension_sample_view,
            };
    private DimensionDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        Log.v(LOG_TAG, "Hello view => " + view + ", columnIndex => " + columnIndex);
        if (view instanceof TextView) {
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
        } else {
            float valueInPixel = 0.0f;
            int resourceID = cursor.getInt(columnIndex);
            String textValue = getString(resourceID);
            if (textValue.endsWith("dip")) {
                String numberText = textValue.substring(0, textValue.length() - 3);
                float dipValue = Float.parseFloat(numberText);
                valueInPixel = dipValue * getResources().getDisplayMetrics().density;
                Log.d(LOG_TAG, "binder textValue => " + textValue + ", dipValue =>" + dipValue);

            } else if (textValue.endsWith("px")) {
                String valueInString = textValue.substring(0, textValue.length() - 2);
                valueInPixel = Float.parseFloat(valueInString);
            } else if (textValue.endsWith("%")) {
                String valueInString = textValue.substring(0, textValue.length() - 1);
                float valueInPercent = Float.parseFloat(valueInString);
                int displayWidth = getResources().getDisplayMetrics().widthPixels;
                valueInPixel = valueInPercent / 100.0f * displayWidth;
            }
            Log.d(LOG_TAG, "binder valueInPixel =>" + valueInPixel);
            Animation scaleAnimation = new ScaleAnimation(0.0f, valueInPixel, 0.0f, 1.0f, 0.0f, 0.0f);
            scaleAnimation.setStartOffset(500);
            scaleAnimation.setDuration(1000);
            scaleAnimation.setFillBefore(true);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setFillEnabled(true);
            scaleAnimation.setInterpolator(new DecelerateInterpolator());
            view.startAnimation(scaleAnimation);
        }

        return true;
    }

    @Override
    protected void updateListData() {
        if (db != null) {
            TextView searchWordTextView = (TextView) findViewById(R.id.search_word_textview);
            String searchWord = searchWordTextView.getText().toString().trim();

            Cursor newCursor = dbHelper.getDimensions(db, searchWord);
            adapter.changeCursor(newCursor);
            cursor.close();
            cursor = newCursor;
        }
    }

    @Override
    protected void initializeListData() {
        if (dbHelper == null) {
            dbHelper = new DimensionDBHelper(this);
        }

        // init db
        db = dbHelper.getReadableDatabase();
        cursor = dbHelper.getDimensions(db, "");

        // init list adapter
        adapter = new SimpleCursorAdapter(this,
                R.layout.dimension_list_item, cursor, MAPPING_FROM, MAPPING_TO);
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
    protected String getResourceType() {
        return "dimen";
    }
}
