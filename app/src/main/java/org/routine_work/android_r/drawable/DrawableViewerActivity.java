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
package org.routine_work.android_r.drawable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.routine_work.android_r.DashboardActivity;
import org.routine_work.android_r.R;
import org.routine_work.android_r.ResourceListActivity;
import org.routine_work.utils.Log;
import org.routine_work.utils.SystemResourceUtils;

public class DrawableViewerActivity extends ResourceListActivity
        implements DrawableDBConstants, SimpleCursorAdapter.ViewBinder,
        OnItemClickListener, DialogInterface.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "android.R";
    private static final String[] MAPPING_FROM = new String[]
            {
                    Columns.RESOURCE_ID,
                    Columns.NAME,
                    Columns.RESOURCE_ID,
            };
    private static final int[] MAPPING_TO = new int[]
            {
                    R.id.drawable_imageview,
                    R.id.drawable_name_textview,
                    R.id.drawable_id_textview,
            };
    private static final int DIALOG_ID_BACKGROUND_COLOR_PICKER = 1;
    // background color chooser dialog
    BackgroundColorListAdapterFactory backgroundColorListAdapterFactory;
    // preferences
    private SharedPreferences sharedPreferences;
    // preferences : background color
    private String bgColorPreferenceKey;
    private String bgColorDefaultValue;
    // db
    private DrawableDBHelper dbHelper;
    private DrawableCursorAdapter adapter;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == cursor.getColumnIndex(Columns.RESOURCE_ID)
                && (view instanceof ImageView)) {
            int resourceID = cursor.getInt(columnIndex);
            ImageView imageView = (ImageView) view;
            imageView.setImageResource(resourceID);

        } else {
            String textValue = cursor.getString(columnIndex);
//			Log.d(LOG_TAG, "resourceName=> " + cursor.getString(columnIndex));
            TextView textView = (TextView) view;
            textView.setText(textValue);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.drawable_viewer_menu, menu);
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        int itemId = item.getItemId();
        if (itemId == R.id.background_color_menuitem) {
            //showBackgroundColorListDialog();
            showDialog(DIALOG_ID_BACKGROUND_COLOR_PICKER);
        } else if (itemId == SystemResourceUtils.getResourceId("android.R$id.home")) {
            DashboardActivity.goDashboardActivity(this);
        } else {
            result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    /**
     * DialogInterface.OnClickListener
     *
     * @param dialog
     * @param index
     */
    @Override
    public void onClick(DialogInterface dialog, int index) {
        Log.v(LOG_TAG, "Hello");

        String selectedColorValue = backgroundColorListAdapterFactory.getColorValueAt(index);
        Log.d(LOG_TAG, "selectedColorValue => " + selectedColorValue);
        if (selectedColorValue != null) {
            Editor editor = sharedPreferences.edit();
            editor.putString(bgColorPreferenceKey, selectedColorValue);
            editor.commit();
        }
        dialog.dismiss();

        Log.v(LOG_TAG, "Bye");
    }

    /**
     * OnItemClickListener
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    @Override
    public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
        Log.v(LOG_TAG, "Hello");

        TextView drawableIDTextView = (TextView) view.findViewById(R.id.drawable_id_textview);
        String drawableIDText = drawableIDTextView.getText().toString();
        int drawableID = Integer.parseInt(drawableIDText);

        TextView drawableNameTextView = (TextView) view.findViewById(R.id.drawable_name_textview);
        String drawableName = drawableNameTextView.getText().toString();

        Log.d(LOG_TAG, "clicked drawableID => " + drawableID);
        Log.d(LOG_TAG, "clicked drawableName => " + drawableName);

        Intent intent = new Intent(this, DrawableZoomActivity.class);
        intent.putExtra(DrawableZoomActivity.EXTRA_DRAWABLE_ID, drawableID);
        intent.putExtra(DrawableZoomActivity.EXTRA_DRAWABLE_NAME, drawableName);
        startActivity(intent);

        Log.v(LOG_TAG, "Bye");
    }

    /**
     * SharedPreferences.OnSharedPreferenceChangeListener
     *
     * @param prefs
     * @param prefKey
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String prefKey) {
        Log.v(LOG_TAG, "Hello ");
        Log.d(LOG_TAG, "prefKey => " + prefKey);

        if (bgColorPreferenceKey.equals(prefKey)) {
            updateBackgroundColor();
            updateListData();
        }

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for BackgroundColorList Dialog
        backgroundColorListAdapterFactory = new BackgroundColorListAdapterFactory(this);

        // init preference and callback
        String preferenceName = getPackageName() + "_preferences";
        sharedPreferences = getSharedPreferences(preferenceName, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // init background color
        bgColorPreferenceKey = getString(R.string.drawable_background_color_key);
        bgColorDefaultValue = getString(R.string.drawable_background_color_default_value);

        // set drawable background color
        updateBackgroundColor();

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        Log.v(LOG_TAG, "Hello");

        switch (id) {
            case DIALOG_ID_BACKGROUND_COLOR_PICKER:
                dialog = createBackgroundColorListDialog();
                break;
        }

        Log.d(LOG_TAG, "dialog => " + dialog);
        Log.v(LOG_TAG, "Bye");
        return dialog;
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected void updateListData() {
        Log.v(LOG_TAG, "Hello");

        // update ListView
        TextView searchWordTextView = (TextView) findViewById(R.id.search_word_textview);
        String searchWord = searchWordTextView.getText().toString();

        Cursor newCursor = dbHelper.getDrawables(db, searchWord);
        adapter.changeCursor(newCursor);
        cursor.close();
        cursor = newCursor;

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void initializeListData() {
        Log.v(LOG_TAG, "Hello");

        if (dbHelper == null) {
            dbHelper = new DrawableDBHelper(this);
        }

        // init list adapter
        db = dbHelper.getReadableDatabase();
        cursor = dbHelper.getDrawables(db, "");

        adapter = new DrawableCursorAdapter(this,
                R.layout.drawable_list_item, cursor, MAPPING_FROM, MAPPING_TO);
        adapter.setViewBinder(this);
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
    protected String getResourceType() {
        return "drawable";
    }

    private Dialog createBackgroundColorListDialog() {
        String bgColorString = sharedPreferences.getString(bgColorPreferenceKey, bgColorDefaultValue);
        Log.d(LOG_TAG, "bgColorString => " + bgColorDefaultValue);
        int bgColorValueIndex = backgroundColorListAdapterFactory.getIndexByColorValue(bgColorString);
        Log.d(LOG_TAG, "bgColorValueIndex => " + bgColorValueIndex);

        ListAdapter listAdapter = backgroundColorListAdapterFactory.createBackgroundColorListAdapter();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.background_color);
        builder.setSingleChoiceItems(listAdapter, bgColorValueIndex, this);

        return builder.create();
    }

    private void updateBackgroundColor() {
        String bgColorValue = sharedPreferences.getString(bgColorPreferenceKey, bgColorDefaultValue);
        Log.d(LOG_TAG, "bgColorValue => " + bgColorValue);
        int bgColor = Color.parseColor(bgColorValue);
        Log.d(LOG_TAG, "bgColor => " + bgColor);
        adapter.setBackgroundColor(bgColor);
    }
}
