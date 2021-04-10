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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import org.routine_work.android_r.DashboardActivity;
import org.routine_work.android_r.PreferencesActivity;
import org.routine_work.android_r.R;
import org.routine_work.utils.Log;
import org.routine_work.utils.SystemResourceUtils;

import java.lang.reflect.Field;

public class ArrayResourceListActivity extends ListActivity {

    public static final String EXTRA_ARRAY_FIELD_NAME = "EXTRA_ARRAY_FIELD_NAME";
    private static final String LOG_TAG = "android.R";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list_activity);

        Bundle extras = getIntent().getExtras();
        String fieldName = extras.getString(EXTRA_ARRAY_FIELD_NAME);

        String titleBase = getString(R.string.array_viewer_title);
        setTitle(titleBase + "." + fieldName);

        String[] stringArray = loadStringArray(fieldName);
        if (stringArray == null) {
            stringArray = new String[0];
        }

        ArrayAdapter listADapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, stringArray);
        setListAdapter(listADapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.preference_menu, menu);
        menuInflater.inflate(R.menu.quit_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Log.v(LOG_TAG, "selected item => " + item);

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.preference_menuitem:
                Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
                startActivity(preferencesIntent);
                break;
            case R.id.quit_menuitem:
                DashboardActivity.quitApplication(this);
                break;
            default:
                if (itemId == SystemResourceUtils.getResourceId("android.R$id.home")) {
                    DashboardActivity.goDashboardActivity(this);
                } else {
                    result = super.onOptionsItemSelected(item);
                }
        }

        return result;
    }

    private String[] loadStringArray(String fieldName) {
        String[] result = null;
        try {
            Field field = android.R.array.class.getField(fieldName);
            int resourceID = field.getInt(null);
            result = getResources().getStringArray(resourceID);
        } catch (NoSuchFieldException ex) {
            Log.e(LOG_TAG, "loadStringArray() Failed.", ex);
        } catch (SecurityException ex) {
            Log.e(LOG_TAG, "loadStringArray() Failed.", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(LOG_TAG, "loadStringArray() Failed.", ex);
        } catch (IllegalAccessException ex) {
            Log.e(LOG_TAG, "loadStringArray() Failed.", ex);
        }

        return result;
    }
}
